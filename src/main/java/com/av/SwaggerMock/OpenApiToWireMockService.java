package com.av.SwaggerMock;

import com.av.SwaggerMock.wiremock.PatternBuilder.SchemaToPatternBuilderDispatcher;
import com.av.SwaggerMock.wiremock.StubMappingGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OpenApiToWireMockService {

    private final SchemaToPatternBuilderDispatcher schemaToPatternBuilderDispatcher;

    @Autowired
    public OpenApiToWireMockService(SchemaToPatternBuilderDispatcher schemaToPatternBuilderDispatcher) {
        this.schemaToPatternBuilderDispatcher = schemaToPatternBuilderDispatcher;
    }
    // Is this the best way? Passing down the schemaToPatternBuilderDispatcher feels unnecessary
    // Ask John

    public List<StubMapping> generateStubMappings(String specContent) {
        OpenAPI openAPI = new OpenAPIV3Parser().readContents(specContent, null, null).getOpenAPI();

        if (openAPI == null) {
            throw new IllegalArgumentException("Invalid OpenAPI spec.");
        }

        return new StubMappingGenerator(openAPI, schemaToPatternBuilderDispatcher).generate();
    }
}