package com.av.SwaggerMock.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiExceptionHandlerTest {

  private final ApiExceptionHandler handler = new ApiExceptionHandler();

  @Test
  void handleIllegalArgumentException_returnsBadRequest() {
    IllegalArgumentException ex = new IllegalArgumentException("Invalid input");
    ResponseEntity<String> response = handler.handleIllegalArgumentException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("Bad request: Invalid input");
  }

  @Test
  void handleGeneralException_returnsInternalServerError() {
    Exception ex = new Exception("Something went wrong");
    ResponseEntity<String> response = handler.handleGeneralException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isEqualTo("An unexpected error occurred: Something went wrong");
  }
}
