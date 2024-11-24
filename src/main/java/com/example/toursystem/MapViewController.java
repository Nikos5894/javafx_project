package com.example.toursystem;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.control.TextField;
import netscape.javascript.JSObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.toursystem.DatabaseManager.connect;

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
        selectedPoints = new ArrayList<>(); // Ініціалізація списку точок
        WebEngine webEngine = mapView.getEngine();
        String mapHtml = getClass().getResource("/com/example/toursystem/html/map.html").toExternalForm();
        System.out.println("MapView завантажено: " + mapHtml);

        webEngine.load(mapHtml);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                System.out.println("HTML карта завантажена");
                webEngine.executeScript(
                        "window.javafxBridge = { addPointToRoute: function(lat, lng) { javafxCall.addPointToRoute(lat, lng); } };"
                );

                // Додаємо обробник кліків тільки один раз
                webEngine.executeScript(
                        "map.on('click', function(e) {" +
                                "   var lat = e.latlng.lat;" +
                                "   var lng = e.latlng.lng;" +
                                "   window.javafxBridge.addPointToRoute(lat, lng);" +
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
        boolean exists = false;
        for (Object obj : selectedPoints) {
            if (obj instanceof double[]) {
                double[] point = (double[]) obj;
                if (point[0] == latitude && point[1] == longitude) {
                    exists = true;
                    break;
                }
            }
        }
        if (!exists) {
            selectedPoints.add(new double[]{latitude, longitude});
            System.out.println("Точка додана: [" + latitude + ", " + longitude + "]");
        } else {
            System.out.println("Точка вже існує у списку.");
        }
    }




    @FXML
    private void onSaveRoute() {
        if (currentRouteId == -1) {
            currentRouteId = DatabaseHandler.addRoute("Новий маршрут"); // Створюємо маршрут
            System.out.println("Новий маршрут створено з ID: " + currentRouteId);
        }

        if (selectedPoints.isEmpty()) {
            System.out.println("Немає точок для збереження!");
            return;
        }

        for (Object obj : selectedPoints) {
            if (obj instanceof double[]) {
                double[] point = (double[]) obj;
                // Виклик методу для додавання точок у базу
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
        try (Connection conn = connect();
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
        List<double[]> points = DatabaseHandler.getRoutePoints(routeId);

        if (points.isEmpty()) {
            System.out.println("Маршрут порожній. Нічого не завантажено.");
            return;
        }
        System.out.println("Завантаження маршруту з ID: " + routeId);
        for (double[] point : points) {
            System.out.println("Точка: " + point[0] + ", " + point[1]);
        }

        WebEngine webEngine = mapView.getEngine();
        points.forEach(point -> {
            if (point.length == 2) { // Перевірка на коректність розміру масиву
                double latitude = point[0];
                double longitude = point[1];

                if (!Double.isNaN(latitude) && !Double.isNaN(longitude)) { // Перевірка на коректність координат
                    String script = String.format(
                            "L.marker([%f, %f]).addTo(map).bindPopup('Точка маршруту: [%f, %f]');",
                            latitude, longitude, latitude, longitude
                    );
                    webEngine.executeScript(script);
                } else {
                    System.out.println("Некоректні координати: [" + latitude + ", " + longitude + "]");
                }
            }
        });

        System.out.println("Маршрут завантажено: ID " + routeId);
    }




}
