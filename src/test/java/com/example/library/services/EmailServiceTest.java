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
    private final String EXAMPLE_NAME = "example";
    private final String EXAMPLE_EMAIL = "test@example.com";
    private final String EXAMPLE_OLD_EMAIL = "oldTest@example.com";
    private final String EXAMPLE_PASSWORD = "pass123";
    @Test
    void newAccountEmail_successful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.newAccountEmail(EXAMPLE_EMAIL,EXAMPLE_NAME,EXAMPLE_PASSWORD);
    }
    @Test
    void deleteAccountEmail_successful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.deleteAccountEmail(EXAMPLE_EMAIL,EXAMPLE_NAME);
    }

    @Test
    void oldAccountEmail_successful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.oldAccountEmail(EXAMPLE_OLD_EMAIL,EXAMPLE_EMAIL,EXAMPLE_NAME);
    }

    @Test
    void modifiedAccountEmail_successful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.modifiedAccountEmail(EXAMPLE_OLD_EMAIL,EXAMPLE_EMAIL,EXAMPLE_NAME,EXAMPLE_PASSWORD);
    }

    @Test
    void regeneratedPasswordEmail_successful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.regeneratedPasswordEmail(EXAMPLE_EMAIL,EXAMPLE_NAME,EXAMPLE_PASSWORD);
    }
    @Test
    void newAccountEmail_whenNotExistsEmail_throwBadRequestException(){
        doThrow(new BadRequestException("El correo proporcionado no existe"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        assertThrows(BadRequestException.class, () ->
                emailService.newAccountEmail(EXAMPLE_EMAIL, EXAMPLE_NAME, EXAMPLE_PASSWORD)
        );
    }
    @Test
    void deleteAccountEmail_whenNotExistsEmail_throwBadRequestException(){
        doThrow(new BadRequestException("El correo proporcionado no existe"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertThrows(BadRequestException.class, () ->
                emailService.deleteAccountEmail(EXAMPLE_EMAIL, EXAMPLE_NAME)
        );
    }
    @Test
    void oldAccountEmail_whenNotExistsEmail_throwBadRequestException(){
        doThrow(new BadRequestException("El correo proporcionado no existe"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertThrows(BadRequestException.class, () ->
                emailService.oldAccountEmail(EXAMPLE_OLD_EMAIL,EXAMPLE_EMAIL, EXAMPLE_NAME)
        );
    }
    @Test
    void modifiedAccountEmail_whenNotExistsEmail_throwBadRequestException(){
        doThrow(new BadRequestException("El correo proporcionado no existe"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertThrows(BadRequestException.class, () ->
                emailService.modifiedAccountEmail(EXAMPLE_OLD_EMAIL,EXAMPLE_EMAIL, EXAMPLE_NAME,EXAMPLE_PASSWORD)
        );
    }
    @Test
    void regeneratedPasswordEmail_whenNotExistsEmail_throwBadRequestException(){
        doThrow(new BadRequestException("El correo proporcionado no existe"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertThrows(BadRequestException.class, () ->
                emailService.regeneratedPasswordEmail(EXAMPLE_EMAIL, EXAMPLE_NAME,EXAMPLE_PASSWORD)
        );
    }
}
