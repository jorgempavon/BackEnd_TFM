package com.example.library.services;

import com.example.library.api.exceptions.models.BadRequestException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final String BIBLIOKIE =  "Bibliokie";
    private final String BIBLIOKIE_EMAIL =  "bibliokiejackie@gmail.com";
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void newAccountEmail(String email,String userName, String password) {
        try{
            String subject = "Notificación de nueva cuenta en "+BIBLIOKIE;
            String body = "Estimado/a " + userName + ":\n\n" +
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
            String body = "Estimado/a "+userName+":\n\n" +
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
}
