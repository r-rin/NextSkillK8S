package ukma.springboot.nextskill.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ukma.springboot.nextskill.common.dto.views.EmailRequest;
import ukma.springboot.nextskill.common.messaging.JmsDestinations;
import ukma.springboot.nextskill.email.EmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JmsTemplate jmsTemplate;

    @Override
    public void sendEmail(String to, String subject, String text) {
        var request = EmailRequest.builder()
            .subject(subject)
            .text(text)
            .to(to)
            .build();

        try {
            jmsTemplate.convertAndSend(JmsDestinations.SEND_EMAIL_QUEUE, request);
            System.out.println("Create send email message: " + request);
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }
}
