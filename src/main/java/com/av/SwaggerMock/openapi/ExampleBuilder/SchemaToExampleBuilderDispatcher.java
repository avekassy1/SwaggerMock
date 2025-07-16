package com.av.SwaggerMock.openapi.ExampleBuilder;

import io.swagger.v3.oas.models.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SchemaToExampleBuilderDispatcher {

    private final Map<Class<? extends Schema<?>>, ResponseBodyExampleBuilder> buildersMap = new HashMap<>();

    @Autowired
    public SchemaToExampleBuilderDispatcher(List<ResponseBodyExampleBuilder> builders) {
        for (ResponseBodyExampleBuilder builder : builders) {
            buildersMap.put(builder.getSchemaType(), builder);
        }
    }

    public Object buildExample(Schema<?> schema) {
        if (schema == null) return null;

        ResponseBodyExampleBuilder exampleBuilder = buildersMap.get(schema.getClass());
        if (exampleBuilder == null) {
            throw new UnsupportedOperationException("No ResponseBodyBuilder found for schema type: " + schema.getClass());
        }
        if (exampleBuilder instanceof RecursiveResponseBodyExampleBuilder recursiveBuilder) {
            return recursiveBuilder.build(schema, this);
        }

        return exampleBuilder.build(schema);

    }
}
