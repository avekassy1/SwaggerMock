package com.av.SwaggerMock;

import com.av.SwaggerMock.wiremock.PatternBuilder.SchemaToPatternBuilderDispatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OpenApiToWireMockService {

    private final SchemaToPatternBuilderDispatcher schemaToPatternBuilderDispatcher;
    private static final ObjectMapper objectMapper = new ObjectMapper();

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

        Components components = openAPI.getComponents(); // Stores reusable definitions
        // Passing this down to every other function is tedious, need an object-oriented approach I reckon

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
        ResponseDefinitionBuilder responseDefBuilder = createResponseDefinitionBuilder(operation);
        // STUB
        StubMapping stubMapping = requestPattern.willReturn(responseDefBuilder).build();
        log.info("Stub created:\n{}", stubMapping);
        return stubMapping;
    }

    private MappingBuilder createRequestPattern(
        String path, PathItem pathItem, Operation operation, PathItem.HttpMethod method) {

        // TODO - deal with path parameters in URL
        MappingBuilder requestPattern = WireMock.request(method.name(), WireMock.urlPathTemplate(path));

        List<Parameter> allParams = getAllParameters(pathItem, operation);
        Map<String, List<Parameter>> paramGroups = groupParametersByIn(allParams);
        putParametersOnRequestPattern(paramGroups, requestPattern);

        return requestPattern;
    }

    private ResponseDefinitionBuilder createResponseDefinitionBuilder(Operation operation) {
        ApiResponses responses = operation.getResponses();
        String statusCode = responses.keySet().iterator().next();

        // How to deal with multiple responses provided? Create a stub for each innit?
        // But then logic won't hold up for client-side error responses
        // For now, generate response for one and only one status aka the first one

        ApiResponse response = responses.get(statusCode);
        ResponseDefinitionBuilder responseDefinition =
            new ResponseDefinitionBuilder()
                .withStatus(Integer.parseInt(statusCode));
        // Further fields: use .like() method of ResponseDefBuilder for inspiration
        //.withStatusMessage()

        putBodyOnResponseDefinition(response, responseDefinition);

        return responseDefinition;
    }

    private static void putBodyOnResponseDefinition(ApiResponse response, ResponseDefinitionBuilder responseDefinition) {
        Content content = response.getContent(); // LinkedHashMap<String, MediaType>
        if (content == null || content.isEmpty()) return;

        String contentType = content.keySet().iterator().next();
        responseDefinition.withHeader("Content-Type", contentType);

        MediaType mediaType = content.get(contentType);
        Schema bodySchema = mediaType.getSchema();
        String $ref = bodySchema.get$ref();

        Object example = bodySchema.getExample();
        // Example isn't picked up if referenced

        if (example != null) {
            String json = null;
            try {
                json = objectMapper.writeValueAsString(example);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            responseDefinition.withBody(json);
            return;
        }


        responseDefinition.withBody("body"); // input is either string or byte
        // Also possible to have withResponseBody(Body body) and a withJsonBody(JsonNode jsonBody)
        // Factory pattern again
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
        Map<String, BiConsumer<String, StringValuePattern>> parameterAppliers = Map.of(
            "path", requestPattern::withPathParam,
            "query", requestPattern::withQueryParam,
            "header", requestPattern::withHeader,
            "cookie", requestPattern::withCookie
        );

        for (Map.Entry<String, BiConsumer<String, StringValuePattern>> entry : parameterAppliers.entrySet()) {
            String paramType = entry.getKey();
            BiConsumer<String, StringValuePattern> applier = entry.getValue();

            List<Parameter> params = paramGroups.getOrDefault(paramType, Collections.emptyList());
            for (Parameter parameter : params) {
                Schema schema = parameter.getSchema();
                StringValuePattern pattern = schemaToPatternBuilderDispatcher.createPattern(schema);

                if (Boolean.FALSE.equals(parameter.getRequired())) {
                    log.debug("Parameter '{}' of type '{}' is optional. WireMock stubs may need custom matcher to reflect this.",
                        parameter.getName(), paramType);
                    // TODO - reflect this on stub somehow - if required is false, return or(absent(), pattern)?
                    // Might have to implement a custom matcher
                }
                applier.accept(parameter.getName(), pattern);
            }
        }
    }
}
