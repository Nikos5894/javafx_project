package com.example.toursystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:tours.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createTableQuery = """
                CREATE TABLE IF NOT EXISTS tours (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    date TEXT NOT NULL,
                    time TEXT NOT NULL,
                    accessibility TEXT
                );
                """;
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(createTableQuery)) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
