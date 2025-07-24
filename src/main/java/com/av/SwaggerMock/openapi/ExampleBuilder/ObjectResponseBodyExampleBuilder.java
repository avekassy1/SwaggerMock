package com.av.SwaggerMock.openapi.ExampleBuilder;

import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ObjectResponseBodyExampleBuilder implements RecursiveResponseBodyExampleBuilder {

  @Override
  public boolean supports(Schema<?> schema) {
    return schema instanceof ObjectSchema
        || (schema.getType() != null && schema.getType().equals("object"));
  }

  @Override
  public Object build(Schema<?> schema, SchemaToExampleBuilderDispatcher dispatcher) {
    if (schema.getExample() != null) {
      return schema.getExample();
    }

    Map<String, Object> result = new LinkedHashMap<>();
    if (schema.getProperties() != null) {
      schema
          .getProperties()
          .forEach(
              (name, prop) ->
                  // When running as a test,
                  result.put(name, dispatcher.buildExample((Schema<?>) prop)));
    }

    return result;
  }

  @Override
  public Object build(Schema<?> schema) {
    throw new UnsupportedOperationException(
        "Recursive builders should use build(schema, dispatcher)");
  }

  @Override
  public Class<? extends Schema<?>> getSchemaType() {
    return ObjectSchema.class;
  }
}
