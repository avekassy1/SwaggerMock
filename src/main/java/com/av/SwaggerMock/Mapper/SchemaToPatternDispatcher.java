package com.av.SwaggerMock.Mapper;

import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SchemaToPatternDispatcher {
    private final Map<Class<? extends Schema<?>>, WireMockPatternFactory> factoryMap = new HashMap<>();

    @Autowired
    public SchemaToPatternDispatcher(List<WireMockPatternFactory> factories) {
        for (WireMockPatternFactory factory : factories) {
            factoryMap.put(factory.getSchemaType(), factory);
        }
    }

    public StringValuePattern createPattern(Schema<?> schema) {
        //if (schema == null) return WireMock.any(); // TODO - what is going on here?

        WireMockPatternFactory factory = factoryMap.get(schema.getClass());

        if (factory != null) {
            return factory.create(schema);
        }

        //return WireMock.anything(); // Fallback
        return null;
    }
}
