package ukma.springboot.nextskill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private Environment environment;

    @GetMapping("/email")
    public String getEmail() {
        return environment.getProperty("local.server.port");
    }
}
