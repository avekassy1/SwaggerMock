package com.av.SwaggerMock.openapi.ExampleBuilder;

import static com.av.SwaggerMock.utils.Constants.EXAMPLE_STRING;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.stereotype.Component;

@Component
public class StringResponseBodyExampleBuilder implements ResponseBodyExampleBuilder {
  @Override
  public boolean supports(Schema<?> schema) {
    return schema instanceof StringSchema
        || (schema.getType() != null && schema.getType().equals("string"));
  }

  @Override
  public Object build(Schema<?> stringSchema) {
    return stringSchema.getExample() != null ? stringSchema.getExample() : EXAMPLE_STRING;
  }

  @Override
  public Class<? extends Schema<?>> getSchemaType() {
    return StringSchema.class;
  }
}
