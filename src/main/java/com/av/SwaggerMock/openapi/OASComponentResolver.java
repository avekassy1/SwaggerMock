package com.av.SwaggerMock.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.Map;
import java.util.function.Function;
import lombok.AllArgsConstructor;

/*
Resolver extracting $ref values
* */
@AllArgsConstructor
public class OASComponentResolver {

  private final Components components;

  public Schema<?> resolveSchema(Schema<?> schema) {
    return resolve(schema, components.getSchemas(), Schema::get$ref);
  }

  public Parameter resolveParameter(Parameter parameter) {
    return resolve(parameter, components.getParameters(), Parameter::get$ref);
  }

  public RequestBody resolveRequestBody(RequestBody requestBody) {
    return resolve(requestBody, components.getRequestBodies(), RequestBody::get$ref);
  }

  public ApiResponse resolveResponse(ApiResponse response) {
    return resolve(response, components.getResponses(), ApiResponse::get$ref);
  }

  private <T> T resolve(
      T componentWithRef, Map<String, T> componentMap, Function<T, String> refExtractor) {
    if (componentWithRef == null) return null;

    String ref = refExtractor.apply(componentWithRef);
    if (ref == null) return componentWithRef;

    String refName = extractRefName(ref);
    return componentMap.getOrDefault(refName, componentWithRef);
  }

  private String extractRefName(String ref) {
    // Handles "#/components/schemas/Foo" => "Foo"
    return ref.substring(ref.lastIndexOf('/') + 1);
  }
}
