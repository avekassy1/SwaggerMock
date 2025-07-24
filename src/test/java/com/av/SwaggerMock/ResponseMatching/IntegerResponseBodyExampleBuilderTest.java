package com.av.SwaggerMock.ResponseMatching;

import static com.av.SwaggerMock.utils.Constants.EXAMPLE_INT;
import static org.junit.jupiter.api.Assertions.*;

import com.av.SwaggerMock.openapi.ExampleBuilder.IntegerResponseBodyExampleBuilder;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IntegerResponseBodyExampleBuilderTest {

  private final IntegerResponseBodyExampleBuilder builder = new IntegerResponseBodyExampleBuilder();
  private IntegerSchema schema;

  @BeforeEach
  void setUp() {
    schema = new IntegerSchema();
  }

  @Test
  void shouldSupportIntegerSchema() {
    assertTrue(builder.supports(new IntegerSchema()));
  }

  @Test
  void shouldNotSupportOtherSchemaTypes() {
    assertFalse(builder.supports(new Schema<>()));
    assertFalse(builder.supports(new StringSchema()));
  }

  @Test
  void shouldReturnExampleIfPresent() {
    schema.setExample(6);

    Object intExample = builder.build(schema);
    assertEquals(6, intExample);
  }

  @Test
  void shouldReturnExampleIntWhenNoExampleIsProvidedInSchema() {
    Object intExample = builder.build(schema);
    assertEquals(EXAMPLE_INT, intExample);
  }
}
