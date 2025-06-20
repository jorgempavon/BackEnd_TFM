package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final String BIBLIOKIE =  "Bibliokie";
    private final String DEAR = "Estimado/a ";
    private final String BIBLIOKIE_EMAIL =  "bibliokiejackie@gmail.com";
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void newAccountEmail(String email,String userName, String password) {
        try{
            String subject = "Notificación de nueva cuenta en "+BIBLIOKIE;
            String body = DEAR + userName + ":\n\n" +
                    "Le damos la bienvenida a " + BIBLIOKIE + ". Su cuenta ha sido creada exitosamente y ya puede acceder a nuestros servicios.\n\n" +
                    (password != null && !password.isBlank()
                            ? "Su contraseña temporal es: " + password + "\n\n"
                            : "") +
                    "Si usted no ha solicitado esta cuenta o tiene alguna duda, por favor contáctenos lo antes posible.\n\n" +
                    "Atentamente,\n" +
                    BIBLIOKIE+"\n" +
                    BIBLIOKIE_EMAIL;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(BIBLIOKIE_EMAIL);
            message.setTo(email);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        }
        catch (Exception e){
            throw new BadRequestException("El correo proporcionado no existe. Porfavor, introduzca un nuevo.");
        }
    }

    public void deleteAccountEmail(String email, String userName) {
        try{
            String subject = "Notificación de eliminación de cuenta en Bibliokie";
            String body = DEAR+userName+":\n\n" +
                    "Le informamos que su cuenta ha sido eliminada de nuestra aplicación de forma definitiva.\n" +
                    "Esta acción implica que ya no podrá acceder a los servicios asociados ni recuperar los datos vinculados a dicha cuenta.\n\n" +
                    "Si esta acción fue realizada por usted, no es necesario hacer nada más.\n" +
                    "En caso contrario, o si tiene alguna pregunta, no dude en ponerse en contacto con nosotros a través de este correo o del soporte técnico.\n\n" +
                    "Atentamente,\n" +
                    BIBLIOKIE+"\n" +
                    BIBLIOKIE_EMAIL;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(BIBLIOKIE_EMAIL);
            message.setTo(email);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        }
        catch (Exception e){
            throw new BadRequestException("El correo proporcionado no existe. Porfavor, introduzca un nuevo.");
        }
    }

    public void oldAccountEmail(String oldEmail, String newEmail, String userName) {
        try {
            String subject = "Notificación de cambio de correo en " + BIBLIOKIE;
            String body = DEAR + userName + ":\n\n" +
                    "Le informamos que su dirección de correo asociada a nuestra aplicación ha sido actualizada correctamente.\n" +
                    "Correo anterior: " + oldEmail + "\n" +
                    "Nuevo correo: " + newEmail + "\n\n" +
                    "Si usted ha realizado este cambio, no necesita realizar ninguna acción adicional.\n" +
                    "En caso de no haber solicitado esta modificación, por favor contáctenos de inmediato para asegurar la integridad de su cuenta.\n\n" +
                    "Atentamente,\n" +
                    BIBLIOKIE + "\n" +
                    BIBLIOKIE_EMAIL;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(oldEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch (Exception e) {
            throw new BadRequestException("El correo proporcionado no existe. Porfavor, introduzca un nuevo.");
        }
    }

    public void modifiedAccountEmail(String oldEmail, String newEmail, String userName, String generatedPassword) {
        try {
            String subject = "Actualización de correo en " + BIBLIOKIE;
            String body =
                    DEAR + userName + ":\n\n" +
                            "Le informamos que se ha actualizado la dirección de correo asociada a su cuenta en " + BIBLIOKIE + ".\n" +
                            "Anteriormente, su dirección de correo era: " + oldEmail + "\n" +
                            "Ahora, su nueva dirección de correo es: " + newEmail + "\n\n" +
                            (generatedPassword != null && !generatedPassword.isBlank()
                                    ? "Se ha generado una contraseña temporal: " + generatedPassword + "\n" +
                                    "Le recomendamos cambiarla al iniciar sesión.\n\n"
                                    : "") +
                            "Si usted no solicitó este cambio o tiene alguna duda, por favor contáctenos de inmediato.\n\n" +
                            "Atentamente,\n" +
                            BIBLIOKIE + "\n" +
                            BIBLIOKIE_EMAIL;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(newEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch (Exception e) {
            throw new BadRequestException("El correo proporcionado no existe. Porfavor, introduzca un nuevo.");
        }
    }

    public void regeneratedPasswordEmail(String email, String userName, String generatedPassword) {
        try {
            String subject = "Contraseña restablecida en " + BIBLIOKIE;

            String body =
                    DEAR + userName + ":\n\n" +
                            "Le informamos que su contraseña ha sido restablecida correctamente en " + BIBLIOKIE + ".\n" +
                            "Nueva contraseña generada: " + generatedPassword + "\n\n" +
                            "Le recomendamos iniciar sesión lo antes posible y cambiar esta contraseña por una de su preferencia desde su perfil de usuario.\n\n" +
                            "Si usted no solicitó este restablecimiento, por favor contáctenos de inmediato.\n\n" +
                            "Atentamente,\n" +
                            BIBLIOKIE + "\n" +
                            BIBLIOKIE_EMAIL;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch (Exception e) {
            throw new BadRequestException("No se pudo enviar el correo de restablecimiento de contraseña.");
        }
    }

}
