package com.av.SwaggerMock.wiremock.PatternBuilder;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class UUIDSchemaPatternBuilder implements WireMockPatternBuilder {
  private static final Pattern UUID_REGEX =
      Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

  @Override
  public boolean supports(Schema<?> schema) {
    return schema instanceof UUIDSchema && "uuid".equals(schema.getFormat());
  }

  @Override
  public StringValuePattern create(Schema<?> schema) {
    return WireMock.matching(UUID_REGEX.pattern());
  }

  @Override
  public Class<? extends Schema<?>> getSchemaType() {
    return UUIDSchema.class;
  }
}
