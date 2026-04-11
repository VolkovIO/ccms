package com.example.ccms.communicationcase.infrastructure.integration.messaging;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(MessagingProviderProperties.class)
public class RestClientConfig {

  @Bean
  RestClient messagingProviderRestClient(
      RestClient.Builder builder, MessagingProviderProperties properties) {

    return builder.baseUrl(properties.baseUrl()).build();
  }
}
