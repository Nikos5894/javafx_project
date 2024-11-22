package com.example.toursystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ViewToursController {

    @FXML
    private TableView<Tour> toursTable;
    @FXML
    private TableColumn<Tour, Integer> idColumn;
    @FXML
    private TableColumn<Tour, String> nameColumn;
    @FXML
    private TableColumn<Tour, String> descriptionColumn;
    @FXML
    private TableColumn<Tour, String> dateColumn;
    @FXML
    private TableColumn<Tour, String> timeColumn;
    @FXML
    private TableColumn<Tour, String> accessibilityColumn;

    private final ObservableList<Tour> toursList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        accessibilityColumn.setCellValueFactory(new PropertyValueFactory<>("accessibility"));

        loadTours();
    }

    private void loadTours() {
        toursList.clear();
        String query = "SELECT * FROM tours";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                toursList.add(new Tour(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("accessibility")
                ));
            }
            toursTable.setItems(toursList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEdit(ActionEvent event) {
        Tour selectedTour = toursTable.getSelectionModel().getSelectedItem();
        if (selectedTour == null) {
            showAlert("Помилка", "Оберіть екскурсію для редагування.");
            return;
        }
        EditTourController.showEditDialog(selectedTour);
        loadTours(); // Оновлення після редагування
    }

    @FXML
    private void onDelete(ActionEvent event) {
        Tour selectedTour = toursTable.getSelectionModel().getSelectedItem();
        if (selectedTour == null) {
            showAlert("Помилка", "Оберіть екскурсію для видалення.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Підтвердження");
        alert.setHeaderText(null);
        alert.setContentText("Ви впевнені, що хочете видалити цю екскурсію?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String query = "DELETE FROM tours WHERE id = ?";
            try (Connection conn = DatabaseManager.connect();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, selectedTour.getId());
                stmt.executeUpdate();
                showAlert("Успіх", "Екскурсію видалено.");
                loadTours(); // Оновлення після видалення
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Помилка", "Не вдалося видалити екскурсію.");
            }
        }
    }

    @FXML
    private void onClose(ActionEvent event) {
        Stage stage = (Stage) toursTable.getScene().getWindow();
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
