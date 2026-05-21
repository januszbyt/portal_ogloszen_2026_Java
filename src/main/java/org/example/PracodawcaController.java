package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.util.Optional;

public class PracodawcaController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private TextField locationField;
    @FXML private TextField salaryMinField;
    @FXML private TextField salaryMaxField;
    @FXML private TextArea descriptionArea;

    @FXML private TableView<JobOfferMock> offersTable;
    @FXML private TableColumn<JobOfferMock, String> colTitle;
    @FXML private TableColumn<JobOfferMock, String> colCategory;
    @FXML private TableColumn<JobOfferMock, String> colLocation;
    @FXML private TableColumn<JobOfferMock, String> colSalary;
    @FXML private TableColumn<JobOfferMock, String> colStatus;

    @FXML private ListView<String> candidatesListView;

    private static final ObservableList<JobOfferMock> mockOffersList = FXCollections.observableArrayList();
    private JobOfferMock selectedOfferForEdit = null;

    @FXML
    public void initialize() {
        categoryCombo.setItems(FXCollections.observableArrayList("IT / Software", "Budownictwo", "Finanse", "Sprzedaż"));

        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salaryRange"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        if (mockOffersList.isEmpty()) {
            mockOffersList.add(new JobOfferMock("Java Developer", "IT / Software", "Warszawa", "8000 - 15000 PLN", "Wymagana Java 21"));
        }
        offersTable.setItems(mockOffersList);

   
        offersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                candidatesListView.setItems(newSelection.getApplications());
            } else {
                candidatesListView.setItems(null);
            }
        });
    }

    // 2.1: Dodawanie nowej oferty 
    @FXML
    private void handleAddOffer(ActionEvent event) {
        String salaryRange = salaryMinField.getText() + " - " + salaryMaxField.getText() + " PLN";
        JobOfferMock newOffer = new JobOfferMock(
            titleField.getText(), categoryCombo.getValue(), locationField.getText(), salaryRange, descriptionArea.getText()
        );
        mockOffersList.add(newOffer);
        clearForm();
    }

    // 2.3: Kliknięcie "Edytuj" - przepisuje dane do formularza
    @FXML
    private void handleEditClick(ActionEvent event) {
        selectedOfferForEdit = offersTable.getSelectionModel().getSelectedItem();
        if (selectedOfferForEdit != null) {
            titleField.setText(selectedOfferForEdit.getTitle());
            categoryCombo.setValue(selectedOfferForEdit.getCategory());
            locationField.setText(selectedOfferForEdit.getLocation());
            descriptionArea.setText(selectedOfferForEdit.getDescription());
        }
    }

    //2.3: Zapisanie zmian po edycji
    @FXML
    private void handleSaveEdit(ActionEvent event) {
        if (selectedOfferForEdit != null) {
            selectedOfferForEdit.setTitle(titleField.getText());
            selectedOfferForEdit.setCategory(categoryCombo.getValue());
            selectedOfferForEdit.setLocation(locationField.getText());
            selectedOfferForEdit.setSalaryRange(salaryMinField.getText() + " - " + salaryMaxField.getText() + " PLN");
            selectedOfferForEdit.setDescription(descriptionArea.getText());
            
            offersTable.refresh();
            clearForm();
            selectedOfferForEdit = null;
        }
    }

    //2.4: Zakończenie rekrutacji (Zmiana statusu)
    @FXML
    private void handleCloseClick(ActionEvent event) {
        JobOfferMock selected = offersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Nieaktywna");
            offersTable.refresh();
        }
    }

    //2.4: Usuwanie oferty całkowicie
    @FXML
    private void handleDeleteOffer(ActionEvent event) {
        JobOfferMock selected = offersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Czy na pewno chcesz usunąć tę ofertę?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                mockOffersList.remove(selected);
            }
        }
    }

    //2.6: Zmiana statusu aplikacji kandydata na "W toku"
    @FXML
    private void handleChangeStatusAccept(ActionEvent event) {
        int selectedIndex = candidatesListView.getSelectionModel().getSelectedIndex();
        JobOfferMock selectedOffer = offersTable.getSelectionModel().getSelectedItem();
        if (selectedIndex >= 0 && selectedOffer != null) {
            String currentKandydat = selectedOffer.getApplications().get(selectedIndex);
            String czystyKandydat = currentKandydat.split(" - ")[0];
            selectedOffer.getApplications().set(selectedIndex, czystyKandydat + " - CV.pdf (W toku)");
        }
    }

    //2.6: Zmiana statusu aplikacji kandydata na "Odrzucony"
    @FXML
    private void handleChangeStatusReject(ActionEvent event) {
        int selectedIndex = candidatesListView.getSelectionModel().getSelectedIndex();
        JobOfferMock selectedOffer = offersTable.getSelectionModel().getSelectedItem();
        if (selectedIndex >= 0 && selectedOffer != null) {
            String currentKandydat = selectedOffer.getApplications().get(selectedIndex);
            String czystyKandydat = currentKandydat.split(" - ")[0];
            selectedOffer.getApplications().set(selectedIndex, czystyKandydat + " - CV.pdf (Odrzucony)");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        App.setRoot("login");
    }

    private void clearForm() {
        titleField.clear();
        locationField.clear();
        salaryMinField.clear();
        salaryMaxField.clear();
        descriptionArea.clear();
    }
}