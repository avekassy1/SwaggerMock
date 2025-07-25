package com.av.SwaggerMock.wiremock.PatternBuilder;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StringSchemaPatternBuilder implements WireMockPatternBuilder {
  @Override
  public boolean supports(Schema<?> schema) {
    return schema instanceof StringSchema;
  }

  @Override
  public StringValuePattern create(Schema<?> schema) {
    if (!(schema instanceof StringSchema stringSchema)) {
      throw new IllegalArgumentException("Unsupported schema type: " + schema.getType());
    }

    // 1. Enum with a single constant value
    List<String> enumValues = castEnum(stringSchema.getEnum());
    if (enumValues != null && enumValues.size() == 1) {
      return WireMock.equalTo(enumValues.get(0));
    }

    // 2. Regex pattern
    if (stringSchema.getPattern() != null) {
      return WireMock.matching(stringSchema.getPattern());
    }

    // 3. Length constraints (minLength / maxLength)
    if (stringSchema.getMinLength() != null || stringSchema.getMaxLength() != null) {
      return buildLengthMatcher(stringSchema.getMinLength(), stringSchema.getMaxLength());
    }

    // 4. Enum (not constant)
    if (enumValues != null && !enumValues.isEmpty()) {
      return WireMock.matching(String.join("|", enumValues));
    }

    // 5. Fallback: wildcard match
    log.debug("Fallback for param named {}", schema.getName());
    return WireMock.matching(".*");
  }

  @Override
  public Class<? extends Schema<?>> getSchemaType() {
    return StringSchema.class;
  }

  private List<String> castEnum(List<?> rawEnum) {
    if (rawEnum == null) return null;
    return rawEnum.stream()
        .filter(Objects::nonNull)
        .map(Object::toString)
        .collect(Collectors.toList());
  }

  private StringValuePattern buildLengthMatcher(Integer minLength, Integer maxLength) {
    StringBuilder pattern = new StringBuilder("^.{");
    if (minLength != null) {
      pattern.append(minLength);
    }
    pattern.append(",");
    if (maxLength != null) {
      pattern.append(maxLength);
    }
    pattern.append("}$");

    return WireMock.matching(pattern.toString());
  }
}
