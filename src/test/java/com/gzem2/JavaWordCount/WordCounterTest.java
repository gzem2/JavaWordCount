package com.gzem2.JavaWordCount;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;

public class WordCounterTest {
    WordCounter counter = new WordCounter();
    //{' ', ',', '.', '! ', '?','"', ';', ':', '[', ']', '(', ')', '\n', '\r', '\t'}
    String delimiter = "{' ', ',', '.', '! ', '?','\"', ';', ':', '[', ']', '(', ')', '\n', '\r', '\t'}";
    String url = "https://www.simbirsoft.com/";

    @Test
    public void testParseDelimiter() {
        assertEquals("a", counter.parseDelimiter("{'a'}"));
        assertEquals("a|b", counter.parseDelimiter("{'a', 'b'}"));
        assertEquals("a|b", counter.parseDelimiter("{'a','b'}"));

        assertEquals("\\s+|,|\\.|!\\s+|\\?|\"|;|:|\\[|\\]|\\(|\\)", counter.parseDelimiter(delimiter));         
    }

    @Test
    public void testCountWords() throws UnsupportedEncodingException {
        counter.countWords(counter.scrapUrl(url), delimiter);
        
        LinkedHashMap<String, Integer> results = counter.sortWords();

        assertTrue(results.containsKey("сайт") && results.get("сайт") != null);
        assertTrue(results.containsKey("email") && results.get("email") != null);
    }

    @Test
    public void testSaveToDB() {
        DB db = new DB();

        counter.countWords(counter.scrapUrl(url), delimiter);
        LinkedHashMap<String, Integer> results = counter.sortWords();

        db.saveToDB(url, results);
    }
}
