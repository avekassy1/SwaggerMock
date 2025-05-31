package com.av.SwaggerMock.Mapper;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.stereotype.Component;

@Component
public class StringSchemaPatternFactory implements WireMockPatternFactory {
    @Override
    public boolean supports(Schema<?> schema) {
        return schema instanceof StringSchema;
    }

    @Override
    public StringValuePattern create(Schema<?> schema) {
        return WireMock.equalTo("temporary");
    }

    @Override
    public Class<? extends Schema<?>> getSchemaType() {
        return StringSchema.class;
    }
}
