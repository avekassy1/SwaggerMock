package com.av.SwaggerMock.Mapper;

import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.Schema;

public interface WireMockPatternFactory {

    boolean supports(Schema<?> schema);

    StringValuePattern create(Schema<?> schema);

    Class<? extends Schema<?>> getSchemaType();
}
