package io.github.tonyguerra122.notfisparser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.tonyguerra122.notfisgenerator.NotfisField;
import io.github.tonyguerra122.notfisgenerator.NotfisFieldType;
import io.github.tonyguerra122.notfisgenerator.NotfisLine;
import io.github.tonyguerra122.notfisgenerator.NotfisType;

public final class NotfisParser {

    private final Properties props;
    private final Map<String, JSONArray> layouts;

    public NotfisParser(NotfisType notfisType) throws Exception {
        if (notfisType == null) {
            throw new IllegalArgumentException("The notfisType is null");
        }

        this.props = loadProperties();
        this.layouts = loadLayouts(notfisType);
    }

    private Properties loadProperties() throws IOException {
        final var props = new Properties();
        try (final var input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("application.properties não encontrado.");
            }
            props.load(input);
        }
        return props;
    }

    private Map<String, JSONArray> loadLayouts(NotfisType notfisType) throws IOException {
        final String versionKey = notfisType.equals(NotfisType.VERSION31) ? "version-3.1" : "version-5.0";
        final String layoutPath = props.getProperty(versionKey).replace("classpath:", "");

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(layoutPath)) {
            if (inputStream == null) {
                throw new IOException("Layout de configuração não encontrado: " + layoutPath);
            }
            String content = new String(inputStream.readAllBytes());
            JSONObject json = new JSONObject(content);

            final Map<String, JSONArray> layoutMap = new HashMap<>();
            json.keySet().forEach(key -> layoutMap.put(key, json.getJSONArray(key)));

            return layoutMap;
        }
    }

    public List<NotfisLine> parseNotfisLine(String content) throws IOException {
        final List<String> lines = Arrays.asList(content.split("\\R"));
        return lines.stream().map(this::parseLine).collect(Collectors.toList());
    }

    public List<NotfisLine> parseNotfisLine(Path filePath) throws IOException {
        final String content = Files.readAllLines(filePath)
                .stream()
                .collect(Collectors.joining(System.lineSeparator()));

        return parseNotfisLine(content);
    }

    private NotfisLine parseLine(String line) {
        final String recordType = line.substring(0, 3);
        var layout = layouts.get(recordType);

        if (layout == null) {
            throw new IllegalArgumentException("Layout não encontrado para o tipo de registro: " + recordType);
        }

        if (layout.length() == 1 && layout.get(0) instanceof JSONArray) {
            layout = layout.getJSONArray(0);
        }

        final List<NotfisField> fields = new ArrayList<>();

        for (int i = 0; i < layout.length(); i++) {
            final var fieldConfig = layout.getJSONObject(i);

            final String name = fieldConfig.getString("name");
            final short position = (short) fieldConfig.getInt("position");
            final NotfisFieldType format = fieldConfig.getString("format").equals("A")
                    ? NotfisFieldType.ALPHANUMERIC
                    : NotfisFieldType.NUMERIC;
            final short size = (short) fieldConfig.getInt("size");
            final boolean mandatory = fieldConfig.optBoolean("mandatory");

            final Object value = line.length() >= position + size - 1
                    ? line.substring(position - 1, Math.min(position + size - 1, line.length())).trim()
                    : "";

            final var field = new NotfisField(name, format, position, size, mandatory, value);
            fields.add(field);
        }

        return new NotfisLine(recordType, fields);
    }

}
