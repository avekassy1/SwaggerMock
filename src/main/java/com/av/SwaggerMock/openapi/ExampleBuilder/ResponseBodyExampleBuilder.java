package com.av.SwaggerMock.openapi.ExampleBuilder;

import io.swagger.v3.oas.models.media.Schema;

public interface ResponseBodyExampleBuilder {
  boolean supports(Schema<?> schema);

  Object build(Schema<?> schema);

  Class<? extends Schema<?>> getSchemaType();
}
