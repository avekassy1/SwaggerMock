package com.av.SwaggerMock.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.av.SwaggerMock.OpenApiToWireMockService;
import com.av.SwaggerMock.wiremock.WireMockManager;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(WireMockController.class)
public class WireMockControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockitoBean private WireMockManager wireMockManager;

  @MockitoBean private OpenApiToWireMockService openApiToWireMockService;

  @Test
  void addStubMapping_shouldReturnOkOnValidMapping() throws Exception {
    StubMapping mapping =
        StubMapping.buildFrom(
            "{ \"request\": { \"method\": \"GET\", \"url\": \"/test\" }, \"response\": { \"status\": 200 } }");
    String json = Json.write(mapping);

    when(wireMockManager.getServer()).thenReturn(mock(WireMockServer.class));

    mockMvc
        .perform(
            post("/wiremock/upload-mapping").contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string("Mapping added."));
  }

  @Test
  void addStubMapping_shouldReturnBadRequestOnInvalidJson() throws Exception {
    String invalidJson = "{ malformed json }";

    mockMvc
        .perform(
            post("/wiremock/upload-mapping")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid mapping")));
  }

  @Test
  void uploadOpenApiSpec_shouldReturnOkAndAddMappings() throws Exception {
    String dummySpec = "openapi: 3.0.0\ninfo:\n  title: Test\npaths: {}";
    StubMapping mapping =
        StubMapping.buildFrom(
            "{ \"request\": { \"method\": \"GET\", \"url\": \"/dummy\" }, \"response\": { \"status\": 200 } }");

    when(openApiToWireMockService.generateStubMappings(dummySpec))
        .thenReturn(Collections.singletonList(mapping));

    mockMvc
        .perform(
            post("/wiremock/upload-spec")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dummySpec))
        .andExpect(status().isOk())
        .andExpect(content().string("Mappings created: 1"));
  }

  @Test
  void uploadOpenApiSpec_shouldReturnBadRequestOnException() throws Exception {
    String badSpec = "invalid spec";
    when(openApiToWireMockService.generateStubMappings(badSpec))
        .thenThrow(new RuntimeException("Invalid OpenAPI"));

    mockMvc
        .perform(
            post("/wiremock/upload-spec").contentType(MediaType.APPLICATION_JSON).content(badSpec))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Error processing spec")));
  }
}
