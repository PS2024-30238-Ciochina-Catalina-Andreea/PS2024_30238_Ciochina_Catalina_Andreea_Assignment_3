package com.example.consumer.config;

import com.example.consumer.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueListenerConfig {

    @Bean
    public QueueListener queueListenerService(EmailService emailService, ObjectMapper objectMapper) {
        QueueListener listener = new QueueListener(emailService, objectMapper);
        listener.startListening();
        return listener;
    }
}

