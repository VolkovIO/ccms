package com.example.ccms.communicationcase.infrastructure.integration.messaging;

import com.example.ccms.communicationcase.application.port.OutgoingMessageSendRequest;
import com.example.ccms.communicationcase.application.port.OutgoingMessageSender;
import com.example.ccms.communicationcase.application.port.SendMessageResult;
import com.example.ccms.communicationcase.infrastructure.integration.fakeprovider.FakeProviderSendMessageRequest;
import com.example.ccms.communicationcase.infrastructure.integration.fakeprovider.FakeProviderSendMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@Profile("http-messaging")
@RequiredArgsConstructor
public class HttpOutgoingMessageSender implements OutgoingMessageSender {

  private final RestClient messagingProviderRestClient;
  private final MessagingProviderProperties properties;

  @Override
  public SendMessageResult send(OutgoingMessageSendRequest request) {
    FakeProviderSendMessageRequest providerRequest =
        new FakeProviderSendMessageRequest(
            request.phoneNumber(), request.channel(), request.text());

    try {
      log.info(
          "Sending message via HTTP provider: channel={}, phoneNumber={}",
          request.channel(),
          maskPhoneNumber(request.phoneNumber()));

      FakeProviderSendMessageResponse response =
          messagingProviderRestClient
              .post()
              .uri(properties.sendMessagePath())
              .body(providerRequest)
              .retrieve()
              .body(FakeProviderSendMessageResponse.class);

      if (response == null) {
        log.warn("Messaging provider returned empty response");
        return SendMessageResult.failure("Messaging provider returned empty response");
      }

      if (response.accepted()) {
        log.info(
            "Messaging provider accepted message: providerMessageId={}",
            response.providerMessageId());
        return SendMessageResult.success(response.providerMessageId());
      }

      return SendMessageResult.failure(response.failureReason());
    } catch (RestClientException ex) {
      log.error("HTTP call to messaging provider failed", ex);
      return SendMessageResult.failure("Messaging provider call failed: " + ex.getMessage());
    }
  }

  private static final int VISIBLE_SUFFIX_LENGTH = 4;
  private static final int SHORT_PHONE_THRESHOLD = 4;

  private String maskPhoneNumber(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.isBlank()) {
      return "<empty>";
    }

    if (phoneNumber.length() <= SHORT_PHONE_THRESHOLD) {
      return "****";
    }

    int visiblePrefix =
        Math.min(VISIBLE_SUFFIX_LENGTH, phoneNumber.length() - VISIBLE_SUFFIX_LENGTH);
    return phoneNumber.substring(0, visiblePrefix)
        + "***"
        + phoneNumber.substring(phoneNumber.length() - VISIBLE_SUFFIX_LENGTH);
  }
}
