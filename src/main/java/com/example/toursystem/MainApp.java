package com.example.toursystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseHandler.initialize();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/toursystem/MainView.fxml"));
        BorderPane root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Система управління екскурсіями");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}