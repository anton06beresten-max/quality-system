package com.quality.util;

import java.sql.SQLException;

public class DatabaseErrorHandler {

    public static String toUserMessage(SQLException e) {

        String sqlState = e.getSQLState();
        String message = e.getMessage().toLowerCase();

        // Нарушение уникальности
        if ("23505".equals(sqlState) || message.contains("duplicate")) {
            return "Такая запись уже существует.";
        }

        // Нарушение внешнего ключа
        if ("23503".equals(sqlState) || message.contains("foreign key")) {
            return "Операция невозможна: существуют связанные данные.";
        }

        // Нарушение NOT NULL
        if ("23502".equals(sqlState) || message.contains("not null")) {
            return "Не заполнены обязательные поля.";
        }

        // Нарушение CHECK
        if ("23514".equals(sqlState) || message.contains("check")) {
            return "Введённые данные не соответствуют допустимым значениям.";
        }

        // Прочие SQL-ошибки
        return "Ошибка базы данных. Проверьте корректность введённых данных.";
    }
}