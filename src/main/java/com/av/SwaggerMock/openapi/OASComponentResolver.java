package com.av.SwaggerMock.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.RequestBody;
import lombok.AllArgsConstructor;

/*
Resolver extracting $ref values
* */
@AllArgsConstructor
public class OASComponentResolver {
    private final Components components;

    public RequestBody resolveRequestBody(RequestBody requestBody) {
        if (requestBody == null || requestBody.get$ref() == null) return requestBody;
        String refName = extractRefName(requestBody.get$ref());
        return components.getRequestBodies().get(refName);
    }

    private String extractRefName(String ref) {
        // Handles "#/components/schemas/Foo" => "Foo"
        return ref.substring(ref.lastIndexOf('/') + 1);
    }
}
