package ukma.springboot.nextskill.email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ukma.springboot.nextskill.common.communication.ServiceNames;

@RestController
public class EmailController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${email-service.api-key}")
    private String apiKey;

    @GetMapping("/email")
    public String getEmail() {
        String url = "http://" + ServiceNames.EMAIL_SERVICE + "/email";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        var entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
    }
}
