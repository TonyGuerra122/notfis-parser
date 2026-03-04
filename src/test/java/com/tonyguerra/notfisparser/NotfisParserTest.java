package com.tonyguerra.notfisparser;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.tonyguerra.notfisgenerator.NotfisField;
import com.tonyguerra.notfisgenerator.NotfisLine;

class NotfisParserTest {

    @TempDir
    Path tempDir;

    @Test
    void constructor_shouldThrow_whenNotfisTypeIsNull() {
        var ex = assertThrows(IllegalArgumentException.class, () -> new NotfisParser(null));
        assertEquals("The notfisType is null", ex.getMessage());
    }

    @Test
    void parseNotfisLine_shouldParseFromString() throws Exception {
        NotfisParser parser = new NotfisParser(new Properties(), fakeLayouts());

        List<NotfisLine> lines = parser.parseNotfisLine("000ABC123");

        assertEquals(1, lines.size());
        NotfisLine line = lines.get(0);

        assertEquals(3, line.getField().size());

        assertField(line.getField().get(0), "reg", "000", (short)1, (short)3, true);
        assertField(line.getField().get(1), "name", "ABC", (short)4, (short)3, false);
        assertField(line.getField().get(2), "num", "123", (short)7, (short)3, false);
    }

    @Test
    void parseNotfisLine_shouldReturnEmpty_whenLineTooShort() throws Exception {
        NotfisParser parser = new NotfisParser(new Properties(), fakeLayouts());

        List<NotfisLine> lines = parser.parseNotfisLine("000ABC");

        NotfisLine line = lines.get(0);

        assertEquals("", line.getField().get(2).getValue());
    }

    @Test
    void parseNotfisLine_shouldThrow_whenLayoutMissing() throws Exception {
        NotfisParser parser = new NotfisParser(new Properties(), fakeLayouts());

        var ex = assertThrows(IllegalArgumentException.class,
                () -> parser.parseNotfisLine("999AAAA"));

        assertTrue(ex.getMessage().contains("Layout não encontrado"));
    }

    @Test
    void parseNotfisLine_shouldHandleNestedLayout() throws Exception {
        NotfisParser parser = new NotfisParser(new Properties(), fakeLayouts());

        List<NotfisLine> lines = parser.parseNotfisLine("001HELLO");

        assertEquals(2, lines.get(0).getField().size());
    }

    @Test
    void parseNotfisLine_shouldParseFromFile() throws Exception {
        NotfisParser parser = new NotfisParser(new Properties(), fakeLayouts());

        Path file = tempDir.resolve("notfis.txt");

        Files.writeString(file,
                "000ABC123" + System.lineSeparator() +
                "001HELLO");

        List<NotfisLine> lines = parser.parseNotfisLine(file);

        assertEquals(2, lines.size());
    }

    static Map<String, JSONArray> fakeLayouts() {

        Map<String, JSONArray> map = new HashMap<>();

        JSONArray layout000 = new JSONArray()
            .put(new JSONObject().put("name","reg").put("position",1).put("size",3).put("format","N").put("mandatory",true))
            .put(new JSONObject().put("name","name").put("position",4).put("size",3).put("format","A").put("mandatory",false))
            .put(new JSONObject().put("name","num").put("position",7).put("size",3).put("format","N").put("mandatory",false));

        JSONArray inner001 = new JSONArray()
            .put(new JSONObject().put("name","reg").put("position",1).put("size",3).put("format","N").put("mandatory",true))
            .put(new JSONObject().put("name","value").put("position",4).put("size",5).put("format","A").put("mandatory",false));

        JSONArray layout001 = new JSONArray().put(inner001);

        map.put("000", layout000);
        map.put("001", layout001);

        return map;
    }

    private static void assertField(NotfisField field, String name, String value,
                                    short pos, short size, boolean mandatory) {

        assertEquals(name, field.getName());
        assertEquals(value, field.getValue());
        assertEquals(pos, field.getPosition());
        assertEquals(size, field.getSize());
        assertEquals(mandatory, field.isMandatory());
    }
}