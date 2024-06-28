package com.pentacode.pentacode_middleware.email_gateway.service;

import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import com.pentacode.pentacode_middleware.email_gateway.dto.SendEmailRequest;

import java.time.LocalDateTime;

@Service
public class EmailService {

    private static Logger audit = LogManager.getLogger("audit-log");
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromName;
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(SendEmailRequest sendEmailRequest) {
        try {
            if (!sendEmailRequest.getTo().isEmpty() &&
                    !sendEmailRequest.getBody().isEmpty() &&
                    !sendEmailRequest.getSubject().isEmpty()
            ) {
                run(sendEmailRequest);
            }

        } catch (Exception e) {
            audit.info("email gateway,email,sent,fail,to," + sendEmailRequest.getTo() + ",error," + e.getMessage());
        }
    }

    @Async
    public void run(SendEmailRequest sendEmailRequest) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            String from = fromName + " <" + fromEmail + ">";

            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(sendEmailRequest.getTo());
            mimeMessageHelper.setSubject(sendEmailRequest.getSubject());
            mimeMessageHelper.setText(sendEmailRequest.getBody());

            javaMailSender.send(mimeMessage);
            audit.info("email gateway,email,sent," + sendEmailRequest.getTo() + ",at," + LocalDateTime.now() + ",success,ok");
        } catch (Exception e) {
            audit.info("email gateway,email,sent,fail,to," + sendEmailRequest.getTo() + ",error," + e.getMessage());
        }
    }
}