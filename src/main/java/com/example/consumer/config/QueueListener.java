package com.example.consumer.config;
import com.example.consumer.dto.notification.NotificationDTO;
import com.example.consumer.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class QueueListener {

    private final static String QUEUE_NAME = "email_queue";

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Autowired
    public QueueListener(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    public void startListening() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("localhost");

                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                System.out.println("Așteptam mesaje...");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    System.out.println("Am primit mesajul: '" + message + "'");

                    try {
                        NotificationDTO notificationDto = objectMapper.readValue(message, NotificationDTO.class);
                        emailService.sendEmail(notificationDto);
                    } catch (IOException e) {
                        System.err.println("Eroare la deserializarea mesajului: " + e.getMessage());
                    }
                };

                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
                });
            } catch (Exception e) {
                System.err.println("Eroare la conectarea la RabbitMQ sau la crearea canalului: " + e.getMessage());
            }
        });
    }
}
