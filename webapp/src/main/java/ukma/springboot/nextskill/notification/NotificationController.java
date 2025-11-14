package ukma.springboot.nextskill.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ukma.grpc.notification.NotificationResponse;
import ukma.grpc.notification.StatusResponse;
import ukma.springboot.nextskill.grpc.NotificationGrpcClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationGrpcClient notificationGrpcClient;

    /**
     * Stream notifications for a user (synchronous)
     */
    @GetMapping("/stream/{userId}")
    public ResponseEntity<List<NotificationResponse>> streamNotifications(
            @PathVariable String userId,
            @RequestParam(defaultValue = "EMAIL") List<String> types,
            @RequestParam(defaultValue = "5") int maxCount) {

        log.info("REST API: Streaming notifications for user: {}", userId);
        
        List<NotificationResponse> notifications = notificationGrpcClient.streamNotifications(
                userId, types, maxCount);
        
        return ResponseEntity.ok(notifications);
    }

    /**
     * Stream notifications for a user (asynchronous)
     */
    @GetMapping("/stream-async/{userId}")
    public CompletableFuture<ResponseEntity<List<NotificationResponse>>> streamNotificationsAsync(
            @PathVariable String userId,
            @RequestParam(defaultValue = "EMAIL") List<String> types,
            @RequestParam(defaultValue = "5") int maxCount) {

        log.info("REST API: Async streaming notifications for user: {}", userId);
        
        return notificationGrpcClient.streamNotificationsAsync(userId, types, maxCount)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Send a single notification
     */
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(
            @RequestBody SendNotificationDto request) {

        log.info("REST API: Sending notification for user: {}", request.getUserId());
        
        NotificationResponse response = notificationGrpcClient.sendNotification(
                request.getUserId(),
                request.getNotificationType(),
                request.getSubject(),
                request.getMessage(),
                request.getPriority()
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get notification status
     */
    @GetMapping("/status/{notificationId}")
    public ResponseEntity<StatusResponse> getNotificationStatus(@PathVariable String notificationId) {
        log.info("REST API: Getting status for notification: {}", notificationId);
        
        StatusResponse response = notificationGrpcClient.getNotificationStatus(notificationId);
        
        return ResponseEntity.ok(response);
    }
}
