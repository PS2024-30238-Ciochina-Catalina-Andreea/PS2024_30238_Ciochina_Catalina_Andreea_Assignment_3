package com.example.consumer;

import com.example.consumer.config.QueueListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
        QueueListener queueListenerService = new QueueListener();
        queueListenerService.startListening();
    }

}
