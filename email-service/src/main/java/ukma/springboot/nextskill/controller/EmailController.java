package ukma.springboot.nextskill.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ukma.springboot.nextskill.common.dto.views.EmailRequest;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Deprecated
public class EmailController {

    private final JmsTemplate jmsTemplate;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        jmsTemplate.convertAndSend("sendEmailQueue", request);
        return ResponseEntity.ok("Email sent");
    }
}
