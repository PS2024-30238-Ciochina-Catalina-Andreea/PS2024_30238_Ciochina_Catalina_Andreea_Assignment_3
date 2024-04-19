package com.example.consumer;

import com.example.consumer.config.QueueListener;
import com.example.consumer.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsumerApplication {

	@Autowired
	private static EmailService emailService;

	@Autowired
	private static ObjectMapper objectMapper;

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
		QueueListener queueListenerService = new QueueListener(emailService, objectMapper);
		queueListenerService.startListening();
	}

}
