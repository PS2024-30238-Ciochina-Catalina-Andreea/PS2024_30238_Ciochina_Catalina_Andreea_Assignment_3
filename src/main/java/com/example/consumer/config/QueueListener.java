package com.example.consumer.config;

import com.example.consumer.dto.notification.NotificationDTO;
import com.example.consumer.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class QueueListener {

    private final static String QUEUE_NAME = "email_queue";

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueListener.class);

    @Autowired
    private EmailService emailService;

    public void startListening() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("localhost");

                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                LOGGER.info("Astept mesaje...");

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    LOGGER.info("Am primit: '" + message + "'");

                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        NotificationDTO notificationDto = objectMapper.readValue(message, NotificationDTO.class);
                        if(emailService == null){
                            this.emailService = new EmailService();
                        }
                        emailService.sendEmail(notificationDto);
                    } catch (IOException e) {
                        LOGGER.error("Eroare la deserializarea mesajului: " + e.getMessage());
                    }
                };

                channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
                });
            } catch (Exception e) {
                LOGGER.error("Eroare la conectarea la RabbitMQ sau la crearea canalului: " + e.getMessage());
            }
        });
    }
}
