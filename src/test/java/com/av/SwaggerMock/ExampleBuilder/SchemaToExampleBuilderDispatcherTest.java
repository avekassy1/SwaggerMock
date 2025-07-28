package com.av.SwaggerMock.ExampleBuilder;

import static org.junit.jupiter.api.Assertions.assertNull;

import com.av.SwaggerMock.openapi.ExampleBuilder.SchemaToExampleBuilderDispatcher;
import io.swagger.v3.oas.models.media.ArraySchema;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SchemaToExampleBuilderDispatcherTest {

  @Autowired private SchemaToExampleBuilderDispatcher dispatcher;

  @Test
  void shouldReturnNullIfSchemaIsNull() {
    assertNull(dispatcher.buildExample(null));
  }

  @Test
  void shouldReturnNullIfExampleBuilderIsNull() {
    assertNull(dispatcher.buildExample(new ArraySchema()));
  }
}
