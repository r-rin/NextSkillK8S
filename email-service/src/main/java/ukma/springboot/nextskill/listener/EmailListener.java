package ukma.springboot.nextskill.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ukma.springboot.nextskill.common.dto.EmailRequest;
import ukma.springboot.nextskill.service.EmailService;

@Component
@RequiredArgsConstructor
public class EmailListener {

    private final EmailService emailService;

    @JmsListener(destination = "sendEmailQueue")
    public void receiveEmail(EmailRequest request) {
        System.out.println("Received email: " + request);
        emailService.sendEmail(request.getTo(), request.getSubject(), request.getText());
    }
}
