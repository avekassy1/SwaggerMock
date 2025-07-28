package com.av.SwaggerMock.ResponseMatching;

import static com.av.SwaggerMock.utils.Constants.EXAMPLE_STRING;
import static org.junit.jupiter.api.Assertions.*;

import com.av.SwaggerMock.openapi.ExampleBuilder.StringResponseBodyExampleBuilder;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StringResponseBodyExampleBuilderTest {

  private final StringResponseBodyExampleBuilder builder = new StringResponseBodyExampleBuilder();
  private StringSchema schema;

  @BeforeEach
  void setUp() {
    schema = new StringSchema();
  }

  @Test
  void shouldSupportStringSchema() {
    assertTrue(builder.supports(new StringSchema()));
  }

  @Test
  void shouldNotSupportOtherSchemaTypes() {
    assertFalse(builder.supports(new Schema<>()));
    assertFalse(builder.supports(new IntegerSchema()));
  }

  @Test
  void shouldReturnExampleIfPresent() {
    schema.setExample("Test string");

    Object strExample = builder.build(schema);
    assertEquals("Test string", strExample);
  }

  @Test
  void shouldReturnExampleStrWhenNoExampleIsProvidedInSchema() {
    Object strExample = builder.build(schema);
    assertEquals(EXAMPLE_STRING, strExample);
  }
}
