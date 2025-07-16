package com.av.SwaggerMock.ExampleBuilder;

import com.av.SwaggerMock.openapi.ExampleBuilder.IntegerResponseBodyExampleBuilder;
import io.swagger.v3.oas.models.media.IntegerSchema;
import org.junit.jupiter.api.Test;

import static com.av.SwaggerMock.utils.Constants.EXAMPLE_INT;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegerResponseBodyExampleBuilderTest {

    private final IntegerResponseBodyExampleBuilder builder = new IntegerResponseBodyExampleBuilder();

    @Test
    void shouldReturnExampleIfPresent() {
        IntegerSchema schema = new IntegerSchema();
        schema.setExample(6);

        Object intExample = builder.build(schema);
        assertEquals(6, intExample);
    }

    @Test
    void shouldReturnExampleIntWhenNoExampleIsProvidedInSchema() {
        IntegerSchema schema = new IntegerSchema();
        Object intExample = builder.build(schema);
        assertEquals(EXAMPLE_INT, intExample);
    }
}
