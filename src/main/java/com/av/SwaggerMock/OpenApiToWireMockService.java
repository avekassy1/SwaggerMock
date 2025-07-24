package com.av.SwaggerMock;

import com.av.SwaggerMock.wiremock.StubMappingGenerator;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenApiToWireMockService {

  private final StubMappingGenerator stubMappingGenerator;

  public List<StubMapping> generateStubMappings(String specContent) {
    OpenAPI openAPI = new OpenAPIV3Parser().readContents(specContent, null, null).getOpenAPI();
    if (openAPI == null) {
      throw new IllegalArgumentException("Invalid OpenAPI spec.");
    }

    return stubMappingGenerator.generate(openAPI);
  }
}
