package com.av.SwaggerMock.PatternBuilder;

import com.av.SwaggerMock.wiremock.PatternBuilder.NumberSchemaPatternBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NumberSchemaPatternBuilderTest {

    @Autowired
    private NumberSchemaPatternBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new NumberSchemaPatternBuilder();
    }

    @Test
    void shouldSupportNumberSchema() {
        assertTrue(builder.supports(new NumberSchema()));
    }

    @Test
    void shouldNotSupportOtherSchemaTypes() {
        assertFalse(builder.supports(new Schema<>()));
        assertFalse(builder.supports(new StringSchema()));
    }

    @Test
    void shouldReturnMatchingPatternForEnumValues() {
        NumberSchema schema = new NumberSchema();
        schema.setEnum(List.of(BigDecimal.valueOf(1.23), BigDecimal.valueOf(4.56), BigDecimal.valueOf(7.89)));

        StringValuePattern pattern = builder.create(schema);

        assertTrue(pattern.match("1.23").isExactMatch());
        assertTrue(pattern.match("4.56").isExactMatch());
        assertTrue(pattern.match("7.89").isExactMatch());
        assertFalse(pattern.match("123").isExactMatch());
    }

    @Test
    void shouldEscapeSpecialCharactersInEnum() {
        NumberSchema schema = new NumberSchema();
        schema.setEnum(List.of(BigDecimal.valueOf(1.0), BigDecimal.valueOf(1.2), BigDecimal.valueOf(3.14)));

        StringValuePattern pattern = builder.create(schema);

        assertTrue(pattern.match("1.0").isExactMatch());
        assertTrue(pattern.match("1.2").isExactMatch());
        assertTrue(pattern.match("3.14").isExactMatch());
        assertFalse(pattern.match("2.71").isExactMatch());
    }

    @Test
    void shouldReturnCorrectRegexForGeneralNumber() {
        NumberSchema schema = new NumberSchema();
        StringValuePattern pattern = builder.create(schema);
        assertEquals("\"^-?\\\\d+(\\\\.\\\\d+)?$\";", pattern.getExpected());
    }
}
