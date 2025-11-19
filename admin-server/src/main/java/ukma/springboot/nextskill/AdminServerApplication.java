package ukma.springboot.nextskill;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ukma.springboot.nextskill.config.CourseServiceClientProperties;

@SpringBootApplication
@EnableAdminServer
@EnableConfigurationProperties(CourseServiceClientProperties.class)
public class AdminServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminServerApplication.class, args);
    }
}

