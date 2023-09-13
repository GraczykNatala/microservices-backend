package com.example.auth.configuration;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import static com.example.auth.utils.EmailUtils.*;

@Configuration
@Slf4j
public class EmailConfiguration {


    private final String email;
    private final String password;
    private Authenticator auth;
    private Session session;
    private Properties properties;

    public EmailConfiguration(@Value("${notification.mail}") String email, @Value("${notification.password}") String password){
        this.email = email;
        this.password = password;
        config();
    }

    private void config(){
        String smtpHost = SMTP_HOST_NAME;
        int smtpPort = 587;

        properties = new Properties();
        properties.put(SMTP_AUTH, "true");
        properties.put(SMTP_STARTTLS, "true");
        properties.put(MAIL_SMTP_HOST, smtpHost);
        properties.put(MAIL_SMTP_PORT, smtpPort);

        this.auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        };
    }

    private void refreshSession(){
        session = Session.getInstance(properties,auth);
    }

    public void sendMail(String recipientEmail, String content,String subject,boolean onCreate){
        if (session == null){
            refreshSession();
        }
        try{
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(content, CONTENT_TYPE);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
            Transport.send(message);
        }catch (MessagingException e) {
            log.warn("Email not sent");
            if (onCreate){
                refreshSession();
                sendMail(recipientEmail,content,subject,false);
            }
        }
    }
}
