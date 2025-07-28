package com.av.SwaggerMock.wiremock.PatternBuilder;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.EmailSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class EmailSchemaPatternBuilder implements WireMockPatternBuilder {

  private static final Pattern EMAIL_REGEX = Pattern.compile("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$");

  @Override
  public boolean supports(Schema<?> schema) {
    return schema instanceof EmailSchema && "email".equals(schema.getFormat());
  }

  @Override
  public StringValuePattern create(Schema<?> schema) {
    return WireMock.matching(EMAIL_REGEX.pattern());
  }

  @Override
  public Class<? extends Schema<?>> getSchemaType() {
    return EmailSchema.class;
  }
}
