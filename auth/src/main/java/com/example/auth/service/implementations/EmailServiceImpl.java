package com.example.auth.service.implementations;

import com.example.auth.configuration.EmailConfiguration;
import com.example.auth.entity.User;
import com.example.auth.service.EmailService;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static com.example.auth.utils.EmailUtils.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final EmailConfiguration emailConfiguration;

    @Value("${front.url}")
    private String frontendUrl;
    @Value("classpath:static/mail-activate.html")
    Resource activeTemplate;

    @Value("classpath:static/mail-reset-password.html")
    Resource recoveryTemplate;


    public void sendActivation(User user) {
        try {
            String html = Files.toString(activeTemplate.getFile(), Charsets.UTF_8);
            html = html.replace(GOOGLE, frontendUrl + ACTIVATE_PATH + user.getUuid());
            emailConfiguration.sendMail(user.getEmail(), html, ACTIVATE_SUBJECT, true);
            log.info("Email sent");
        }
        catch(IOException e) {
            log.warn("Cannot send email");
            throw new RuntimeException(e);
        }
    }

    public void sendPasswordRecovery(User user, String uid) {
        try {
            String html = Files.toString(recoveryTemplate.getFile(), Charsets.UTF_8);
            html = html.replace(GOOGLE, frontendUrl + RESET_PASSWORD_PATH + uid);
            emailConfiguration.sendMail(user.getEmail(), html, RESET_PASSWORD_SUBJECT, true);
            log.info("Email sent");
        }
        catch(IOException e) {
            log.warn("Cannot send email");
            throw new RuntimeException(e);
        }
    }

}
