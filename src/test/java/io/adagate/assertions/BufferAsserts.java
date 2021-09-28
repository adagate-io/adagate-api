package io.adagate.assertions;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.adagate.assertions.JsonObjectAsserts.toJsonArray;
import static io.adagate.assertions.JsonObjectAsserts.toJsonObject;
import static io.adagate.utils.GZipUtils.decompress;
import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.fail;

public final class BufferAsserts {

    /**
     * Tests whether the given field has a <b>null</b> value.
     * @param jsonField Defines the name of the JSON field.
     * @return {@link Consumer} for the response buffer.
     */
    public static Consumer<HttpResponse<Buffer>> assertNullField(String jsonField) {
        return (res) -> {
            try {
                Assertions.assertNull(toJsonObject(decompress(res.body())).getValue(jsonField), format("Found non null value for '%s' field", jsonField));
            } catch (IOException e) {
                fail(e);
            }
        };
    }

    /**
     * Tests whether the given field has a <b>non-null</b> value.
     * @param jsonField Defines the name of the JSON field.
     * @return {@link Consumer} for response buffer.
     */
    public static Consumer<HttpResponse<Buffer>> assertNonNullField(String jsonField) {
        return (res) -> {
            try {
                Assertions.assertNotNull(toJsonObject(decompress(res.body())).getValue(jsonField), format("Found null for '%s' field", jsonField));
            } catch (IOException e) {
                fail(e);
            }
        };
    }

    /**
     * Tests for a <code>Integer</code> field to be equal to the given expected value.
     * @param jsonIntegerField Defines the JSON field name to get the actual.
     * @param expected Defines the expected value.
     * @return {@link Consumer} for response buffer.
     */
    public static Consumer<HttpResponse<Buffer>> assertFieldEquals(String jsonIntegerField, int expected) {
        return (res) -> {
            try {
                Assertions.assertEquals(expected, toJsonObject(decompress(res.body())).getInteger(jsonIntegerField), format("Mismatching '%s' field", jsonIntegerField));
            } catch (ClassCastException | IOException e) {
                fail(e);
            }
        };
    }

    /**
     * Tests for a <code>Integer</code> field to be equal to the given expected value.
     * @param jsonIntegerField Defines the JSON field name to get the actual.
     * @param expected Defines the expected value.
     * @return {@link Consumer} for response buffer.
     */
    public static Consumer<HttpResponse<Buffer>> assertFieldGreaterOrEquals(String jsonIntegerField, int expected) {
        return (res) -> {
            try {
                final int actual = toJsonObject(decompress(res.body())).getInteger(jsonIntegerField);
                Assertions.assertTrue(actual >= expected, format("Expected '%s' field: >= %d (actual: %d)", jsonIntegerField, expected, actual));
            } catch (ClassCastException | IOException e) {
                fail(e);
            }
        };
    }

    /**
     * Tests for a <code>Boolean</code> field to be equal to the given expected value.
     * @param jsonBoolField Defines the JSON field name to get the actual.
     * @param expected Defines the expected value.
     * @return {@link Consumer} for response buffer.
     */
    public static Consumer<HttpResponse<Buffer>> assertFieldEquals(String jsonBoolField, boolean expected) {
        return (res) -> {
            try {
                Assertions.assertEquals(expected, toJsonObject(decompress(res.body())).getBoolean(jsonBoolField), format("Mismatching '%s' field", jsonBoolField));
            } catch (ClassCastException | IOException e) {
                fail(e);
            }
        };
    }

    /**
     * Tests for a <code>Double</code> field to be equal to the given expected value.
     * @param jsonDoubleField Defines the JSON field name to get the actual.
     * @param expected Defines the expected value.
     * @return {@link Consumer} for response buffer.
     */
    public static Consumer<HttpResponse<Buffer>> assertFieldEquals(String jsonDoubleField, double expected) {
        return (res) -> {
            try {
                Assertions.assertEquals(expected, toJsonObject(decompress(res.body())).getDouble(jsonDoubleField), format("Mismatching '%s' field", jsonDoubleField));
            } catch (ClassCastException | IOException e) {
                fail(e);
            }
        };
    }

    /**
     * Tests for a <code>non null</code> field to be of an expected type.
     * @param jsonField Defines the JSON field name to get the actual.
     * @param expectedType Defines the expected type.
     * @return {@link Consumer} for response buffer.
     */
    public static Consumer<HttpResponse<Buffer>> assertNonNullField(String jsonField, Class<?> expectedType) {
        return (res) -> {
            try {
                Assertions.assertNotNull(expectedType.cast(toJsonObject(decompress(res.body())).getValue(jsonField)), format("Mismatching type for '%s' field", jsonField));
            } catch (ClassCastException | IOException e) {
                fail(e);
            }
        };
    }

    /**
     * Expects a given buffer encoding a {@link JsonArray}, will otherwise fail.
     * @param asserts Defines the list of assertions to be run against the first array element.
     * @return {@link Consumer} for response buffer.
     */
    @SafeVarargs
    public static Consumer<HttpResponse<Buffer>> expectFirstArrayElement(Consumer<JsonObject>... asserts) {
        return (res) -> {
            try {
                final JsonArray jsonArray = toJsonArray(decompress(res.body()));
                Assertions.assertFalse(jsonArray.isEmpty());

                for (Consumer<JsonObject> jsonAssert : asserts) {
                    jsonAssert.accept(jsonArray.getJsonObject(0));
                }
            } catch (IOException e) {
                fail(e);
            }
        };
    }

