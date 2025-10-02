package ukma.springboot.nextskill.service;

public interface EmailService {

    void sendEmail(String to, String subject, String text);

}
