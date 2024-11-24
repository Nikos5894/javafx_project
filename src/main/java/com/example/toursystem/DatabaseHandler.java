package com.example.toursystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.toursystem.DatabaseManager.connect;

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
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, routeId);
            stmt.setDouble(2, latitude);
            stmt.setDouble(3, longitude);
            stmt.executeUpdate();
            System.out.println("Точка маршруту збережена: Route ID " + routeId + ", [" + latitude + ", " + longitude + "]");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Метод для отримання точок маршруту за ID маршруту
    public static List<double[]> getRoutePoints(int routeId) {
        List<double[]> points = new ArrayList<>();
        String sql = "SELECT latitude, longitude FROM route_points WHERE route_id = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, routeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                points.add(new double[]{rs.getDouble("latitude"), rs.getDouble("longitude")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Отримано точки маршруту з БД: " + points.size());
        return points;
    }

}