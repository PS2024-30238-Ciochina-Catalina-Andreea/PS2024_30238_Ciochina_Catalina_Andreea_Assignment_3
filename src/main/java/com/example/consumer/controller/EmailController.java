package com.example.consumer.controller;

import com.example.consumer.dto.notification.MessageDTO;
import com.example.consumer.dto.notification.NotificationDTO;
import com.example.consumer.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<MessageDTO> sendEmail(@RequestHeader("Authorization") String authorizationHeader,
                                                @RequestBody NotificationDTO requestDto) {

        if (!isValidAuthorizationToken(authorizationHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!isValidPayload(requestDto)) {
            return ResponseEntity.badRequest().build();
        }

        boolean emailSent = emailService.sendEmail(requestDto);
        if (emailSent) {
            return ResponseEntity.ok(new MessageDTO("Mail trimis"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isValidAuthorizationToken(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.equals("Bearer cata");
    }

    private boolean isValidPayload(NotificationDTO requestDto) {
        return requestDto != null && requestDto.getId() != null
                && requestDto.getName() != null && requestDto.getEmail() != null;
    }
}
