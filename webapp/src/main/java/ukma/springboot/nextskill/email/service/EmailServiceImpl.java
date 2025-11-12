package ukma.springboot.nextskill.email.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ukma.springboot.nextskill.common.dto.views.EmailRequest;
import ukma.springboot.nextskill.common.communication.JmsDestinations;
import ukma.springboot.nextskill.email.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private final JmsTemplate queueJmsTemplate;

    public EmailServiceImpl(@Qualifier("jmsQueueTemplate") JmsTemplate jmsTemplate) {
        this.queueJmsTemplate = jmsTemplate;
    }

    @Override
    public void sendHighPriorityEmail(String to, String subject, String text) {
        sendEmail(to, subject, text, "HIGH");
    }

    @Override
    public void sendLowPriorityEmail(String to, String subject, String text) {
        sendEmail(to, subject, text, "LOW");
    }

    public void sendEmail(String to, String subject, String text, String emailPriority) {
        var request = EmailRequest.builder()
            .subject(subject)
            .text(text)
            .to(to)
            .build();

        try {
            queueJmsTemplate.convertAndSend(JmsDestinations.SEND_EMAIL_QUEUE, request, message -> {
                message.setStringProperty("emailPriority", emailPriority);
                return message;
            });
            System.out.println("Create send email message: " + request);
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }
}
