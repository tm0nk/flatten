package org.example;

import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Flatten {

    public static void main(String[] args) throws IOException {
        System.out.println(new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create().toJson(parse(System.in)));
    }

    // package-private for testability, see tests in FlattenTest class
    static JsonObject parse(InputStream in) throws IOException {
        JsonObject result = new JsonObject();
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            JsonStreamParser parser = new JsonStreamParser(reader);
            while (parser.hasNext()) {
                JsonElement element = parser.next();
                if (element.isJsonObject()) {
                    flatten((JsonObject) element, result, "");
                } else {
                    throw new RuntimeException("Expected JsonObject but got " +
                            element.getClass().getName() + " instead");
                }
            }
        }
        return result;
    }

    // recursive function to flatten deeply nested json documents
    private static void flatten(JsonObject obj, JsonObject result, String prefix) {
        for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
            String k = prefix + e.getKey();
            JsonElement v = e.getValue();
            if (v.isJsonObject()) {
                flatten((JsonObject) v, result, k + ".");
            } else {
                result.add(k, v);
            }
        }
    }
}
