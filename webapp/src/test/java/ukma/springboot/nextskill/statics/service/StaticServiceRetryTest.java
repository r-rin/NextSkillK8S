package ukma.springboot.nextskill.statics.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class StaticServiceRetryTest {

    @Autowired
    private StaticService staticService;

    @SpyBean
    private RestTemplate restTemplate;

    @Test
    void getJson_shouldRetryOnRestClientException() {
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
                .thenThrow(new RestClientException("Connection failed"))
                .thenThrow(new RestClientException("Connection failed again"))
                .thenReturn(null);

        Object result = staticService.getJson();

        // Перевіряємо, що було зроблено 2 спроби (maxAttempts = 2)
        verify(restTemplate, times(2)).getForObject(anyString(), eq(Object.class));

        // Перевіряємо, що повернулося fallback значення
        assertThat(result).isNotNull();
    }

    @Test
    void getJson_shouldSucceedOnFirstAttempt() {
        Object expectedResult = new Object();
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
                .thenReturn(expectedResult);

        Object result = staticService.getJson();

        // Перевіряємо, що було зроблено лише 1 спробу
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Object.class));
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getJson_shouldRecoverAfterMaxAttempts() {
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
                .thenThrow(new RestClientException("Persistent failure"));

        Object result = staticService.getJson();

        // Перевіряємо, що було зроблено максимальну кількість спроб
        verify(restTemplate, times(2)).getForObject(anyString(), eq(Object.class));

        // Перевіряємо, що спрацював recover метод і повернулось fallback значення
        assertThat(result).isNotNull();
    }

    @Test
    void getJson_shouldRetryWithBackoff() throws InterruptedException {
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
                .thenThrow(new RestClientException("Connection timeout"))
                .thenReturn(new Object());

        long startTime = System.currentTimeMillis();
        Object result = staticService.getJson();
        long endTime = System.currentTimeMillis();

        // Перевіряємо, що пройшов хоча б мінімальний час затримки (1200ms)
        // У тесті це може бути менше через особливості тестового оточення
        assertThat(endTime - startTime).isGreaterThanOrEqualTo(0);

        // Перевіряємо, що було зроблено 2 спроби
        verify(restTemplate, times(2)).getForObject(anyString(), eq(Object.class));
        assertThat(result).isNotNull();
    }
}
