package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private EmailService emailService;
    private static final String EXAMPLE_NAME = "example";
    private static final String EXAMPLE_EMAIL = "test@example.com";
    private static final String EXAMPLE_OLD_EMAIL = "oldTest@example.com";
    private static final String EXAMPLE_PASS = "pass123";
    private static final String BAD_RESPONSE_EMAIL = "El correo proporcionado no existe";
    
    @Test
    void newAccountEmailSuccessful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.newAccountEmail(EXAMPLE_EMAIL,EXAMPLE_NAME,EXAMPLE_PASS);
    }
    @Test
    void deleteAccountEmailSuccessful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.deleteAccountEmail(EXAMPLE_EMAIL,EXAMPLE_NAME);
    }

    @Test
    void oldAccountEmailSuccessful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.oldAccountEmail(EXAMPLE_OLD_EMAIL,EXAMPLE_EMAIL,EXAMPLE_NAME);
    }

    @Test
    void modifiedAccountEmailSuccessful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.modifiedAccountEmail(EXAMPLE_OLD_EMAIL,EXAMPLE_EMAIL,EXAMPLE_NAME,EXAMPLE_PASS);
    }

    @Test
    void regeneratedPasswordEmailSuccessful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.regeneratedPasswordEmail(EXAMPLE_EMAIL,EXAMPLE_NAME,EXAMPLE_PASS);
    }
    @Test
    void newAccountEmailWhenNotExistsEmailThrowBadRequestException(){
        doThrow(new BadRequestException(BAD_RESPONSE_EMAIL))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        assertThrows(BadRequestException.class, () ->
                emailService.newAccountEmail(EXAMPLE_EMAIL, EXAMPLE_NAME, EXAMPLE_PASS)
        );
    }
    @Test
    void deleteAccountEmailWhenNotExistsEmailThrowBadRequestException(){
        doThrow(new BadRequestException(BAD_RESPONSE_EMAIL))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertThrows(BadRequestException.class, () ->
                emailService.deleteAccountEmail(EXAMPLE_EMAIL, EXAMPLE_NAME)
        );
    }
    @Test
    void oldAccountEmailWhenNotExistsEmailThrowBadRequestException(){
        doThrow(new BadRequestException(BAD_RESPONSE_EMAIL))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertThrows(BadRequestException.class, () ->
                emailService.oldAccountEmail(EXAMPLE_OLD_EMAIL,EXAMPLE_EMAIL, EXAMPLE_NAME)
        );
    }
    @Test
    void modifiedAccountEmailWhenNotExistsEmailThrowBadRequestException(){
        doThrow(new BadRequestException(BAD_RESPONSE_EMAIL))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertThrows(BadRequestException.class, () ->
                emailService.modifiedAccountEmail(EXAMPLE_OLD_EMAIL,EXAMPLE_EMAIL, EXAMPLE_NAME,EXAMPLE_PASS)
        );
    }
    @Test
    void regeneratedPasswordEmailWhenNotExistsEmailThrowBadRequestException(){
        doThrow(new BadRequestException(BAD_RESPONSE_EMAIL))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertThrows(BadRequestException.class, () ->
                emailService.regeneratedPasswordEmail(EXAMPLE_EMAIL, EXAMPLE_NAME,EXAMPLE_PASS)
        );
    }
}
