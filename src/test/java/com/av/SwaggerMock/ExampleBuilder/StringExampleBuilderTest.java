package com.av.SwaggerMock.ExampleBuilder;

import com.av.SwaggerMock.openapi.ExampleBuilder.StringResponseBodyExampleBuilder;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;

import static com.av.SwaggerMock.utils.Constants.EXAMPLE_STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringExampleBuilderTest {
    private final StringResponseBodyExampleBuilder builder = new StringResponseBodyExampleBuilder();

    @Test
    void shouldReturnExampleIfPresent() {
        StringSchema schema = new StringSchema();
        schema.setExample("Woo hoo");

        Object stringExample = builder.build(schema);
        assertEquals("Woo hoo", stringExample);
    }

    @Test
    void shouldReturn123AsAnExample() {
        StringSchema schema = new StringSchema();
        Object stringExample = builder.build(schema);
        assertEquals(EXAMPLE_STRING, stringExample);
    }
}
