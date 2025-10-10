package ukma.springboot.nextskill.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ukma.springboot.nextskill.dto.EmailRequest;
import ukma.springboot.nextskill.service.EmailService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.api-key}")
    private String validApiKey;

    @MockBean
    private EmailService emailService;

    private static final EmailRequest request = EmailRequest.builder()
        .to("user@example.com")
        .subject("Security Test")
        .text("Test message")
        .build();

    @Test
    void sendEmail_withValidApiKey_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/email/send")
                .header("X-API-KEY", validApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string("Email sent"));

        verify(emailService, only()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void sendEmail_withoutApiKey_shouldReturn401() throws Exception {
        mockMvc.perform(post("/api/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void sendEmail_withInvalidApiKey_shouldReturn401() throws Exception {
        mockMvc.perform(post("/api/email/send")
                .header("X-API-KEY", "INVALID_KEY")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}
