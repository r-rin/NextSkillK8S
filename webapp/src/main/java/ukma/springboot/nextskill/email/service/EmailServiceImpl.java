package ukma.springboot.nextskill.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ukma.springboot.nextskill.email.EmailService;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${email.service.url}")
    private String emailServiceUrl;

    @Value("${email.service.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    @Override
    public void sendEmail(String to, String subject, String text) {
        Map<String, String> payload = new HashMap<>();
        payload.put("to", to);
        payload.put("subject", subject);
        payload.put("text", text);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        try {
            restTemplate.postForEntity(emailServiceUrl + "/api/email/send", request, String.class);
        } catch (RestClientException e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }
}
