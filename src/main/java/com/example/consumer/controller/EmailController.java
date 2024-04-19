package com.example.consumer.controller;

import com.example.consumer.dto.notification.NotificationDTO;
import com.example.consumer.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody NotificationDTO notificationRequestDto) {
        this.emailService.sendEmail(notificationRequestDto);
        return ResponseEntity.ok("Email sent successfully");
    }
}