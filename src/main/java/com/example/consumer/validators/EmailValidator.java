package com.example.consumer.validators;

import com.example.consumer.dto.notification.NotificationDTO;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator {

    public boolean isValidAuthorizationToken(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.equals("Bearer cata");
    }

    public boolean isValidPayload(NotificationDTO requestDto) {
        return requestDto != null && requestDto.getId() != null
                && requestDto.getName() != null && requestDto.getEmail() != null;
    }
}
