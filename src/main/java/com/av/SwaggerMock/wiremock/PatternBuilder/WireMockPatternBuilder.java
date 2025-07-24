package com.av.SwaggerMock.wiremock.PatternBuilder;

import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.Schema;

public interface WireMockPatternBuilder {

  boolean supports(Schema<?> schema);

  StringValuePattern create(Schema<?> schema);

  Class<? extends Schema<?>> getSchemaType();
}
