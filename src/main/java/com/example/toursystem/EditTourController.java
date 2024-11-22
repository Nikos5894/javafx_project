package com.example.toursystem;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditTourController {

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

    private Tour currentTour;

    public void setTour(Tour tour) {
        this.currentTour = tour;
        nameField.setText(tour.getName());
        descriptionField.setText(tour.getDescription());
        datePicker.setValue(java.time.LocalDate.parse(tour.getDate()));
        timeComboBox.setValue(tour.getTime());
        accessibilityComboBox.setValue(tour.getAccessibility());
    }

    @FXML
    private void onSave() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        String date = datePicker.getValue() != null ? datePicker.getValue().toString() : null;
        String time = timeComboBox.getValue();
        String accessibility = accessibilityComboBox.getValue();

        if (name.isEmpty() || date == null || time == null || accessibility == null) {
            showAlert("Помилка", "Будь ласка, заповніть усі поля.");
            return;
        }

        String query = "UPDATE tours SET name = ?, description = ?, date = ?, time = ?, accessibility = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, date);
            stmt.setString(4, time);
            stmt.setString(5, accessibility);
            stmt.setInt(6, currentTour.getId());
            stmt.executeUpdate();
            showAlert("Успіх", "Екскурсію оновлено успішно.");
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Помилка", "Не вдалося оновити екскурсію.");
        }
    }

    @FXML
    private void onCancel() {
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

    public static void showEditDialog(Tour tour) {
        try {
            FXMLLoader loader = new FXMLLoader(EditTourController.class.getResource("/com/example/toursystem/EditTourView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            EditTourController controller = loader.getController();
            controller.setTour(tour);
            stage.setTitle("Редагувати екскурсію");
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
