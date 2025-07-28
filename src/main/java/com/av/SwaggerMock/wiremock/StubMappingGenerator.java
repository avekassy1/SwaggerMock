package com.av.SwaggerMock.wiremock;

import com.av.SwaggerMock.openapi.ExampleBuilder.SchemaToExampleBuilderDispatcher;
import com.av.SwaggerMock.openapi.OASComponentResolver;
import com.av.SwaggerMock.wiremock.PatternBuilder.SchemaToPatternBuilderDispatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wiremock.com.fasterxml.jackson.databind.JsonNode;

@Slf4j
@Component
@RequiredArgsConstructor
public class StubMappingGenerator {

  private final SchemaToPatternBuilderDispatcher schemaToPatternBuilderDispatcher;
  private final SchemaToExampleBuilderDispatcher schemaToExampleBuilderDispatcher;
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static OASComponentResolver componentResolver;

  public List<StubMapping> generate(OpenAPI openAPI) {
    componentResolver = new OASComponentResolver(openAPI.getComponents());

    List<StubMapping> stubMappings = new ArrayList<>();
    openAPI
        .getPaths()
        .forEach(
            (path, pathItem) -> {
              pathItem
                  .readOperationsMap()
                  .forEach(
                      (httpMethod, operation) -> {
                        StubMapping stubMapping =
                            createStubFromOperation(operation, path, pathItem, httpMethod);
                        stubMappings.add(stubMapping);
                      });
            });

    return stubMappings;
  }

  private StubMapping createStubFromOperation(
      Operation operation, String path, PathItem pathItem, PathItem.HttpMethod method) {

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
        new ResponseDefinitionBuilder().withStatus(Integer.parseInt(statusCode));
    // Further fields: use .like() method of ResponseDefBuilder for inspiration

    putJsonBodyOnResponseDefinition(response, responseDefinition);

    return responseDefinition;
  }

  private void putJsonBodyOnResponseDefinition(
      ApiResponse response, ResponseDefinitionBuilder responseDefinition) {
    Content content = response.getContent(); // LinkedHashMap<String, MediaType>
    if (content == null || content.isEmpty()) return;

    String contentType = content.keySet().iterator().next();
    responseDefinition.withHeader("Content-Type", contentType);

    MediaType mediaType = content.get(contentType);
    Schema<?> bodySchema = mediaType.getSchema(); // ? is a questionable declaration
    if (bodySchema.get$ref() != null) {
      bodySchema = componentResolver.resolveSchema(bodySchema);
    }

    Object exampleBody = schemaToExampleBuilderDispatcher.buildExample(bodySchema);

    try {
      String jsonBody = objectMapper.writeValueAsString(exampleBody);
      JsonNode wmockJsonNode = Json.read(jsonBody, JsonNode.class);
      responseDefinition.withJsonBody(wmockJsonNode);
    } catch (JsonProcessingException e) {
      log.warn("Failed to generate example response body from schema", e);
      throw new RuntimeException("Invalid JSON body", e);
    }
  }

  private Map<String, List<Parameter>> groupParametersByIn(List<Parameter> parameters) {
    return parameters.stream()
        .filter(p -> p.getIn() != null)
        .collect(Collectors.groupingBy(Parameter::getIn));
  }

  private List<Parameter> getAllParameters(PathItem pathItem, Operation operation) {
    List<Parameter> allParams = new ArrayList<>();

    Optional.ofNullable(pathItem.getParameters()).ifPresent(allParams::addAll);

    Optional.ofNullable(operation.getParameters()).ifPresent(allParams::addAll);

    return allParams;
  }

  private void putParametersOnRequestPattern(
      Map<String, List<Parameter>> paramGroups, MappingBuilder requestPattern) {

    Map<String, BiConsumer<String, StringValuePattern>> parameterAppliers =
        Map.of(
            "path", requestPattern::withPathParam,
            "query", requestPattern::withQueryParam,
            "header", requestPattern::withHeader,
            "cookie", requestPattern::withCookie);

    for (Map.Entry<String, BiConsumer<String, StringValuePattern>> entry :
        parameterAppliers.entrySet()) {
      String paramType = entry.getKey();
      BiConsumer<String, StringValuePattern> applier = entry.getValue();

      List<Parameter> params = paramGroups.getOrDefault(paramType, Collections.emptyList());
      for (Parameter parameter : params) {
        Schema schema = parameter.getSchema();
        StringValuePattern pattern = schemaToPatternBuilderDispatcher.createPattern(schema);

        if (Boolean.FALSE.equals(parameter.getRequired())) {
          pattern = WireMock.or(WireMock.absent(), pattern);
        }
        applier.accept(parameter.getName(), pattern);
      }
    }
  }
}
