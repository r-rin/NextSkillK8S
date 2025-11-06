package ukma.springboot.nextskill.email;

public interface EmailService {

    void sendHighPriorityEmail(String to, String subject, String text);

    void sendLowPriorityEmail(String to, String subject, String text);

}
