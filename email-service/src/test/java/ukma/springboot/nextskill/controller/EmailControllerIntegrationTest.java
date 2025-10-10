package ukma.springboot.nextskill.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender mailSender;

    @Value("${app.api-key}")
    private String validApiKey;

    @MockBean
    private RestTemplate restTemplate;

    private static final String EMAIL_REQUEST_JSON = """
            {
                "to": "user@example.com",
                "subject": "Security Test",
                "text": "Test message"
            }
            """;

    @Test
    void sendEmail_withValidApiKey_shouldReturn200() throws Exception {
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

        mockMvc.perform(post("/api/email/send")
                        .header("X-API-KEY", validApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EMAIL_REQUEST_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent"));
    }

    @Test
    void sendEmail_withoutApiKey_shouldReturn401() throws Exception {
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EMAIL_REQUEST_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sendEmail_withInvalidApiKey_shouldReturn401() throws Exception {
        Mockito.when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

        mockMvc.perform(post("/api/email/send")
                        .header("X-API-KEY", "INVALID_KEY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EMAIL_REQUEST_JSON))
                .andExpect(status().isUnauthorized());
    }
}
