package com.av.SwaggerMock;

import com.av.SwaggerMock.Mapper.SchemaToPatternDispatcher;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

@Service
public class OpenApiToWireMockService {

    private final SchemaToPatternDispatcher schemaToPatternDispatcher;

    @Autowired
    public OpenApiToWireMockService(SchemaToPatternDispatcher schemaToPatternDispatcher) {
        this.schemaToPatternDispatcher = schemaToPatternDispatcher;
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

        // Path params
        List<Parameter> pathParams = pathItem.getParameters();

        // Operation params - query, header, and cookie params
        List<Parameter> operationParams = Optional.ofNullable(operation.getParameters()).orElse(Collections.emptyList());

        List<Parameter> queryParams = operationParams.stream().filter(s -> Objects.equals(s.getIn(), "query")).toList();
        Map<String, StringValuePattern> queryParamsMap =
            queryParams.stream().collect(Collectors.toMap(Parameter::getName, p -> equalTo("temporary")));
        List<Parameter> headerParams = operationParams.stream().filter(s -> Objects.equals(s.getIn(), "header")).toList();
        List<Parameter> cookieParams = operationParams.stream().filter(s -> Objects.equals(s.getIn(), "cookie")).toList();

        MappingBuilder requestPattern =
            WireMock.request(method.name(), WireMock.urlPathTemplate(path))
                .withPathParam(pathParams.get(0).getName(), equalTo("123")) // no withPathParams so need to loop
                // .withCookie()
                .withQueryParams(queryParamsMap);

        if (!headerParams.isEmpty()) {
            for (Parameter parameter : headerParams) {
                Schema schema = parameter.getSchema();
                StringValuePattern pattern = schemaToPatternDispatcher.createPattern(schema);
                requestPattern.withHeader(parameter.getName(), pattern);
            }
            // TODO - generate the equalTo(...) automatically using schemaToPatternDispatcher

//            requestPattern
//                    .withHeader(headerParams.get(0).getName(), equalTo(String.valueOf(UUID.randomUUID()))); // withHeaders only available on ResponseDefinitionBuilder???
//                    .withHeader(headerParams.get(0).getName(), absent());
        }
        return requestPattern;
    }

    private ResponseDefinitionBuilder createResponseDefinitionBuilder() {
        ResponseDefinitionBuilder responseDefinition =
            new ResponseDefinitionBuilder()
                .withStatus(200)
                .withStatusMessage(String.format("Status message. Added path and query params"));
        return responseDefinition;
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
