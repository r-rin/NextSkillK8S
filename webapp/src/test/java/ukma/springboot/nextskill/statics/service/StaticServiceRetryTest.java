package ukma.springboot.nextskill.statics.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void getJson_shouldSucceedOnFirstAttempt() {
        Object expectedResult = new Object();
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
            .thenReturn(expectedResult);

        Object result = staticService.getJson();

        verify(restTemplate, times(1)).getForObject(anyString(), eq(Object.class));
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getJson_shouldRetryWithBackoff() {
        Object expectedResult = new Object();
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
            .thenThrow(new RestClientException("Connection timeout"))
            .thenReturn(expectedResult);

        long startTime = System.currentTimeMillis();
        Object result = staticService.getJson();
        long endTime = System.currentTimeMillis();

        assertThat(endTime - startTime).isGreaterThanOrEqualTo(1200);

        verify(restTemplate, times(2)).getForObject(anyString(), eq(Object.class));
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void getJson_shouldRecoverAfterMaxAttempts() {
        when(restTemplate.getForObject(anyString(), eq(Object.class)))
            .thenThrow(new RestClientException("Connection timeout"));

        long startTime = System.currentTimeMillis();
        Object result = staticService.getJson();
        long endTime = System.currentTimeMillis();

        assertThat(endTime - startTime).isGreaterThanOrEqualTo(3600);

        verify(restTemplate, times(3)).getForObject(anyString(), eq(Object.class));
        assertThat(result).isNotNull();
    }
}
