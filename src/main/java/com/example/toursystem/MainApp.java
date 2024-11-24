package com.example.toursystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("/com/example/toursystem/MainView.fxml");
        System.out.println("FXML Location: " + fxmlLocation); // Для діагностики
        if (fxmlLocation == null) {
            throw new IllegalStateException("FXML file not found! Ensure it's in the correct resources folder.");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();
        primaryStage.setTitle("Tour Management System");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}