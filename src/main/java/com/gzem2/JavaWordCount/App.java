package com.gzem2.JavaWordCount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App 
{
    public static void main( String[] args ) throws MalformedURLException, IOException
    {
        Logger logger = LoggerFactory.getLogger("JavaWordCount");

        WordCounter wordCounter = new WordCounter();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Url:");
        String url = in.readLine().trim();
        if (url.isEmpty()) {
            logger.error("Url cannot be empty");
            return;
        }

        System.out.print("Delimiter:");
        String delimiter = in.readLine().trim();
        if (delimiter.isEmpty()) {
            logger.error("Delimiter cannot be empty");
            return;
        }

        wordCounter.countWords(wordCounter.scrapUrl(url), delimiter);

        LinkedHashMap<String, Integer> results = wordCounter.sortWords();
        
        if (System.getProperty("os.name").startsWith("Windows")) {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "chcp", "65001").inheritIO();
            Process p;
            try {
                p = pb.start();
                p.waitFor();
            } catch (IOException e) {
                logger.error("IO error", e);
            } catch (InterruptedException e) {
                logger.error("Interrupted", e);
            }
        };

        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        for(String key : results.keySet()) {
            String kv = key + " - " + results.get(key);
            
            out.println(kv);
        }

        out.println("Saving to DB...");
        DB db = new DB();
        db.saveToDB(url, results);
    }
}
