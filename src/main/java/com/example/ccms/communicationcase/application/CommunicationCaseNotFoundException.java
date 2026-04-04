package com.example.ccms.communicationcase.application;

import java.io.Serial;

public class CommunicationCaseNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  public CommunicationCaseNotFoundException(String message) {
    super(message);
  }
}
