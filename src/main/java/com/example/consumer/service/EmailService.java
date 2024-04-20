package com.example.consumer.service;

import com.example.consumer.dto.notification.NotificationDTO;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    public boolean sendEmail(NotificationDTO notificationRequestDto) {
        if (javaMailSender == null) {
            this.javaMailSender = new JavaMailSenderImpl();
        }
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(notificationRequestDto.getEmail());
            helper.setSubject(notificationRequestDto.getBodyAction() + " on FlowerShop");

            String htmlBody = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/accountCreated.html")));

            helper.setText(htmlBody, true);
            DataSource cornerImg = new FileDataSource("src/main/resources/templates/corner.png");
            helper.addInline("corner", cornerImg);
            DataSource cornerRightImg = new FileDataSource("src/main/resources/templates/corner-right.png");
            helper.addInline("corner-right", cornerRightImg);
            DataSource bouquet = new FileDataSource("src/main/resources/templates/bouquet1.png");
            helper.addInline("bouquet1", bouquet);

            javaMailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            LOGGER.error("Ceva nu a mers bine la crearea email-ului", e);
        } catch (IOException e) {
            LOGGER.error("Nu s-a citit fisierul HTML pentru construirea emailului", e);
        }
        return false;
    }



}
