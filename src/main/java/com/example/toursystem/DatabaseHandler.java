package com.example.toursystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    private static final String DB_URL = "jdbc:sqlite:tours.db";

    // Ініціалізація бази даних
    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String createRoutesTable = "CREATE TABLE IF NOT EXISTS routes ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL);";

            String createRoutePointsTable = "CREATE TABLE IF NOT EXISTS route_points ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "route_id INTEGER NOT NULL, "
                    + "latitude REAL NOT NULL, "
                    + "longitude REAL NOT NULL, "
                    + "FOREIGN KEY (route_id) REFERENCES routes (id));";

            conn.createStatement().execute(createRoutesTable);
            conn.createStatement().execute(createRoutePointsTable);

            System.out.println("База даних готова!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int addRoute(String name) {
        String sql = "INSERT INTO routes (name) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.executeUpdate();

            var rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Повертаємо ID маршруту
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void addRoutePoint(int routeId, double latitude, double longitude) {
        String sql = "INSERT INTO route_points (route_id, latitude, longitude) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, routeId);
            pstmt.setDouble(2, latitude);
            pstmt.setDouble(3, longitude);

            pstmt.executeUpdate();
            System.out.println("Точка додана: [" + latitude + ", " + longitude + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для отримання точок маршруту за ID маршруту
    public static List<double[]> getRoutePoints(int routeId) {
        List<double[]> points = new ArrayList<>();
        String sql = "SELECT latitude, longitude FROM route_points WHERE route_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, routeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                double latitude = rs.getDouble("latitude");
                double longitude = rs.getDouble("longitude");
                points.add(new double[]{latitude, longitude});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return points;
    }
}