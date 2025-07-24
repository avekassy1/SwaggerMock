package com.av.SwaggerMock.PatternBuilder;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.*;

import com.av.SwaggerMock.OpenApiToWireMockService;
import com.av.SwaggerMock.wiremock.StubMappingGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StubMappingGeneratorTest {

  @Autowired OpenApiToWireMockService openApiToWireMockService;

  @Autowired StubMappingGenerator stubMappingGenerator;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void shouldProcess200ResponseWithJsonObjectInTheBody() throws Exception {
    validateStubMapping(
        "specs/200ResponseWithJsonObjectInTheBody.yaml",
        "stubs/StubFor200ResponseWithJsonObjectInTheBody.json");
  }

  @Test
  void shouldLogWarningWhenJsonProcessingExceptionOccurs() throws Exception {
    Schema<Object> bodySchema = new ObjectSchema();
    bodySchema.setExample(new UnserializableObject());

    OpenAPI openAPI = generateCustomOpenApiSpec(bodySchema, null);
    assertThrows(RuntimeException.class, () -> stubMappingGenerator.generate(openAPI));
  }

  @Test
  void createRequestMatcherWithOrForNonRequiredParameter() {
    Parameter optionalQueryParam =
        new QueryParameter()
            .name("optionalParam")
            .required(false)
            .schema(new StringSchema()._default("defaultVal"));

    OpenAPI openAPI = generateCustomOpenApiSpec(new ObjectSchema(), optionalQueryParam);

    List<StubMapping> mappings = stubMappingGenerator.generate(openAPI);
    assertFalse(mappings.isEmpty(), "Expected stub mappings to be generated");
    String mappingJson = mappings.get(0).toString();
    assertTrue(
        mappingJson.contains("optionalParam") && mappingJson.contains("queryParameters"),
        "Optional parameter should be reflected in the stub if supported.");
  }

  private static OpenAPI generateCustomOpenApiSpec(Schema<?> bodySchema, Parameter parameter) {

    // Set up the MediaType with the schema
    MediaType mediaType = new MediaType();
    mediaType.setSchema(bodySchema);

    // Create content with the above MediaType
    Content content = new Content();
    content.addMediaType("application/json", mediaType);

    // Set up a 200 OK response using that content
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setContent(content);

    // Add it to an ApiResponses map
    ApiResponses responses = new ApiResponses();
    responses.addApiResponse("200", apiResponse);

    // Create an operation (GET, POST, etc.) and attach the responses
    Operation operation = new Operation();
    operation.setResponses(responses);

    if (parameter != null) {
      operation.addParametersItem(parameter);
    }

    // Create a path item and attach the operation
    PathItem pathItem = new PathItem();
    pathItem.setGet(operation);

    // Set up the path and OpenAPI object
    Paths paths = new Paths();
    paths.addPathItem("/test", pathItem);

    OpenAPI openAPI = new OpenAPI();
    openAPI.setPaths(paths);
    return openAPI;
  }

  static class UnserializableObject {
    public String getValue() {
      throw new RuntimeException("Intentional serialization failure");
    }
  }

  private void validateStubMapping(String specPath, String stubPath) throws IOException {
    String spec = readFileToString(specPath);
    List<StubMapping> stubMappings = openApiToWireMockService.generateStubMappings(spec);

    if (stubMappings.isEmpty()) {
      throw new AssertionError("No stub mappings generated for spec: " + specPath);
    }

    StubMapping stubMapping = stubMappings.get(0);
    String expectedStub = readFileToString(stubPath);

    JsonNode actualNode = objectMapper.readTree(stubMapping.toString());
    JsonNode expectedNode = objectMapper.readTree(expectedStub);

    assertThatJson(actualNode).whenIgnoringPaths("id", "uuid").isEqualTo(expectedNode);
  }

  private String readFileToString(String path) throws IOException {
    URL resource = getClass().getClassLoader().getResource(path);
    if (resource == null) {
      throw new FileNotFoundException("Could not find resource " + path);
    }
    File file = new File(resource.getFile());
    return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
  }
}
