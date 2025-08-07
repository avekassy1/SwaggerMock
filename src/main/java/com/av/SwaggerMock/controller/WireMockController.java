package com.av.SwaggerMock.controller;

import com.av.SwaggerMock.service.OpenApiToWireMockService;
import com.av.SwaggerMock.wiremock.WireMockManager;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wiremock")
@RequiredArgsConstructor
public class WireMockController {

  private final WireMockManager wireMockManager;
  private final OpenApiToWireMockService openApiToWireMockService;

  @PostMapping("/upload-mapping")
  public ResponseEntity<String> addStubMapping(@RequestBody String mappingJson) {
    try {
      StubMapping mapping = Json.read(mappingJson, StubMapping.class);
      wireMockManager.getServer().addStubMapping(mapping);
      return ResponseEntity.ok("Mapping added.");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Invalid mapping: " + e.getMessage());
    }
  }

  @PostMapping("/upload-spec")
  public ResponseEntity<?> uploadOpenApiSpec(@RequestBody String specContent) {
    List<StubMapping> mappings = openApiToWireMockService.generateStubMappings(specContent);
    // TODO - remove wiremock server
    mappings.forEach(wireMockManager::addMapping);
    return ResponseEntity.ok(mappings.toString());
  }
}
