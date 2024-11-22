package com.example.toursystem;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    private void onExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void onViewTours(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/toursystem/ViewToursView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("перегляд екскурсій");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }    }

    @FXML
    private void onAddTour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/toursystem/AddTourView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Додати екскурсію");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void onViewMap(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/toursystem/MapView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Карта екскурсій");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
