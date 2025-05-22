package com.av.SwaggerMock;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

@Service
public class OpenApiToWireMockService {

    public List<StubMapping> generateStubMappings(String specContent) {
        OpenAPI openAPI =
            new OpenAPIV3Parser().readContents(specContent, null, null).getOpenAPI();

        if (openAPI == null) {
            throw new IllegalArgumentException("Invalid OpenAPI spec.");
        }

        List<StubMapping> stubMappings = new ArrayList<>();
        openAPI.getPaths().forEach((path, pathItem) -> {
            pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                // Generate a stub for each path + operation
                StubMapping stubMapping = createStubFromOperation(path, pathItem, httpMethod, operation);
                stubMappings.add(stubMapping);
            });
        });

        return stubMappings;
    }

    private StubMapping createStubFromOperation(
            String path,
            PathItem pathItem,
            PathItem.HttpMethod method,
            Operation operation) {

        List<Parameter> pathParams = pathItem.getParameters();

        // TODO - filter cookie parameters too!
        List<Parameter> operationParams = Optional.ofNullable(operation.getParameters()).orElse(Collections.emptyList());

        List<Parameter> queryParams = operationParams.stream().filter(s -> Objects.equals(s.getIn(), "query")).toList();
        Map<String, StringValuePattern> queryParamsMap =
                queryParams.stream().collect(Collectors.toMap(Parameter::getName, p -> equalTo("temporary")));
        List<Parameter> headerParams = operationParams.stream().filter(s -> Objects.equals(s.getIn(), "header")).toList();

        // REQUEST
        MappingBuilder requestPattern =
            WireMock.request(method.name(), WireMock.urlPathTemplate(path))
                .withPathParam(pathParams.get(0).getName(), equalTo("123")) // no withPathParams so need to loop
                // .withCookie()
                .withQueryParams(queryParamsMap);

        if (!headerParams.isEmpty()) {
            requestPattern
                    .withHeader(headerParams.get(0).getName(), equalTo(String.valueOf(UUID.randomUUID()))); // withHeaders only available on ResponseDefinitionBuilder???
        }

        // RESPONSE
        ResponseDefinitionBuilder responseDefinition =
                new ResponseDefinitionBuilder()
                        .withStatus(200)
                        .withStatusMessage(String.format("Status message. " +
                                "added path and query params"));

        StubMapping stubMapping = requestPattern.willReturn(responseDefinition).build();

        System.out.println("Stub created");
        System.out.println(stubMapping.toString());

        return stubMapping;
    }

//        StubMapping sm = WireMock.request(
//                        method.name(), WireMock.urlEqualTo(path)
//                )
//                .withHeader("Content-Type", "application/json")
//                //.withQueryParams()
//                .willReturn(
//                        aResponse()
////                        .withHeader("Content-Type", "text/plain")
////                        .withBody("Hello world!" + serverDescription))
//                                .build();

// Questions and thoughts
// - Would this work with $ref values (within same or different files)
// - Add validation step in there
// - Automatic generation of examples of none provided
}
