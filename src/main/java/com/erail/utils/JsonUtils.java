package com.erail.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class JsonUtils {

    private static final Gson GSON = new Gson();

    private JsonUtils() {
    }

    public static <T> T readFromClasspath(String resourcePath, Class<T> type) {
        return readFromClasspath(resourcePath, (Type) type);
    }

    public static <T> T readFromClasspath(String resourcePath, Type type) {
        try (InputStream input = JsonUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("JSON file not found on classpath: " + resourcePath);
            }
            try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                T data = GSON.fromJson(reader, type);
                if (data == null) {
                    throw new IllegalStateException("JSON file is empty: " + resourcePath);
                }
                return data;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read JSON file: " + resourcePath, e);
        }
    }

    public static Map<String, String> readTestDataSetByCaseName(String resourcePath, String testCaseName) {
        Type mapType = TypeToken.getParameterized(Map.class, String.class, Map.class).getType();
        Map<String, Map<String, Object>> testDataMap = readFromClasspath(resourcePath, mapType);

        Map<String, Object> rawDataSet = testDataMap.get(testCaseName);
        if (rawDataSet == null) {
            throw new IllegalStateException(
                    "Test data not found for test case '" + testCaseName + "' in " + resourcePath);
        }

        Map<String, String> dataSet = new HashMap<>();
        rawDataSet.forEach((key, value) -> dataSet.put(key, toStringValue(value)));
        return dataSet;
    }

    private static String toStringValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Number) {
            Number number = (Number) value;
            double doubleValue = number.doubleValue();
            if (doubleValue == Math.rint(doubleValue)) {
                return String.valueOf((long) doubleValue);
            }
            return String.valueOf(number);
        }
        return String.valueOf(value);
    }
}
