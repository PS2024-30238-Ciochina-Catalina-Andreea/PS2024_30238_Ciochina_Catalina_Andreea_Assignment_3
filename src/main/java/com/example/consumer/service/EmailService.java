package com.example.consumer.service;

import com.example.consumer.dto.notification.InvoiceDTO;
import com.example.consumer.dto.notification.NotificationDTO;
import com.example.consumer.validators.EmailValidator;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    private final EmailValidator emailValidator;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender javaMailSender, EmailValidator emailValidator) {
        this.javaMailSender = javaMailSender;
        this.emailValidator = emailValidator;
    }

    public boolean sendEmail(NotificationDTO notificationRequestDto) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(notificationRequestDto.getEmail());
            helper.setSubject(notificationRequestDto.getBodyAction() + " on FlowerShop");

            String htmlBody = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/accountCreated.html")));

            setEmailTemplate(helper, htmlBody);

            javaMailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            LOGGER.error("Ceva nu a mers bine la crearea email-ului", e);
        } catch (IOException e) {
            LOGGER.error("Nu s-a citit fisierul HTML pentru construirea emailului", e);
        }
        return false;
    }

    public void sendEmailWithPdf(InvoiceDTO invoiceDTO) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            if (this.emailValidator.isValidPayloadForInvoice(invoiceDTO)) {
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(invoiceDTO.getEmail());
                helper.setSubject("Invoice");

                String htmlBody = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/invoiceSent.html")));
                setEmailTemplate(helper, htmlBody);

                OutputStream out = new FileOutputStream("invoice_user.pdf");
                out.write(invoiceDTO.getBody());
                out.close();
                DataSource pdf = new FileDataSource("invoice_user.pdf");
                helper.addAttachment("invoice_user.pdf", pdf);
                javaMailSender.send(message);
                LOGGER.info("Email-ul a fost trimit catre {}", invoiceDTO.getEmail());
            } else {
                LOGGER.error("Payload-ul nu este valid");
            }
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    private void setEmailTemplate(MimeMessageHelper helper, String htmlBody) throws MessagingException {
        helper.setText(htmlBody, true);
        DataSource cornerImg = new FileDataSource("src/main/resources/templates/corner.png");
        helper.addInline("corner", cornerImg);
        DataSource cornerRightImg = new FileDataSource("src/main/resources/templates/corner-right.png");
        helper.addInline("corner-right", cornerRightImg);
        DataSource bouquet = new FileDataSource("src/main/resources/templates/bouquet1.png");
        helper.addInline("bouquet1", bouquet);
    }
}
