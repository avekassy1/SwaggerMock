package com.av.SwaggerMock.PatternBuilder;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SchemaToPatternBuilderDispatcher {

    private final List<WireMockPatternBuilder> builders;

    @Autowired
    public SchemaToPatternBuilderDispatcher(List<WireMockPatternBuilder> builders) {
        this.builders = builders;
    }

    public StringValuePattern createPattern(Schema<?> schema) {
        for (WireMockPatternBuilder builder : builders) {
            if (builder.supports(schema)) {
                return builder.create(schema);
            }
        }
        // Fallback if no builder supports this schema
        return WireMock.absent(); // TODO - understand fallback better
    }
}
