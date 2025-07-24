package com.av.SwaggerMock.openapi.ExampleBuilder;

import io.swagger.v3.oas.models.media.Schema;

public interface RecursiveResponseBodyExampleBuilder extends ResponseBodyExampleBuilder {
  Object build(Schema<?> schema, SchemaToExampleBuilderDispatcher dispatcher);
}
