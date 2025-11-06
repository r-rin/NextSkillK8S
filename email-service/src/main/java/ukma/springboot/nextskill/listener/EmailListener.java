package ukma.springboot.nextskill.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ukma.springboot.nextskill.common.dto.views.EmailRequest;
import ukma.springboot.nextskill.common.messaging.JmsDestinations;
import ukma.springboot.nextskill.service.EmailService;

@Component
@RequiredArgsConstructor
public class EmailListener {

    private final EmailService emailService;

    @JmsListener(
        destination = JmsDestinations.SEND_EMAIL_QUEUE,
        containerFactory = "queueListenerFactory",
        selector = "emailPriority = 'HIGH'"
    )
    public void receiveSendHighPriorityEmailMessage(EmailRequest request) {
        System.out.println("Received Send High Priority Email Message: " + request);
        emailService.sendEmail(request.getTo(), request.getSubject(), request.getText());
    }

    @JmsListener(
        destination = JmsDestinations.SEND_EMAIL_QUEUE,
        containerFactory = "queueListenerFactory",
        selector = "emailPriority = 'LOW'"
    )
    public void receiveSendLowPriorityEmailMessage(EmailRequest request) {
        System.out.println("Ignored Send Low Priority Email Message: " + request);
    }

    @JmsListener(
        destination = JmsDestinations.USER_CREATED_TOPIC,
        containerFactory = "topicListenerFactory"
    )
    public void receiveUserCreatedMessage(String userEmail) {
        System.out.println("Received User Created Message: " + userEmail);
        emailService.sendWelcomeEmail(userEmail);
    }
}
