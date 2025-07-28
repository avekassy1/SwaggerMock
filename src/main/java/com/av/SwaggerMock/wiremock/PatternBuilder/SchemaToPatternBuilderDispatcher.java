package com.av.SwaggerMock.wiremock.PatternBuilder;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchemaToPatternBuilderDispatcher {

  private final Map<Class<? extends Schema<?>>, WireMockPatternBuilder> builderMap;

  @Autowired
  public SchemaToPatternBuilderDispatcher(List<WireMockPatternBuilder> builders) {
    this.builderMap =
        builders.stream()
            .collect(Collectors.toMap(WireMockPatternBuilder::getSchemaType, Function.identity()));
  }

  public StringValuePattern createPattern(Schema<?> schema) {

    WireMockPatternBuilder builder = builderMap.get(schema.getClass());
    if (builder != null && builder.supports(schema)) {
      return builder.create(schema);
    }
    return WireMock.absent();
  }
}
