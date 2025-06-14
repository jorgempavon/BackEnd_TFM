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
    private final String exampleName = "example";
    private final String exampleEmail = "test@example.com";
    private final String exampleOldEmail = "oldTest@example.com";
    private final String examplePass = "pass123";
    private final String badResponseEmail = "El correo proporcionado no existe";
    
    @Test
    void newAccountEmail_successful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.newAccountEmail(exampleEmail,exampleName,examplePass);
    }
    @Test
    void deleteAccountEmail_successful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.deleteAccountEmail(exampleEmail,exampleName);
    }

    @Test
    void oldAccountEmail_successful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.oldAccountEmail(exampleOldEmail,exampleEmail,exampleName);
    }

    @Test
    void modifiedAccountEmail_successful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.modifiedAccountEmail(exampleOldEmail,exampleEmail,exampleName,examplePass);
    }

    @Test
    void regeneratedPasswordEmail_successful(){
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        this.emailService.regeneratedPasswordEmail(exampleEmail,exampleName,examplePass);
    }
    @Test
    void newAccountEmail_whenNotExistsEmail_throwBadRequestException(){
        doThrow(new BadRequestException(badResponseEmail))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        assertThrows(BadRequestException.class, () ->
                emailService.newAccountEmail(exampleEmail, exampleName, examplePass)
        );
    }
    @Test
    void deleteAccountEmail_whenNotExistsEmail_throwBadRequestException(){
        doThrow(new BadRequestException(badResponseEmail))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertThrows(BadRequestException.class, () ->
                emailService.deleteAccountEmail(exampleEmail, exampleName)
        );
    }
    @Test
    void oldAccountEmail_whenNotExistsEmail_throwBadRequestException(){
        doThrow(new BadRequestException(badResponseEmail))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertThrows(BadRequestException.class, () ->
                emailService.oldAccountEmail(exampleOldEmail,exampleEmail, exampleName)
        );
    }
    @Test
    void modifiedAccountEmail_whenNotExistsEmail_throwBadRequestException(){
        doThrow(new BadRequestException(badResponseEmail))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertThrows(BadRequestException.class, () ->
                emailService.modifiedAccountEmail(exampleOldEmail,exampleEmail, exampleName,examplePass)
        );
    }
    @Test
    void regeneratedPasswordEmail_whenNotExistsEmail_throwBadRequestException(){
        doThrow(new BadRequestException(badResponseEmail))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        assertThrows(BadRequestException.class, () ->
                emailService.regeneratedPasswordEmail(exampleEmail, exampleName,examplePass)
        );
    }
}
