package com.av.SwaggerMock.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class OpenApiToWireMockServiceTest {
  private final OpenApiToWireMockService service = new OpenApiToWireMockService(null);

  @Test
  void shouldThrowIllegalArgumentExceptionIfSpecIsInvalid() {
    assertThrows(
        IllegalArgumentException.class, () -> service.generateStubMappings("invalid spec"));
  }
}
