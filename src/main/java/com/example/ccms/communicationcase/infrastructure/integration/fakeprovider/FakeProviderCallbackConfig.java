package com.example.ccms.communicationcase.infrastructure.integration.fakeprovider;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(FakeProviderCallbackProperties.class)
public class FakeProviderCallbackConfig {

  @Bean
  RestClient fakeProviderCallbackRestClient(
      RestClient.Builder builder, FakeProviderCallbackProperties properties) {

    return builder.baseUrl(properties.baseUrl()).build();
  }
}
