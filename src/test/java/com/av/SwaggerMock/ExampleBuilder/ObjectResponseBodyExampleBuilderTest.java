package com.av.SwaggerMock.ExampleBuilder;

import com.av.SwaggerMock.SwaggerMockApplication;
import com.av.SwaggerMock.openapi.ExampleBuilder.ObjectResponseBodyExampleBuilder;
import com.av.SwaggerMock.openapi.ExampleBuilder.SchemaToExampleBuilderDispatcher;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static com.av.SwaggerMock.utils.Constants.EXAMPLE_INT;
import static com.av.SwaggerMock.utils.Constants.EXAMPLE_STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = SwaggerMockApplication.class)
public class ObjectResponseBodyExampleBuilderTest {

    @Autowired
    private ObjectResponseBodyExampleBuilder builder;

    @Autowired
    private SchemaToExampleBuilderDispatcher dispatcher;


    @Test
    void shouldReturnExampleIfPresent() {
        ObjectSchema schema = new ObjectSchema();
        schema.setExample(Map.of("foo", "bar"));

        Object result = builder.build(schema, dispatcher);

        assertEquals(Map.of("foo", "bar"), result);
    }

    @Test
    void shouldBuildObjectWithStringandIntForProperties() {
        ObjectSchema schema = new ObjectSchema();
        schema.addProperties("name", new StringSchema());
        schema.addProperties("age", new IntegerSchema());

        Object result = builder.build(schema, dispatcher);

        assertTrue(result instanceof Map);

        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertEquals(2, resultMap.size());
        assertEquals(EXAMPLE_STRING, resultMap.get("name"));
        assertEquals(EXAMPLE_INT, resultMap.get("age"));
    }
}
