package com.quality.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static volatile DatabaseConnection instance;

    private final String url;
    private final String username;
    private final String password;

    private DatabaseConnection() {
        try {
            Properties props = new Properties();
            InputStream input = getClass()
                    .getClassLoader()
                    .getResourceAsStream("db.properties");

            if (input == null) {
                throw new RuntimeException("Файл db.properties не найден в resources!");
            }

            props.load(input);
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");

            Class.forName(props.getProperty("db.driver"));
            System.out.println("[DB] Конфигурация загружена: " + url);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации БД: " + e.getMessage(), e);
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}