package com.example.consumer.controller;

import com.example.consumer.dto.notification.MessageDTO;
import com.example.consumer.dto.notification.NotificationDTO;
import com.example.consumer.service.EmailService;
import com.example.consumer.validators.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    private final EmailService emailService;

    private final EmailValidator emailValidator;

    public EmailController(EmailService emailService, EmailValidator emailValidator) {
        this.emailService = emailService;
        this.emailValidator = emailValidator;
    }

    @PostMapping("/send-email")
    public ResponseEntity<MessageDTO> sendEmail(@RequestHeader("Authorization") String authorizationHeader,
                                                @RequestBody NotificationDTO requestDto) {

        if (!emailValidator.isValidAuthorizationToken(authorizationHeader, requestDto.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!emailValidator.isValidPayload(requestDto)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        boolean emailSent = emailService.sendEmail(requestDto);
        if (emailSent) {
            return ResponseEntity.ok(new MessageDTO("Mail trimis catre " + requestDto.getEmail()));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
