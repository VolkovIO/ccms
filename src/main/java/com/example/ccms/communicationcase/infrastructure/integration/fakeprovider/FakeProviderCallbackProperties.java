package com.example.ccms.communicationcase.infrastructure.integration.fakeprovider;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.fake-provider.callback")
public record FakeProviderCallbackProperties(String baseUrl, String incomingMessagePathTemplate) {}
