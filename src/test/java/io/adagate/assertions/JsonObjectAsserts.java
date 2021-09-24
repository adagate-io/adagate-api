package io.adagate.assertions;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;

import java.util.function.Consumer;

import static io.vertx.core.buffer.Buffer.buffer;
import static io.vertx.core.json.Json.decodeValue;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.fail;

public final class JsonObjectAsserts {

    /**
     * Decodes a given byte array to a {@link JsonObject}.
     * @param decompressedData Defines the compress Json data.
     * @return {@link JsonObject}
     */
    public static JsonObject toJsonObject(byte[] decompressedData) {
        return (JsonObject) decodeValue(buffer(decompressedData));
    }

    /**
     * Decodes a given byte array to a {@link JsonArray}.
     * @param decompressedData Defines the compress Json data.
     * @return {@link JsonArray}
     */
    public static JsonArray toJsonArray(byte[] decompressedData) {
        return (JsonArray) decodeValue(buffer(decompressedData));
    }

    /**
     * Tests whether the given field has a <b>null</b> value.
     * @param jsonField Defines the name of the JSON field.
     * @return {@link Consumer} for the {@link JsonObject}.
     */
    public static Consumer<JsonObject> assertNullField(String jsonField) {
        return (res) -> Assertions.assertNull(res.getValue(jsonField), format("Found '%s' value for '%s' field", res.getValue(jsonField), jsonField));
    }

    /**
     * Tests whether the given field has a <b>non-null</b> value.
     * @param jsonField Defines the name of the JSON field.
     * @return {@link Consumer} for the {@link JsonObject}.
     */
    public static Consumer<JsonObject> assertNotNullField(String jsonField) {
        return (res) -> Assertions.assertNotNull(res.getValue(jsonField), format("Found null value for '%s' field", jsonField));
    }

    /**
     * Tests for a <code>String</code> field to be of an expected value.
     * @param jsonStringField Defines the {@link String} field name.
     * @param expected Defines the expected {@link String} value.
     * @return {@link JsonObject} that shall be tested.
     */
    public static Consumer<JsonObject> assertFieldEquals(String jsonStringField, String expected) {
        return (res) -> {
            try {
                Assertions
                    .assertEquals(
                        expected,
                        res.getString(jsonStringField),
                        format("Mismatching '%s' field", jsonStringField)
                    );
            } catch (ClassCastException e) {
                fail(e);
            }
        };
    }

    /**
     * Tests for a <code>Integer</code> field to be of an expected value.
     * @param jsonIntegerField Defines the {@link Integer} field name.
     * @param expected Defines the expected {@link Integer} value.
     * @return {@link JsonObject} that shall be tested.
     */
    public static Consumer<JsonObject> assertFieldEquals(String jsonIntegerField, int expected) {
        return (res) -> {
            try {
                Assertions
                    .assertEquals(
                        expected,
                        res.getInteger(jsonIntegerField),
                        format("Mismatching '%s' field", jsonIntegerField)
                    );
            } catch (ClassCastException e) {
                fail(e);
            }
        };
    }
}
