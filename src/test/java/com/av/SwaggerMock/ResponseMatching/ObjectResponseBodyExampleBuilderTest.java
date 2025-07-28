package com.av.SwaggerMock.ResponseMatching;

import static com.av.SwaggerMock.utils.Constants.EXAMPLE_INT;
import static com.av.SwaggerMock.utils.Constants.EXAMPLE_STRING;
import static org.junit.jupiter.api.Assertions.*;

import com.av.SwaggerMock.SwaggerMockApplication;
import com.av.SwaggerMock.openapi.ExampleBuilder.ObjectResponseBodyExampleBuilder;
import com.av.SwaggerMock.openapi.ExampleBuilder.SchemaToExampleBuilderDispatcher;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SwaggerMockApplication.class)
public class ObjectResponseBodyExampleBuilderTest {

  @Autowired private ObjectResponseBodyExampleBuilder builder;

  @Autowired private SchemaToExampleBuilderDispatcher dispatcher;

  @Test
  void shouldSupportObjectSchema() {
    assertTrue(builder.supports(new ObjectSchema()));
  }

  @Test
  void shouldNotSupportOtherSchemaTypes() {
    assertFalse(builder.supports(new Schema<>()));
    assertFalse(builder.supports(new StringSchema()));
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionWhenBuilderIsCalledWithNoDispatcher() {
    assertThrows(UnsupportedOperationException.class, () -> builder.build(new Schema<>()));
  }

  @Test
  void shouldReturnExampleIfPresent() {
    ObjectSchema schema = new ObjectSchema();
    schema.setExample(Map.of("foo", "bar"));

    Object result = builder.build(schema, dispatcher);

    assertEquals(Map.of("foo", "bar"), result);
  }

  @Test
  void shouldBuildObjectWithStringAndIntForProperties() {
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
