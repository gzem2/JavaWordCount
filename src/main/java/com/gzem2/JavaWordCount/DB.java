package com.gzem2.JavaWordCount;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DB {
    Connection conn;
    Logger logger;

    public DB() {
        Logger logger = LoggerFactory.getLogger("JavaWordCount");
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            conn = DriverManager
                    .getConnection("jdbc:hsqldb:file:HSQLDB/JavaWordCount;shutdown=true;hsqldb.write_delay=false;");
        } catch (Exception e) {
            logger.error("DB connection error", e);
        }
    }

    public void shutdown() throws SQLException {
        Statement st = conn.createStatement();
        st.execute("SHUTDOWN");
        conn.close();
    }

    public synchronized void update(String expression) throws SQLException {
        Statement st = null;

        st = conn.createStatement();

        int i = st.executeUpdate(expression);

        if (i == -1) {
            logger.error("DB error", expression);
        }

        st.close();
    }

    public synchronized int getUrlId(String url) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        int id = 0;

        st = conn.createStatement();

        rs = st.executeQuery("SELECT ID FROM URLS WHERE URL = '" + url + "'"); // run the query

        while (rs.next()) {
            if (rs.isLast()) {
                id = rs.getInt("ID");
            }
        }
        st.close();
        return id;
    }

    public void saveToDB(String url, LinkedHashMap<String, Integer> results) {
        try {
            update("CREATE TABLE IF NOT EXISTS URLS (ID INTEGER IDENTITY PRIMARY KEY, URL VARCHAR(255))");
            update("CREATE TABLE IF NOT EXISTS WORDS (ID INTEGER IDENTITY, URLID INTEGER NOT NULL, PRIMARY KEY(ID,URLID), WORD VARCHAR(255), COUNT INTEGER, CONSTRAINT FK1 FOREIGN KEY(URLID) REFERENCES URLS(ID))");

            update("INSERT INTO URLS(URL) VALUES ('" + url + "')");
            Integer id = getUrlId(url);
            conn.setAutoCommit(false);
            for (String key : results.keySet()) {
                PreparedStatement st = conn.prepareStatement("INSERT INTO WORDS(URLID, WORD, COUNT) VALUES (?, ?, ?)");
                st.setInt(1, id);
                st.setString(2, key);
                st.setInt(3, results.get(key));
                st.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            logger.error("DB error", e);
        }
    }
}
