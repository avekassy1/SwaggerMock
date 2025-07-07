package com.av.SwaggerMock.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OASComponentResolverTest {

    private OASComponentResolver resolver;

    @BeforeEach
    void setUp() {
        Components components = new Components();

        components.addSchemas("stringWithLength3to10", new StringSchema().minLength(3).maxLength(10));
        components.addParameters("UserIdParam", new Parameter().name("id").in("path"));
        components.addRequestBodies("CreateUserBody", new RequestBody().description("User creation body"));
        components.addResponses("SuccessResponse", new ApiResponse().description("Success"));

        resolver = new OASComponentResolver(components);
    }

    @Test
    void shouldResolveReferencedStringSchema() {
        StringSchema stringSchemaWithRef = new StringSchema();
        stringSchemaWithRef.set$ref("#/components/schemas/stringWithLength3to10");
        StringSchema resolvedSchema = (StringSchema) resolver.resolveSchema(stringSchemaWithRef);

        assertNotNull(resolvedSchema);
        assertEquals(3, resolvedSchema.getMinLength());
    }

    @Test
    void shouldResolveReferencedParameter() {
        Parameter parameterWithRef = new Parameter();
        parameterWithRef.set$ref("#/components/parameters/UserIdParam");
        Parameter resolvedParam = resolver.resolveParameter(parameterWithRef);

        assertNotNull(resolvedParam);
        assertEquals("id", resolvedParam.getName());
        assertEquals("path", resolvedParam.getIn());
    }

    @Test
    void shouldResolveReferencedRequestBody() {
        RequestBody requestBodyWithRef = new RequestBody();
        requestBodyWithRef.set$ref("#/components/requestBodies/CreateUserBody");
        RequestBody resolvedRequestBody = resolver.resolveRequestBody(requestBodyWithRef);

        assertNotNull(resolvedRequestBody);
        assertEquals("User creation body", resolvedRequestBody.getDescription());
    }

    @Test
    void shouldResolveReferencedResponse() {
        ApiResponse responseWithRef = new ApiResponse();
        responseWithRef.set$ref("#/components/responses/SuccessResponse");
        ApiResponse resolvedResponse = resolver.resolveResponse(responseWithRef);

        assertNotNull(resolvedResponse);
        assertEquals("Success", resolvedResponse.getDescription());
    }
}
