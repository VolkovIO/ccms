package com.example.ccms.communicationcase.infrastructure.integration.fakeprovider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FakeProviderCallbackClient {

  private final RestClient fakeProviderCallbackRestClient;
  private final FakeProviderCallbackProperties properties;

  public FakeProviderCallbackResult sendIncomingMessageCallback(
      String communicationCaseId, IncomingMessageCallbackRequest request) {

    try {
      fakeProviderCallbackRestClient
          .post()
          .uri(properties.incomingMessagePathTemplate(), communicationCaseId)
          .body(request)
          .retrieve()
          .toBodilessEntity();

      log.info(
          "Fake provider callback delivered: communicationCaseId={}, channel={}",
          communicationCaseId,
          request.channel());

      return FakeProviderCallbackResult.success();

    } catch (HttpStatusCodeException ex) {
      log.warn(
          "Fake provider callback rejected: communicationCaseId={}, status={}",
          communicationCaseId,
          ex.getStatusCode().value());

      return FakeProviderCallbackResult.failure(
          ex.getStatusCode().value(), ex.getResponseBodyAsString());

    } catch (RestClientException ex) {
      log.error("Fake provider callback failed: communicationCaseId={}", communicationCaseId, ex);

      return FakeProviderCallbackResult.failure(
          502, "Callback delivery failed: " + ex.getMessage());
    }
  }
}
