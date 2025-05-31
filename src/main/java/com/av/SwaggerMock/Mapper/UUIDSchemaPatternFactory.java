package com.av.SwaggerMock.Mapper;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDSchemaPatternFactory implements WireMockPatternFactory {
    @Override
    public boolean supports(Schema<?> schema) {
//        return schema instanceof UUIDSchema;
        return "string".equals(schema.getType()) && "uuid".equals(schema.getFormat());
    }

    @Override
    public StringValuePattern create(Schema<?> schema) {
        return WireMock.equalTo(String.valueOf(UUID.randomUUID()));
    }

    @Override
    public Class<? extends Schema<?>> getSchemaType() {
        return UUIDSchema.class;
    }
}
