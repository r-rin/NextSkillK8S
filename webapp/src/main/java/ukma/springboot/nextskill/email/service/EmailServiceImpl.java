package ukma.springboot.nextskill.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ukma.springboot.nextskill.email.EmailService;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${email.service.url}")
    private String emailServiceUrl;

    private final RestTemplate restTemplate;

    @Override
    public void sendEmail(String to, String subject, String text) {
        Map<String, String> payload = new HashMap<>();
        payload.put("to", to);
        payload.put("subject", subject);
        payload.put("text", text);
        restTemplate.postForEntity(emailServiceUrl + "/api/email/send", payload, String.class);
    }
}
