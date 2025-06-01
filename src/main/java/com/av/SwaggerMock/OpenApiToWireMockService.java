package com.av.SwaggerMock;

import com.av.SwaggerMock.PatternBuilder.SchemaToPatternBuilderDispatcher;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.av.SwaggerMock.PatternBuilder.PatternBuilderUtils.TEMPORARY;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

@Service
public class OpenApiToWireMockService {

    private final SchemaToPatternBuilderDispatcher schemaToPatternBuilderDispatcher;

    @Autowired
    public OpenApiToWireMockService(SchemaToPatternBuilderDispatcher schemaToPatternBuilderDispatcher) {
        this.schemaToPatternBuilderDispatcher = schemaToPatternBuilderDispatcher;
    }

    public List<StubMapping> generateStubMappings(String specContent) {
        OpenAPI openAPI =
            new OpenAPIV3Parser().readContents(specContent, null, null).getOpenAPI();

        if (openAPI == null) {
            throw new IllegalArgumentException("Invalid OpenAPI spec.");
        }

        List<StubMapping> stubMappings = new ArrayList<>();
        openAPI.getPaths().forEach((path, pathItem) -> {
            pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                StubMapping stubMapping = createStubFromOperation(operation, path, pathItem, httpMethod);
                stubMappings.add(stubMapping);
            });
        });

        return stubMappings;
    }

    private StubMapping createStubFromOperation(
        Operation operation, String path,
        PathItem pathItem, PathItem.HttpMethod method) {

        // REQUEST
        MappingBuilder requestPattern = createRequestPattern(path, pathItem, operation, method);
        // RESPONSE
        ResponseDefinitionBuilder responseDefBuilder = createResponseDefinitionBuilder();
        // STUB
        StubMapping stubMapping = requestPattern.willReturn(responseDefBuilder).build();
        System.out.println("Stub created:\n" + stubMapping.toString()); // TODO - use SLF4J logger instead

        return stubMapping;
    }

    private MappingBuilder createRequestPattern(
        String path, PathItem pathItem, Operation operation, PathItem.HttpMethod method) {

        MappingBuilder requestPattern = WireMock.request(method.name(), WireMock.urlPathTemplate(path));

        List<Parameter> allParams = getAllParameters(pathItem, operation);
        Map<String, List<Parameter>> paramGroups = groupParametersByIn(allParams);

        putParametersOnRequestPattern(paramGroups, requestPattern);

        return requestPattern;
    }

    private ResponseDefinitionBuilder createResponseDefinitionBuilder() {
        ResponseDefinitionBuilder responseDefinition =
            new ResponseDefinitionBuilder()
                .withStatus(200); // TODO - fill up with variable
        //.withStatusMessage(String.format("Status message. Added path and query params"));
        return responseDefinition;
    }

    private Map<String, List<Parameter>> groupParametersByIn(List<Parameter> parameters) {
        return parameters.stream()
            .filter(p -> p.getIn() != null)
            .collect(Collectors.groupingBy(Parameter::getIn));
    }

    private List<Parameter> getAllParameters(PathItem pathItem, Operation operation) {
        List<Parameter> allParams = new ArrayList<>();

        Optional.ofNullable(pathItem.getParameters())
            .ifPresent(allParams::addAll);

        Optional.ofNullable(operation.getParameters())
            .ifPresent(allParams::addAll);

        return allParams;
    }

    private void putParametersOnRequestPattern(Map<String, List<Parameter>> paramGroups, MappingBuilder requestPattern) {
        List<Parameter> pathParams = paramGroups.getOrDefault("path", Collections.emptyList());
        List<Parameter> queryParams = paramGroups.getOrDefault("query", Collections.emptyList());
        List<Parameter> headerParams = paramGroups.getOrDefault("header", Collections.emptyList());
        List<Parameter> cookieParams = paramGroups.getOrDefault("cookie", Collections.emptyList());

        for (Parameter parameter : pathParams) {
            requestPattern.withPathParam(parameter.getName(), equalTo(TEMPORARY)); // TODO - replace with dispatcher
        }

        for (Parameter parameter : headerParams) {
            Schema schema = parameter.getSchema();
            StringValuePattern pattern = schemaToPatternBuilderDispatcher.createPattern(schema);
            requestPattern.withHeader(parameter.getName(), pattern);
        }

        for (Parameter parameter : queryParams) {
            // withQueryParams() takes in a map which may not work with MultiValuePatterns
            Schema schema = parameter.getSchema();
            StringValuePattern pattern = schemaToPatternBuilderDispatcher.createPattern(schema);
            requestPattern.withQueryParam(parameter.getName(), pattern); // TODO - replace with dispatcher
        }

        for (Parameter parameter : cookieParams) {
            requestPattern.withCookie(parameter.getName(), equalTo(TEMPORARY)); // TODO - replace with dispatcher
        }
    }
}

//        StubMapping sm = WireMock.request(
//                        method.name(), WireMock.urlEqualTo(path)
//                )
//                .withHeader("Content-Type", "application/json")
//                //.withQueryParams()
//                .willReturn(
//                        aResponse()
/// /                        .withHeader("Content-Type", "text/plain")
/// /                        .withBody("Hello world!" + serverDescription)
//                                .build();

// Questions and thoughts
// - What pattern/design would be the best for the stub creator? Builder perhaps? Mapstruct?
// - Considerations for wiremock extension?
// - Would this work with $ref values (within same or different files)
// - Add validation step in there
// - Automatic generation of examples of none provided
// - Code for deleting or modifying mapping (from UI in future)
