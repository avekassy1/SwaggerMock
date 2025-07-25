package com.av.SwaggerMock.PatternBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.av.SwaggerMock.wiremock.PatternBuilder.UUIDSchemaPatternBuilder;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.UUIDSchema;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UUIDSchemaPatternBuilderTest {

  @Autowired UUIDSchemaPatternBuilder builder;

  private static final Pattern UUID_REGEX =
      Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

  @BeforeEach
  void setUp() {
    builder = new UUIDSchemaPatternBuilder();
  }

  @Test
  void shouldSupportUUIDSchema() {
    assertTrue(builder.supports(new UUIDSchema()));
  }

  @Test
  void shouldCreateUUIDRegexPattern() {
    UUIDSchema schema = new UUIDSchema();

    StringValuePattern pattern = builder.create(schema);
    assertTrue(pattern instanceof RegexPattern);

    String expectedRegex = UUID_REGEX.pattern();
    String actualRegex = ((RegexPattern) pattern).getExpected();
    assertEquals(expectedRegex, actualRegex);
  }

  @Test
  void shouldReturnUUIDSchemaClass() {
    assertEquals(UUIDSchema.class, builder.getSchemaType());
  }
}
