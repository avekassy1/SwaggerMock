package com.av.SwaggerMock.PatternBuilder;

import com.av.SwaggerMock.wiremock.PatternBuilder.StringSchemaPatternBuilder;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.RegexPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringSchemaPatternBuilderTest {

    @Autowired
    private StringSchemaPatternBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new StringSchemaPatternBuilder();
    }

    @Test
    void shouldSupportsStringSchema() {
        assertTrue(builder.supports(new StringSchema()));
    }

    @Test
    void shouldUseSingleEnumAsConstant() {
        StringSchema schema = new StringSchema();
        schema.setEnum(Collections.singletonList("ONLY_ONE"));

        StringValuePattern pattern = builder.create(schema);
        assertTrue(pattern instanceof EqualToPattern);
        assertEquals("ONLY_ONE", ((EqualToPattern) pattern).getExpected());
    }

    @Test
    void shouldCreateMultipleEnumsRegexPattern() {
        StringSchema schema = new StringSchema();
        schema.setEnum(Arrays.asList("ENUM1", "ENUM2", "ENUM3"));

        StringValuePattern pattern = builder.create(schema);
        assertTrue(pattern instanceof RegexPattern);
        assertEquals("ENUM1|ENUM2|ENUM3", ((RegexPattern) pattern).getExpected());
    }

    @Test
    void shouldUseRegexPattern() {
        StringSchema schema = new StringSchema();
        schema.setPattern("^\\d{3}-\\d{2}-\\d{4}$");

        StringValuePattern pattern = builder.create(schema);
        assertTrue(pattern instanceof RegexPattern);
        assertEquals("^\\d{3}-\\d{2}-\\d{4}$", ((RegexPattern) pattern).getExpected());
    }

    @Test
    void shouldCreateMinAndMaxLengthPattern() {
        StringSchema schema = new StringSchema();
        schema.setMinLength(3);
        schema.setMaxLength(10);

        StringValuePattern pattern = builder.create(schema);
        assertTrue(pattern instanceof RegexPattern);
        assertEquals("^.{3,10}$", ((RegexPattern) pattern).getExpected());
    }

    @Test
    void testFallbackToMatchAnything() {
        StringSchema schema = new StringSchema();

        StringValuePattern pattern = builder.create(schema);
        assertTrue(pattern instanceof RegexPattern);
        assertEquals(".*", ((RegexPattern) pattern).getExpected());
    }
}
