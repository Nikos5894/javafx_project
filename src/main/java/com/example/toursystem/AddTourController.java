package com.example.toursystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddTourController {

    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> timeComboBox;
    @FXML
    private ComboBox<String> accessibilityComboBox;

    @FXML
    private void onSave(ActionEvent event) {
        String name = nameField.getText();
        String description = descriptionField.getText();
        String date = datePicker.getValue() != null ? datePicker.getValue().toString() : null;
        String time = timeComboBox.getValue();
        String accessibility = accessibilityComboBox.getValue();

        if (name.isEmpty() || date == null || time == null || accessibility == null) {
            showAlert("Помилка", "Будь ласка, заповніть усі поля.");
            return;
        }

        try (Connection conn = DatabaseManager.connect()) {
            String query = "INSERT INTO tours (name, description, date, time, accessibility) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setString(3, date);
                stmt.setString(4, time);
                stmt.setString(5, accessibility);
                stmt.executeUpdate();
            }
            showAlert("Успіх", "Екскурсію збережено успішно!");
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Помилка", "Не вдалося зберегти екскурсію.");
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
