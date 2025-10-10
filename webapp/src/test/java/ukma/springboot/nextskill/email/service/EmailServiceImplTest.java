package ukma.springboot.nextskill.email.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
        "email.service.url=http://email.local",
        "email.service.api-key=TEST_KEY"
})
class EmailServiceImplTest {

    @Autowired
    private EmailServiceImpl emailService;

    @SpyBean
    private RestTemplate restTemplate;

    @Test
    void sendEmail_setsApiKeyHeader_andPayload() {
        when(restTemplate.postForEntity(eq("http://email.local/api/email/send"), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        emailService.sendEmail("to@ex.com", "subj", "text");

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(eq("http://email.local/api/email/send"), captor.capture(), eq(String.class));

        HttpEntity<?> entity = captor.getValue();
        assertThat(entity.getHeaders().getFirst("X-API-KEY")).isEqualTo("TEST_KEY");

        Object body = entity.getBody();
        assertThat(body).isInstanceOf(Map.class);
        Map<?, ?> map = (Map<?, ?>) body;
        assertThat(map.get("to")).isEqualTo("to@ex.com");
        assertThat(map.get("subject")).isEqualTo("subj");
        assertThat(map.get("text")).isEqualTo("text");
    }
    @Test
    void sendEmail_swallowsRestClientException() {
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("down"));

        assertDoesNotThrow(() -> emailService.sendEmail("t@e.com", "s", "b"));
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }
}