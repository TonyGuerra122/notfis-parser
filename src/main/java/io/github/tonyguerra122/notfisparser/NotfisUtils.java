package io.github.tonyguerra122.notfisparser;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.tonyguerra122.notfisgenerator.NotfisLine;

public final class NotfisUtils {
    public static JSONObject notfisLinesToJson(List<NotfisLine> lines) {
        final var result = new JSONObject();

        lines.forEach(line -> {
            final var fieldsArray = new JSONArray();

            line.getField().forEach(field -> {
                final var fieldJson = new JSONObject();
                fieldJson.put("name", field.getName());
                fieldJson.put("value", field.getValue());
                fieldJson.put("position", field.getPosition());
                fieldJson.put("size", field.getSize());
                fieldJson.put("mandatory", field.isMandatory());
                fieldsArray.put(fieldJson);
            });

            final String registerKey = String.format("%03d", line.getRegistration());

            if (result.has(registerKey)) {
                result.getJSONArray(registerKey).put(fieldsArray);
            } else {
                final var occurrences = new JSONArray();
                occurrences.put(fieldsArray);
                result.put(registerKey, occurrences);
            }
        });

        return result;
    }
}
