package com.example.toursystem;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MapViewController {

    private  ArrayList<Object> selectedPoints;
    private int currentRouteId = -1; // Початкове значення -1 означає, що маршрут ще не створений.

    @FXML
    private WebView mapView;

    @FXML
    private TextField latitudeField;
    @FXML
    private TextField longitudeField;

    @FXML
    private void onReverseGeocode() {
        try {
            double latitude = Double.parseDouble(latitudeField.getText());
            double longitude = Double.parseDouble(longitudeField.getText());

            String address = GeoCodingHandler.reverseGeocode(latitude, longitude);

            if (address != null) {
                System.out.println("Адреса: " + address);
            } else {
                System.out.println("Не вдалося знайти адресу.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Неправильний формат координат!");
        }
    }

    @FXML
    private void initialize() {
        selectedPoints = new ArrayList<>(); // Ініціалізація списку/ Ініціалізація списку точок
        WebEngine webEngine = mapView.getEngine();
        String mapHtml = getClass().getResource("/com/example/toursystem/html/map.html").toExternalForm();
        webEngine.load(mapHtml);

        int routeId = DatabaseHandler.addRoute("Маршрут по Києву");

        webEngine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
            if (newDoc != null) {
                webEngine.executeScript(
                        "map.on('click', function(e) {" +
                                "   var lat = e.latlng.lat;" +
                                "   var lng = e.latlng.lng;" +
                                "   javafxBridge.addPointToRoute(lat, lng);" +
                                "   L.marker([lat, lng]).addTo(map);" +
                                "});"
                );
            }
        });
        webEngine.setJavaScriptEnabled(true);
    }

    @FXML
    private TextField routeIdField;


    @FXML
    private void onLoadRoute() {
        try {
            int routeId = Integer.parseInt(routeIdField.getText()); // Отримуємо ID маршруту з текстового поля
            loadRouteOnMap(routeId); // Завантажуємо маршрут на карту
        } catch (NumberFormatException e) {
            System.out.println("Помилка: введіть коректний ID маршруту!");
        }
    }

    public void addPointToRoute(double latitude, double longitude) {
        selectedPoints.add(new double[]{latitude, longitude}); // Додаємо точку до списку
        System.out.println("Точка додана: [" + latitude + ", " + longitude + "]");
    }


    @FXML
    private void onSaveRoute() {
    if (currentRouteId == -1) {
        currentRouteId = DatabaseHandler.addRoute("Новий маршрут"); // Створюємо маршрут
    }
    if (selectedPoints.isEmpty()) {
            System.out.println("Немає точок для збереження!");
            return;
        }
        for (Object obj : selectedPoints) {
            if (obj instanceof double[]) {
                double[] point = (double[]) obj;
                DatabaseHandler.addRoutePoint(currentRouteId, point[0], point[1]);
            }
        }

        System.out.println("Маршрут збережено: ID " + currentRouteId);
    }

    @FXML
    private void onSearchAddress() {
        String address = "вулиця Хрещатик, Київ"; // Приклад адреси
        double[] coordinates = GeoCodingHandler.geocodeAddress(address);

        if (coordinates != null) {
            double lat = coordinates[0];
            double lng = coordinates[1];
            mapView.getEngine().executeScript(String.format(
                    "var marker = L.marker([%f, %f]).addTo(map).bindPopup('Знайдено: %s').openPopup();", lat, lng, address));
            mapView.getEngine().executeScript(String.format("map.setView([%f, %f], 13);", lat, lng));
        } else {
            System.out.println("Адресу не знайдено!");
        }
    }

    @FXML
    private void addMarker(double latitude, double longitude, String description) {
        WebEngine webEngine = mapView.getEngine();
        String script = String.format(
                "var marker = L.marker([%f, %f]).addTo(map).bindPopup('%s').openPopup();",
                latitude, longitude, description
        );
        webEngine.executeScript(script);
    }
    public void loadMarkersFromDatabase() {
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT latitude, longitude, name FROM tours")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                double latitude = rs.getDouble("latitude");
                double longitude = rs.getDouble("longitude");
                String name = rs.getString("name");
                addMarker(latitude, longitude, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRouteOnMap(int routeId) {
        List<double[]> points = DatabaseHandler.getRoutePoints(routeId); // Отримуємо точки з бази даних

        WebEngine webEngine = mapView.getEngine();
        for (double[] point : points) {
            double latitude = point[0];
            double longitude = point[1];

            // Додаємо маркер для кожної точки на карті через JavaScript
            webEngine.executeScript(
                    String.format("L.marker([%f, %f]).addTo(map);", latitude, longitude)
            );
        }
    }

}
