import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;
import ukma.springboot.nextskill.NextSkillApplication;

@SpringBootTest(classes = NextSkillApplication.class)
class NextSkillApplicationTests {

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Checks
    }

}
