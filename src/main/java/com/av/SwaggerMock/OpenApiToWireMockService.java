package com.av.SwaggerMock;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

        // TODO - filter out header, query, and cookie parameters
        // and the format has to be changed to whatever the wiremockbuilder will accept
        List<Parameter> operationParams = operation.getParameters();
        List<Parameter> headerParams = new ArrayList<>();
        headerParams = operationParams.stream().filter(s -> s.getIn() == "header").toList();
        List<Parameter> queryParams = new ArrayList<>();
        headerParams = operationParams.stream().filter(s -> s.getIn() == "query").toList();

        System.out.println("urlEqualTo(path) -> " + path);

        MappingBuilder requestPattern =
//            WireMock.request(method.name(), WireMock.urlEqualTo(path))
            WireMock.request(method.name(), WireMock.urlPathTemplate(path))
                .withPathParam(pathParams.get(0).getName(), equalTo("123")) // no withPathParams so need to loop
                        //.withHeader() // withHeaders only available on ResponseDefinitionBuilder???
                        //.withQueryParams()
//                        .withPathParams()
                ;
        ResponseDefinitionBuilder responseDefinition =
                new ResponseDefinitionBuilder()
                        .withStatus(200)
                        .withStatusMessage(String.format("Non-dynamic status message. " +
                                "path item params: %s", pathParams));
//                                "path params: %s", pathParams));

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

    //  Error processing spec: Cannot invoke "java.util.List.isEmpty()" because "params" is null
//        if (!params.isEmpty()) {
//            headerParams = params.stream().filter(s -> s.getIn() == "header").toList();
//            pathParams = params.stream().filter(s -> s.getIn() == "path").toList();
//            queryParams = params.stream().filter(s -> s.getIn() == "query").toList();
//        }

// Questions and thoughts
// - Would this work with $ref values (within same or different files)
// - Add validation step in there
// - What values can I extract from the openAPI object and from each path?
// - Automatic generation of examples of none provided?
}
