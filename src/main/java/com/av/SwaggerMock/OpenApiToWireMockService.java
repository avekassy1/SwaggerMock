package com.av.SwaggerMock;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@Service
public class OpenApiToWireMockService {

    public List<StubMapping> generateStubMappings(String specContent) {
        OpenAPI openAPI =
            new OpenAPIV3Parser().readContents(specContent, null, null).getOpenAPI();

        if (openAPI == null) {
            throw new IllegalArgumentException("Invalid OpenAPI spec.");
        }

        String serverDesc = openAPI.getServers().get(0).getDescription();

        List<StubMapping> stubMappings = new ArrayList<>();
        openAPI.getPaths().forEach((path, pathItem) -> {
            pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                // Generate a stub for each path + operation
                StubMapping stubMapping = createStubFromOperation(path, httpMethod, operation, serverDesc);
                stubMappings.add(stubMapping);
            });
        });
        
        return stubMappings;
    }

    private StubMapping createStubFromOperation(
            String path, PathItem.HttpMethod method,
            Operation operation, String serverDescription) {
        StubMapping sm = WireMock.request(method.name(), WireMock.urlEqualTo(path))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/plain")
                        .withBody("Hello world!" + serverDescription))
                .build();
        return sm;

// Questions and thoughts
// - Would this work with $ref values (within same or different files)
// - Add validation step in there
// - What values can I extract from the openAPI object and from each path?
// - Automatic generation of examples of none provided?
    }
}
