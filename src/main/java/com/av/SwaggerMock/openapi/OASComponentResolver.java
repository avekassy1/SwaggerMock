package com.av.SwaggerMock.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.AllArgsConstructor;

/*
Resolver extracting $ref values
* */
@AllArgsConstructor
public class OASComponentResolver {

    private final Components components;

    public Schema<?> resolveSchema(Schema<?> schema) {
        if (schema == null || schema.get$ref() == null) return schema;
        String refName = extractRefName(schema.get$ref());
        return components.getSchemas().get(refName);
    }

    public Parameter resolveParameter(Parameter parameter) {
        if (parameter == null || parameter.get$ref() == null) return parameter;
        String refName = extractRefName(parameter.get$ref());
        return components.getParameters().get(refName);
    }

    public RequestBody resolveRequestBody(RequestBody requestBody) {
        if (requestBody == null || requestBody.get$ref() == null) return requestBody;
        String refName = extractRefName(requestBody.get$ref());
        return components.getRequestBodies().get(refName);
    }

    public ApiResponse resolveResponse(ApiResponse response) {
        if (response == null || response.get$ref() == null) return response;
        String refName = extractRefName(response.get$ref());
        return components.getResponses().get(refName);
    }

    private String extractRefName(String ref) {
        // Handles "#/components/schemas/Foo" => "Foo"
        return ref.substring(ref.lastIndexOf('/') + 1);
    }
}
