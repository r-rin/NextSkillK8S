package ukma.springboot.nextskill.notification;

import lombok.Data;

@Data
public class SendNotificationDto {
    private String userId;
    private String notificationType;
    private String subject;
    private String message;
    private String priority;
}
