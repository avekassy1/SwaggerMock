package com.av.SwaggerMock.PatternBuilder;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class NumberSchemaPatternBuilder implements WireMockPatternBuilder {
    @Override
    public boolean supports(Schema<?> schema) {
        return schema instanceof NumberSchema;
    }

    @Override
    public StringValuePattern create(Schema<?> schema) {

        List<?> enumValues = schema.getEnum();


        // ENUMs - match any of them
        if (enumValues != null && !enumValues.isEmpty()) {
            String joined = enumValues.stream()
                .map(Object::toString)
                .map(Pattern::quote) // Escape to avoid regex issues
                .collect(Collectors.joining("|"));
            return WireMock.matching("^(" + joined + ")$");
        }

        // BigDecimal maximum = schema.getMaximum();
        // BigDecimal minimum = schema.getMinimum();
        // Boolean exclusiveMaximum = schema.getExclusiveMaximum();
        // Boolean exclusiveMinimum = schema.getExclusiveMinimum();
        // BigDecimal multipleOf = schema.getMultipleOf();

        // TODO - implement customer matcher. No regexp will be sufficient for checking multipleOf

        return WireMock.matching("\"^-?\\\\d+(\\\\.\\\\d+)?$\";"); // Allows optinal minus sign and optional decimal part
    }

    @Override
    public Class<? extends Schema<?>> getSchemaType() {
        return NumberSchema.class;
    }
}
