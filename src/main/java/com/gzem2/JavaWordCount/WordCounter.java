package com.gzem2.JavaWordCount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordCounter {
    private ArrayList<String> wordList;
    private LinkedHashMap<String, Integer> wordCount;
    Logger logger;

    public WordCounter() {
        logger = LoggerFactory.getLogger("JavaWordCount");
    }

    public ArrayList<String> getWordList() {
        return this.wordList;
    }

    public void setWordList(ArrayList<String> wordList) {
        this.wordList = wordList;
    }

    public LinkedHashMap<String, Integer> getWordCount() {
        return this.wordCount;
    }

    public void setWordCount(LinkedHashMap<String, Integer> wordCount) {
        this.wordCount = wordCount;
    }

    public String parseDelimiter(String delimiter) {
        Pattern p = Pattern.compile("'([^']+)',?");
        Matcher m = p.matcher(delimiter);
        List<String> matches = new ArrayList<>();

        while (m.find()) {
            String t = m.group(1);

            final String[] metaCharacters = { "\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|",
                    "<", ">", "-", "&", "%" };
            for (int i = 0; i < metaCharacters.length; i++) {
                if (t.contains(metaCharacters[i])) {
                    t = t.replace(metaCharacters[i], "\\" + metaCharacters[i]);
                }
            }

            t = t.replaceAll("\\s", "\\\\s+");

            matches.add(t);
        }

        matches = new ArrayList<>(new LinkedHashSet<>(matches));
        delimiter = String.join("|", matches.toArray(new String[0]));

        return delimiter;
    }

    public String scrapUrl(String requestUrl) {
        try {
            URL obj = new URL(requestUrl);
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Safari/537.36");


            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (IOException e) {
            logger.error("IO error", e);
            return "";
        }
    }

    public LinkedHashMap<String, Integer> countWords(String content, String delimiter) {
        String[] splitted = content.split(parseDelimiter(delimiter));
        wordCount = new LinkedHashMap<>();

        for (String s : splitted) {
            if (wordCount.containsKey(s)) {
                wordCount.put(s, wordCount.get(s) + 1);
            } else {
                wordCount.put(s, 1);
            }
        }
        return wordCount;
    }

    public LinkedHashMap<String, Integer> sortWords() {
        wordCount = wordCount.entrySet().stream().sorted(Entry.comparingByValue())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return wordCount;
    }
}
