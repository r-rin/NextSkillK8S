package ukma.springboot.nextskill.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ukma.grpc.notification.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NotificationGrpcClient {

    @GrpcClient("notification-service")
    private NotificationServiceGrpc.NotificationServiceBlockingStub notificationServiceStub;

    /**
     * Stream notifications from the email-service with error handling
     */
    public List<NotificationResponse> streamNotifications(String userId, List<String> notificationTypes, int maxCount) {
        log.info("Requesting notification stream for user: {}", userId);
        List<NotificationResponse> notifications = new ArrayList<>();

        try {
            NotificationRequest request = NotificationRequest.newBuilder()
                    .setUserId(userId)
                    .addAllNotificationTypes(notificationTypes)
                    .setMaxCount(maxCount)
                    .build();

            Iterator<NotificationResponse> responseIterator = notificationServiceStub
                    .withDeadlineAfter(30, TimeUnit.SECONDS)
                    .streamNotifications(request);

            while (responseIterator.hasNext()) {
                try {
                    NotificationResponse notification = responseIterator.next();
                    notifications.add(notification);
                    log.info("Received notification: {} - {}", notification.getNotificationId(), notification.getSubject());

                    // Handle notifications with errors
                    if (notification.hasError()) {
                        log.warn("Notification {} has error: {} - {}",
                                notification.getNotificationId(),
                                notification.getError().getErrorCode(),
                                notification.getError().getErrorMessage());
                    }
                } catch (StatusRuntimeException e) {
                    handleGrpcError("streaming notification", e);
                    break;
                }
            }

            log.info("Successfully received {} notifications for user: {}", notifications.size(), userId);

        } catch (StatusRuntimeException e) {
            handleGrpcError("initiating notification stream", e);
        } catch (Exception e) {
            log.error("Unexpected error while streaming notifications", e);
        }

        return notifications;
    }

    /**
     * Stream notifications asynchronously
     */
    public CompletableFuture<List<NotificationResponse>> streamNotificationsAsync(String userId, List<String> notificationTypes, int maxCount) {
        return CompletableFuture.supplyAsync(() -> streamNotifications(userId, notificationTypes, maxCount));
    }

    /**
     * Send a single notification
     */
    public NotificationResponse sendNotification(String userId, String notificationType, String subject, String message, String priority) {
        log.info("Sending notification for user: {}", userId);

        try {
            SendNotificationRequest request = SendNotificationRequest.newBuilder()
                    .setUserId(userId)
                    .setNotificationType(notificationType)
                    .setSubject(subject)
                    .setMessage(message)
                    .setPriority(priority)
                    .build();

            NotificationResponse response = notificationServiceStub
                    .withDeadlineAfter(10, TimeUnit.SECONDS)
                    .sendNotification(request);

            if (response.hasError()) {
                log.warn("Notification sent with error: {} - {}",
                        response.getError().getErrorCode(),
                        response.getError().getErrorMessage());
            } else {
                log.info("Successfully sent notification: {}", response.getNotificationId());
            }

            return response;

        } catch (StatusRuntimeException e) {
            handleGrpcError("sending notification", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while sending notification", e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }

    /**
     * Get notification status
     */
    public StatusResponse getNotificationStatus(String notificationId) {
        log.info("Getting status for notification: {}", notificationId);

        try {
            StatusRequest request = StatusRequest.newBuilder()
                    .setNotificationId(notificationId)
                    .build();

            StatusResponse response = notificationServiceStub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .getNotificationStatus(request);

            log.info("Notification {} status: {}", notificationId, response.getStatus());
            return response;

        } catch (StatusRuntimeException e) {
            handleGrpcError("getting notification status", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while getting notification status", e);
            throw new RuntimeException("Failed to get notification status", e);
        }
    }

    /**
     * Handle gRPC errors with detailed logging
     */
    private void handleGrpcError(String operation, StatusRuntimeException e) {
        Status status = e.getStatus();
        String description = status.getDescription();

        switch (status.getCode()) {
            case UNAVAILABLE:
                log.error("gRPC service unavailable while {}: {}", operation, description);
                break;
            case UNAUTHENTICATED:
                log.error("Authentication failed while {}: {}", operation, description);
                break;
            case PERMISSION_DENIED:
                log.error("Permission denied while {}: {}", operation, description);
                break;
            case INVALID_ARGUMENT:
                log.error("Invalid argument while {}: {}", operation, description);
                break;
            case NOT_FOUND:
                log.error("Resource not found while {}: {}", operation, description);
                break;
            case DEADLINE_EXCEEDED:
                log.error("Deadline exceeded while {}: {}", operation, description);
                break;
            case INTERNAL:
                log.error("Internal server error while {}: {}", operation, description);
                break;
            default:
                log.error("gRPC error while {}: {} - {}", operation, status.getCode(), description);
        }
    }
}
