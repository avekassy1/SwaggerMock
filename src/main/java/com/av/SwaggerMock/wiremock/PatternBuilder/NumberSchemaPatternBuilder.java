package com.av.SwaggerMock.wiremock.PatternBuilder;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class NumberSchemaPatternBuilder implements WireMockPatternBuilder {
  @Override
  public boolean supports(Schema<?> schema) {
    return schema instanceof NumberSchema;
  }

  @Override
  public StringValuePattern create(Schema<?> schema) {

    List<?> enumValues = schema.getEnum();
    if (enumValues != null && !enumValues.isEmpty()) {
      String joined = enumValues.stream().map(Object::toString).collect(Collectors.joining("|"));
      return WireMock.matching("^(" + joined + ")$");
    }

    // BigDecimal maximum = schema.getMaximum();
    // BigDecimal minimum = schema.getMinimum();
    // Boolean exclusiveMaximum = schema.getExclusiveMaximum();
    // Boolean exclusiveMinimum = schema.getExclusiveMinimum();
    // BigDecimal multipleOf = schema.getMultipleOf();

    // Regular expression cannot express contrainst extracted above. Implement custom matcher?

    return WireMock.matching(
        "\"^-?\\\\d+(\\\\.\\\\d+)?$\";"); // Allows optimal minus sign and optional decimal part
  }

  @Override
  public Class<? extends Schema<?>> getSchemaType() {
    return NumberSchema.class;
  }
}
