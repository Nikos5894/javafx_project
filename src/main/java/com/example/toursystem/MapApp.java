package com.example.toursystem;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MapApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Створюємо WebView для відображення карти
        WebView webView = new WebView();
        String mapHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
                    <style>
                        #map { height: 100%; width: 100%; }
                        html, body { height: 100%; margin: 0; padding: 0; }
                    </style>
                    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"
                            integrity="sha256-pMfRkUmLv7KMY5cEgj3DSqnIZMx1qINrA6ffwR8GVDk="
                            crossorigin=""></script>
                    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
                          integrity="sha256-sA+ufc5wy7+M8Rik4eGRvD9Rr+WLYG9rCi9VVgDhEms="
                          crossorigin=""/>
                </head>
                <body>
                    <div id="map"></div>
                    <script>
                        var map = L.map('map').setView([48.8566, 2.3522], 13); // Париж як приклад
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            maxZoom: 19,
                            attribution: '© OpenStreetMap'
                        }).addTo(map);
                    </script>
                </body>
                </html>
                """;

        // Завантажуємо HTML-код у WebView
        webView.getEngine().loadContent(mapHtml);

        // Додаємо WebView у сцену
        BorderPane root = new BorderPane(webView);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Мапа екскурсій");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
