package com.example.ilms.dto.submission;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Pattern;

/** Field-level decoding only; leaves the project's ObjectMapper unchanged. */
public final class SubmissionJsonDeserializer {
    private static final Pattern UUID_PATTERN = Pattern.compile(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

    private SubmissionJsonDeserializer() { }

    public static UUID parseId(String value) {
        if (value == null || !UUID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("UUID không hợp lệ");
        }
        return UUID.fromString(value);
    }

    public static class Text extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (!parser.hasToken(JsonToken.VALUE_STRING)) {
                return (String) context.handleUnexpectedToken(String.class, parser);
            }
            return parser.getText();
        }
    }

    public static class Decimal extends JsonDeserializer<BigDecimal> {
        @Override
        public BigDecimal deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (!parser.currentToken().isNumeric()) {
                return (BigDecimal) context.handleUnexpectedToken(BigDecimal.class, parser);
            }
            return parser.getDecimalValue();
        }
    }

    public static class Id extends JsonDeserializer<UUID> {
        @Override
        public UUID deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            if (!parser.hasToken(JsonToken.VALUE_STRING)) {
                return (UUID) context.handleUnexpectedToken(UUID.class, parser);
            }
            try {
                return parseId(parser.getText());
            } catch (IllegalArgumentException ex) {
                return (UUID) context.handleWeirdStringValue(UUID.class, parser.getText(), "UUID không hợp lệ");
            }
        }
    }
}
