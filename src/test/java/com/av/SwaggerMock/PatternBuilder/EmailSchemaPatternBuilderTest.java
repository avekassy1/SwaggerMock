package com.av.SwaggerMock.PatternBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.av.SwaggerMock.wiremock.PatternBuilder.EmailSchemaPatternBuilder;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.EmailSchema;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailSchemaPatternBuilderTest {

  private static final Pattern EMAIL_REGEX = Pattern.compile("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$");

  @Autowired EmailSchemaPatternBuilder builder;

  @BeforeEach
  void setUp() {
    builder = new EmailSchemaPatternBuilder();
  }

  @Test
  void shouldSupportEmailSchema() {
    assertTrue(builder.supports(new EmailSchema()));
  }

  @Test
  void shouldCreateEmailRegexPattern() {
    EmailSchema schema = new EmailSchema();

    StringValuePattern pattern = builder.create(schema);
    assertTrue(pattern instanceof RegexPattern);

    String expectedRegex = EMAIL_REGEX.pattern();
    String actualRegex = ((RegexPattern) pattern).getExpected();
    assertEquals(expectedRegex, actualRegex);
  }
}
