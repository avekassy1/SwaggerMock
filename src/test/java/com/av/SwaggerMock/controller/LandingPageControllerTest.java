package com.av.SwaggerMock.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LandingPageControllerTest {
  @Test
  void testHome_ReturnsBannerMessage() {
    LandingPageController controller = new LandingPageController();

    String response = controller.home();

    assertTrue(
        response.contains("<h2>Welcome to SwaggerMock!</h2>"), "Response should contain header");
    assertTrue(
        response.contains("/wiremock/upload-spec"), "Response should mention the upload endpoint");
  }
}
