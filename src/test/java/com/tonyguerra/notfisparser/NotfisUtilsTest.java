package com.tonyguerra.notfisparser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.tonyguerra.notfisgenerator.NotfisLine;

class NotfisUtilsTest {

    @Test
    void shouldConvertLinesToJson() throws Exception {

        NotfisParser parser =
                new NotfisParser(new Properties(), NotfisParserTest.fakeLayouts());

        List<NotfisLine> lines = parser.parseNotfisLine("000ABC123");

        JSONObject json = NotfisUtils.notfisLinesToJson(lines);

        assertTrue(json.has("000"));

        JSONArray occurrences = json.getJSONArray("000");

        assertEquals(1, occurrences.length());
        assertEquals(3, occurrences.getJSONArray(0).length());
    }

    @Test
    void shouldGroupSameRegister() throws Exception {

        NotfisParser parser =
                new NotfisParser(new Properties(), NotfisParserTest.fakeLayouts());

        NotfisLine l1 = parser.parseNotfisLine("000ABC123").get(0);
        NotfisLine l2 = parser.parseNotfisLine("000DEF999").get(0);

        List<NotfisLine> lines = new ArrayList<>();
        lines.add(l1);
        lines.add(l2);

        JSONObject json = NotfisUtils.notfisLinesToJson(lines);

        JSONArray arr = json.getJSONArray("000");

        assertEquals(2, arr.length());
    }
}