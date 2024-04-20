package com.example.consumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueListenerConfig {

    @Bean
    public QueueListener queueListenerService() {
        QueueListener listener = new QueueListener();
        listener.startListening();
        return listener;
    }
}

