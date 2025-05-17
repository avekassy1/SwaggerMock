package com.av.SwaggerMock;

import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
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

    @PostMapping("/mapping")
    public ResponseEntity<String> addStubMapping(@RequestBody String mappingJson) {
        try {
            StubMapping mapping = Json.read(mappingJson, StubMapping.class);
            wireMockManager.addMapping(mapping);
            return ResponseEntity.ok("Mapping added.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid mapping: " + e.getMessage());
        }
    }
}
