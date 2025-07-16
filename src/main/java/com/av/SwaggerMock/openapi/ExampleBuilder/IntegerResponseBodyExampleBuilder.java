package com.av.SwaggerMock.openapi.ExampleBuilder;

import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.stereotype.Component;

import static com.av.SwaggerMock.utils.Constants.EXAMPLE_INT;

@Component
public class IntegerResponseBodyExampleBuilder implements ResponseBodyExampleBuilder {
    @Override
    public boolean supports(Schema<?> schema) {
        return schema instanceof IntegerSchema || (schema.getType() != null && schema.getType().equals("integer"));
    }

    @Override
    public Object build(Schema<?> integerSchema) {
        return integerSchema.getExample() != null
            ? integerSchema.getExample()
            : EXAMPLE_INT;
    }

    @Override
    public Class<? extends Schema<?>> getSchemaType() {
        return IntegerSchema.class;
    }
}
