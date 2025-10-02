package ukma.springboot.nextskill;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmailServiceApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(EmailServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {}
}
