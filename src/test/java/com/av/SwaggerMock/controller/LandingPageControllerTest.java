package com.av.SwaggerMock.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LandingPageControllerTest {
    @Test
    void testHome_ReturnsBannerMessage() {
        // Arrange
        LandingPageController controller = new LandingPageController();

        // Act
        String response = controller.home();

        // Assert
        // Check that response contains key parts of your banner and instructions
        assertTrue(response.contains("<h2>Welcome to SwaggerMock!</h2>"), "Response should contain header");
        assertTrue(response.contains("/wiremock/upload-spec"), "Response should mention the upload endpoint");
    }
}