    /**
     * Expects a given buffer encoding a {@link JsonArray}, will otherwise fail.
     * @param pos Defines the position of the element to run the assertions against.
     * @param asserts Defines the list of assertions to be run against the nth array element.
     * @return {@link Consumer} for response buffer.
     */
    @SafeVarargs
    public static Consumer<HttpResponse<Buffer>> expectNthArrayElement(int pos, Consumer<JsonObject>... asserts) {
        return (res) -> {
            try {
                final JsonArray jsonArray = toJsonArray(decompress(res.body()));
                Assertions.assertFalse(jsonArray.isEmpty());

                for (Consumer<JsonObject> jsonAssert : asserts) {
                    jsonAssert.accept(jsonArray.getJsonObject(pos));
                }
            } catch (IOException e) {
                fail(e);
            }
        };
    }

    /**
     * Expects a given buffer encoding a {@link JsonArray}, will otherwise fail.
     * @param expectedLength Defines the expected array length.
     * @return {@link Consumer} for response buffer.
     */
    public static Consumer<HttpResponse<Buffer>> assertFieldArrayLengthEquals(String fieldName, int expectedLength) {
        return (res) -> {
            try {
                final JsonArray actual = toJsonObject(decompress(res.body())).getJsonArray(fieldName);
                Assertions.assertEquals(expectedLength, actual.size(), "Unequal size of elements");
            } catch (IOException e) {
                fail(e);
            }
        };
    }

    /**
     * Expects a given buffer encoding a {@link JsonArray}, will otherwise fail.
     * @param expectedLength Defines the expected array length.
     * @return {@link Consumer} for response buffer.
     */
    public static Consumer<HttpResponse<Buffer>> assertArrayLengthEquals(int expectedLength) {
        return (res) -> {
            try {
                final JsonArray actual = toJsonArray(decompress(res.body()));
                Assertions.assertEquals(expectedLength, actual.size(), "Unequal size of elements");
            } catch (IOException e) {
                fail(e);
            }
        };
    }

    /**
     * Expects a given buffer encoding a {@link JsonArray}, will otherwise fail.
     * @param expected Defines the expected {@link JsonArray}.
     * @return {@link Consumer} for response buffer.
     */
    public static Consumer<HttpResponse<Buffer>> assertEquals(JsonArray expected) {
        return (res) -> {
            try {
                final JsonArray actual = toJsonArray(decompress(res.body()));
                Assertions.assertEquals(expected.encodePrettily(), actual.encodePrettily());
            } catch (IOException e) {
                fail(e);
            }
        };
    }

    /**
     * Expects a given buffer encoding a {@link JsonArray}, will otherwise fail.
     * @param arrayFieldName Defines the field name where to given expect {@link JsonArray}.
     * @param expected Defines the expected {@link JsonArray}.
     * @return {@link Consumer} for response buffer.
     */
    public static Consumer<HttpResponse<Buffer>> assertFieldEquals(String arrayFieldName, JsonArray expected) {
        return (res) -> {
            try {
                final JsonArray actual = toJsonObject(decompress(res.body())).getJsonArray(arrayFieldName);
                Assertions.assertEquals(expected.encodePrettily(), actual.encodePrettily());
            } catch (IOException e) {
                fail(e);
            }
        };
    }

    /**
     * Expects a given buffer encoding a {@link JsonObject}, will otherwise fail.
     * @param jsonObjectFieldName Defines the field name where to given expect {@link JsonObject}.
     * @param expected Defines the expected {@link JsonObject}.
     * @return {@link Consumer} for response buffer.
     */
    public static Consumer<HttpResponse<Buffer>> assertFieldEquals(String jsonObjectFieldName, JsonObject expected) {
        return (res) -> {
            JsonObject actual = new JsonObject();
            try {
                actual = toJsonObject(decompress(res.body())).getJsonObject(jsonObjectFieldName);
                final Set<String> expectedFieldNames = expected.fieldNames();
                for (String field: expectedFieldNames) {
                    Assertions.assertEquals(expected.getValue(field), actual.getValue(field), format("Unequal value for field: '%s'", field));
                }

                final Set<String> unexpectedFields = actual.fieldNames().stream().filter(f -> ! expectedFieldNames.contains(f)).collect(Collectors.toSet());
                Assertions.assertEquals(emptySet(), unexpectedFields);
            } catch (IOException e) {
                fail(e);
            } catch (NullPointerException e) {
                fail(format("Could not retrieve '%s' from json object: %s", jsonObjectFieldName, actual.encodePrettily()), e);
            }
        };
    }

    /**
     * Tests for a <code>String</code> field to be of an expected value.
     * @param jsonStringField Defines the json field name.
     * @param expected Defines the expected {@link String} value.
     * @return {@link Consumer} for response buffer.
     */
    public static Consumer<HttpResponse<Buffer>> assertFieldEquals(String jsonStringField, String expected) {
        return (res) -> {
            try {
                Assertions
                    .assertEquals(
                        expected,
                        toJsonObject(decompress(res.body())).getString(jsonStringField),
                        format("Mismatching '%s' field", jsonStringField)
                    );
            } catch (ClassCastException | IOException | NullPointerException e) {
                fail(e);
            }
        };
    }
}
