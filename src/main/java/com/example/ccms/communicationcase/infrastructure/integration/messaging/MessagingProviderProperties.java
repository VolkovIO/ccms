package com.example.ccms.communicationcase.infrastructure.integration.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.messaging.provider")
public record MessagingProviderProperties(String baseUrl, String sendMessagePath) {}
