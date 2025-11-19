package ukma.springboot.nextskill.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "nextskill.course-service")
public class CourseServiceClientProperties {
    private String baseUrl = "http://localhost:8080";
}
