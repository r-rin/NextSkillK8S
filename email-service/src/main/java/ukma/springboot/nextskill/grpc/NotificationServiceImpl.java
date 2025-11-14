package ukma.springboot.nextskill.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ukma.grpc.notification.*;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@GrpcService
public class NotificationServiceImpl extends NotificationServiceGrpc.NotificationServiceImplBase {

    private final ConcurrentHashMap<String, NotificationResponse> notificationStore = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Override
    public void streamNotifications(NotificationRequest request, StreamObserver<NotificationResponse> responseObserver) {
        log.info("Starting notification stream for user: {}", request.getUserId());

        try {
            // Validate request
            if (request.getUserId() == null || request.getUserId().isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("User ID cannot be empty")
                        .asRuntimeException());
                return;
            }

            int maxCount = request.getMaxCount() > 0 ? request.getMaxCount() : 10;
            int[] count = {0};

            // Simulate streaming notifications with a scheduled task
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    if (count[0] >= maxCount) {
                        responseObserver.onCompleted();
                        log.info("Completed streaming {} notifications for user: {}", count[0], request.getUserId());
                        return;
                    }

                    String notificationId = UUID.randomUUID().toString();
                    NotificationResponse notification = NotificationResponse.newBuilder()
                            .setNotificationId(notificationId)
                            .setUserId(request.getUserId())
                            .setNotificationType("EMAIL")
                            .setSubject("Notification #" + (count[0] + 1))
                            .setMessage("This is notification message #" + (count[0] + 1))
                            .setPriority("MEDIUM")
                            .setTimestamp(Instant.now().toEpochMilli())
                            .setStatus("SENT")
                            .build();

                    notificationStore.put(notificationId, notification);
                    responseObserver.onNext(notification);
                    count[0]++;

                    log.debug("Streamed notification {} for user: {}", notificationId, request.getUserId());

                } catch (Exception e) {
                    log.error("Error while streaming notification", e);
                    ErrorInfo errorInfo = ErrorInfo.newBuilder()
                            .setErrorCode("STREAM_ERROR")
                            .setErrorMessage(e.getMessage())
                            .setErrorType("INTERNAL")
                            .build();

                    responseObserver.onError(Status.INTERNAL
                            .withDescription("Error streaming notification: " + e.getMessage())
                            .asRuntimeException());
                }
            }, 0, 1, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error("Error setting up notification stream", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Failed to set up notification stream")
                    .asRuntimeException());
        }
    }

    @Override
    public void sendNotification(SendNotificationRequest request, StreamObserver<NotificationResponse> responseObserver) {
        log.info("Sending notification for user: {}", request.getUserId());

        try {
            // Validate request
            if (request.getUserId() == null || request.getUserId().isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("User ID cannot be empty")
                        .asRuntimeException());
                return;
            }

            if (request.getMessage() == null || request.getMessage().isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Message cannot be empty")
                        .asRuntimeException());
                return;
            }

            String notificationId = UUID.randomUUID().toString();
            
            // Simulate notification sending with potential failure
            boolean success = Math.random() > 0.1; // 90% success rate

            NotificationResponse.Builder responseBuilder = NotificationResponse.newBuilder()
                    .setNotificationId(notificationId)
                    .setUserId(request.getUserId())
                    .setNotificationType(request.getNotificationType())
                    .setSubject(request.getSubject())
                    .setMessage(request.getMessage())
                    .setPriority(request.getPriority())
                    .setTimestamp(Instant.now().toEpochMilli());

            if (success) {
                responseBuilder.setStatus("SENT");
                log.info("Successfully sent notification {} for user: {}", notificationId, request.getUserId());
            } else {
                responseBuilder.setStatus("FAILED");
                ErrorInfo errorInfo = ErrorInfo.newBuilder()
                        .setErrorCode("SEND_FAILED")
                        .setErrorMessage("Failed to send notification due to network error")
                        .setErrorType("NETWORK")
                        .build();
                responseBuilder.setError(errorInfo);
                log.warn("Failed to send notification {} for user: {}", notificationId, request.getUserId());
            }

            NotificationResponse response = responseBuilder.build();
            notificationStore.put(notificationId, response);
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error sending notification", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error sending notification: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getNotificationStatus(StatusRequest request, StreamObserver<StatusResponse> responseObserver) {
        log.info("Getting status for notification: {}", request.getNotificationId());

        try {
            if (request.getNotificationId() == null || request.getNotificationId().isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Notification ID cannot be empty")
                        .asRuntimeException());
                return;
            }

            NotificationResponse notification = notificationStore.get(request.getNotificationId());

            if (notification == null) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Notification not found")
                        .asRuntimeException());
                return;
            }

            StatusResponse.Builder statusBuilder = StatusResponse.newBuilder()
                    .setNotificationId(notification.getNotificationId())
                    .setStatus(notification.getStatus())
                    .setLastUpdated(notification.getTimestamp());

            if (notification.hasError()) {
                statusBuilder.setError(notification.getError());
            }

            responseObserver.onNext(statusBuilder.build());
            responseObserver.onCompleted();

            log.info("Retrieved status for notification: {}", request.getNotificationId());

        } catch (Exception e) {
            log.error("Error retrieving notification status", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error retrieving notification status: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
