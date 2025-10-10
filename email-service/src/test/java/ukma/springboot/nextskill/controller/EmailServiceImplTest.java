package ukma.springboot.nextskill.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ukma.springboot.nextskill.service.EmailServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void sendEmail_constructsAndSendsCorrectMessage() {
        String to = "user@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail(to, subject, body);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        assertAll("Checking the margins of a letter",
                () -> assertEquals(to, capturedMessage.getTo()[0]),
                () -> assertEquals(subject, capturedMessage.getSubject()),
                () -> assertEquals(body, capturedMessage.getText())
        );
    }

    @Test
    void sendEmail_throwsMailException_whenSenderFails() {
        String errorMessage = "Service unavailable";
        doThrow(new MailSendException(errorMessage))
                .when(mailSender)
                .send(Mockito.any(SimpleMailMessage.class));

        MailSendException thrownException = assertThrows(MailSendException.class, () -> {
            emailService.sendEmail("user@example.com", "Test Subject", "Test Body");
        });

        assertEquals(errorMessage, thrownException.getMessage());

        verify(mailSender).send(Mockito.any(SimpleMailMessage.class));
    }
}