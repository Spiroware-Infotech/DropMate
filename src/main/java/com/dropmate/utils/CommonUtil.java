package com.dropmate.utils;

import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class CommonUtil {

    private static final Logger log = LoggerFactory.getLogger(CommonUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT); // optional: pretty print

    private CommonUtil() {
        // private constructor to prevent instantiation
    }

    /**
     * Convert any object to JSON string.
     */
    public static String convertToJsonString(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON string", e);
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    /**
     * Convert JSON string to an object of given class.
     */
    public static <T> T fromJsonString(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Error converting JSON string to object: {}", clazz.getSimpleName(), e);
            throw new RuntimeException("JSON deserialization failed", e);
        }
    }

    /**
     * Convert JSON string to a List of objects (generic).
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            log.error("Error converting JSON string to List<{}>", clazz.getSimpleName(), e);
            throw new RuntimeException("JSON deserialization to list failed", e);
        }
    }

    /**
     * Convert JSON string to any complex generic type using TypeReference.
     * (Useful for Map<String, Object>, List<Map<String, Object>>, etc.)
     */
    public static <T> T fromJsonString(String json, TypeReference<T> typeRef) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            log.error("Error converting JSON string to TypeReference object", e);
            throw new RuntimeException("JSON deserialization (TypeReference) failed", e);
        }
    }

    /**
     * Pretty print JSON (optional helper).
     */
    public static String prettyPrint(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return String.valueOf(obj);
        }
    }
}
