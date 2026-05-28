package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PracodawcaController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private TextField locationField;
    @FXML private TextField salaryMinField;
    @FXML private TextField salaryMaxField;
    @FXML private TextArea descriptionArea;

    @FXML private TableView<JobOffer> offersTable;
    @FXML private TableColumn<JobOffer, String> colTitle;
    @FXML private TableColumn<JobOffer, String> colCategory;
    @FXML private TableColumn<JobOffer, String> colLocation;
    @FXML private TableColumn<JobOffer, String> colSalary;
    @FXML private TableColumn<JobOffer, String> colStatus;

    @FXML private ListView<String> candidatesListView;

    private final ObservableList<JobOffer> offersList = FXCollections.observableArrayList();
    private JobOffer selectedOfferForEdit = null;

    private final Map<String, Integer> categoryMap = new HashMap<>();
    private final Map<String, Integer> offerStatusMap = new HashMap<>();
    private final Map<String, Integer> appStatusMap = new HashMap<>();
    private final ObservableList<Integer> currentApplicationsIdList = FXCollections.observableArrayList();

    private int loggedInEmployerId; 

    @FXML
    public void initialize() {
        if (UserSession.getInstance() == null) {
            showAlert(Alert.AlertType.ERROR, "Błąd sesji", "Brak aktywnej sesji. Zaloguj się ponownie.");
            return;
        }
        
        loggedInEmployerId = UserSession.getInstance().getUserId();

        loadDictionariesFromDB();
        categoryCombo.setItems(FXCollections.observableArrayList(categoryMap.keySet()));

        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salaryRange")); // Model sam sklei MIN i MAX do tego stringa
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        offersTable.setItems(offersList);
        loadEmployerOffersFromDB();

        offersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadCandidatesForOfferFromDB(newSelection.getId());
            } else {
                candidatesListView.setItems(null);
                currentApplicationsIdList.clear();
            }
        });
    }

    private void loadDictionariesFromDB() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT CategoryID, CategoryName FROM Categories");
            while (rs.next()) categoryMap.put(rs.getString("CategoryName"), rs.getInt("CategoryID"));

            rs = stmt.executeQuery("SELECT OfferStatusID, StatusName FROM OfferStatuses");
            while (rs.next()) offerStatusMap.put(rs.getString("StatusName"), rs.getInt("OfferStatusID"));

            rs = stmt.executeQuery("SELECT StatusID, StatusName FROM ApplicationStatuses");
            while (rs.next()) appStatusMap.put(rs.getString("StatusName"), rs.getInt("StatusID"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadEmployerOffersFromDB() {
        offersList.clear();
        String sql = "SELECT o.OfferID, o.Title, c.CategoryName, o.Location, o.SalaryMIN, o.SalaryMAX, o.Description, s.StatusName " +
                     "FROM JobOffers o " +
                     "JOIN Categories c ON o.CategoryID = c.CategoryID " +
                     "JOIN OfferStatuses s ON o.OfferStatusID = s.OfferStatusID " +
                     "WHERE o.EmployerID = ?";
                     
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, loggedInEmployerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
               
                JobOffer offer = new JobOffer(
                    rs.getInt("OfferID"),
                    rs.getString("Title"),
                    rs.getString("CategoryName"),
                    rs.getString("Location"),
                    rs.getBigDecimal("SalaryMIN"),
                    rs.getBigDecimal("SalaryMAX"),
                    rs.getString("Description")
                );
                offer.setStatus(rs.getString("StatusName"));
                offersList.add(offer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie można pobrać ogłoszeń.");
        }
    }

    private void loadCandidatesForOfferFromDB(int offerId) {
        ObservableList<String> displayRows = FXCollections.observableArrayList();
        currentApplicationsIdList.clear();

        String sql = "SELECT a.ApplicationID, c.FirstName, c.LastName, c.CVFilePath, s.StatusName FROM Applications a " +
                     "JOIN Candidates c ON a.CandidateID = c.CandidateID " +
                     "JOIN ApplicationStatuses s ON a.StatusID = s.StatusID " +
                     "WHERE a.OfferID = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, offerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String cvFile = rs.getString("CVFilePath") != null ? rs.getString("CVFilePath") : "CV.pdf";
                String candidateInfo = rs.getString("FirstName") + " " + rs.getString("LastName") + " - " + cvFile + " (" + rs.getString("StatusName") + ")";
                displayRows.add(candidateInfo);
                currentApplicationsIdList.add(rs.getInt("ApplicationID"));
            }
            candidatesListView.setItems(displayRows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddOffer(ActionEvent event) {
        String title = titleField.getText() != null ? titleField.getText().trim() : "";
        String location = locationField.getText() != null ? locationField.getText().trim() : "";
        String desc = descriptionArea.getText() != null ? descriptionArea.getText().trim() : "";
        String sMin = salaryMinField.getText() != null ? salaryMinField.getText().trim() : "";
        String sMax = salaryMaxField.getText() != null ? salaryMaxField.getText().trim() : "";

        // USUNĘLIŚMY sMin.isEmpty() i sMax.isEmpty() z walidacji – teraz mogą być puste!
        if (title.isEmpty() || location.isEmpty() || desc.isEmpty() || categoryCombo.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Walidacja", "Wypełnij tytuł, lokalizację, opis i kategorię!");
            return;
        }

        String sql = "INSERT INTO JobOffers (EmployerID, Title, CategoryID, Description, SalaryMIN, SalaryMAX, Location, OfferStatusID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, loggedInEmployerId);
            pstmt.setString(2, title);
            pstmt.setInt(3, categoryMap.getOrDefault(categoryCombo.getValue(), 1));
            pstmt.setString(4, desc);
            
            // JEŚLI pole min jest puste, wyślij do bazy MySQL wartość NULL
            if (sMin.isEmpty()) {
                pstmt.setNull(5, java.sql.Types.DECIMAL);
            } else {
                pstmt.setBigDecimal(5, new BigDecimal(sMin));
            }

            // JEŚLI pole max jest puste, wyślij do bazy MySQL wartość NULL
            if (sMax.isEmpty()) {
                pstmt.setNull(6, java.sql.Types.DECIMAL);
            } else {
                pstmt.setBigDecimal(6, new BigDecimal(sMax));
            }

            pstmt.setString(7, location);
            pstmt.setInt(8, offerStatusMap.getOrDefault("Aktywna", 1));

            pstmt.executeUpdate();
            loadEmployerOffersFromDB();
            clearForm();
        } catch (SQLException | NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zapisać oferty. Sprawdź poprawność wpisanych kwot.");
        }
    }

    @FXML
    private void handleEditClick(ActionEvent event) {
        selectedOfferForEdit = offersTable.getSelectionModel().getSelectedItem();
        if (selectedOfferForEdit == null) {
            showAlert(Alert.AlertType.WARNING, "Wybór", "Zaznacz ofertę z tabeli.");
            return;
        }
        
        titleField.setText(selectedOfferForEdit.getTitle());
        categoryCombo.setValue(selectedOfferForEdit.getCategory());
        locationField.setText(selectedOfferForEdit.getLocation());
        descriptionArea.setText(selectedOfferForEdit.getDescription());
        salaryMinField.setText(selectedOfferForEdit.getSalaryMin().toString());
        salaryMaxField.setText(selectedOfferForEdit.getSalaryMax().toString());
    }

    @FXML
    private void handleSaveEdit(ActionEvent event) {
        if (selectedOfferForEdit == null) {
            showAlert(Alert.AlertType.WARNING, "Błąd", "Nie wybrano ogłoszenia do edycji.");
            return;
        }

        String sql = "UPDATE JobOffers SET Title = ?, CategoryID = ?, Description = ?, SalaryMIN = ?, SalaryMAX = ?, Location = ? WHERE OfferID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, titleField.getText().trim());
            pstmt.setInt(2, categoryMap.getOrDefault(categoryCombo.getValue(), 1));
            pstmt.setString(3, descriptionArea.getText().trim());
            pstmt.setBigDecimal(4, new BigDecimal(salaryMinField.getText().trim()));
            pstmt.setBigDecimal(5, new BigDecimal(salaryMaxField.getText().trim()));
            pstmt.setString(6, locationField.getText().trim());
            pstmt.setInt(7, selectedOfferForEdit.getId());

            pstmt.executeUpdate();
            loadEmployerOffersFromDB();
            clearForm();
            selectedOfferForEdit = null;
            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Ogłoszenie zostało pomyślnie zaktualizowane.");
        } catch (SQLException | NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Błąd zapisu danych.");
        }
    }

    @FXML
    private void handleCloseClick(ActionEvent event) {
        JobOffer selected = offersTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String sql = "UPDATE JobOffers SET OfferStatusID = ? WHERE OfferID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, offerStatusMap.getOrDefault("Nieaktywna", 2));
            pstmt.setInt(2, selected.getId());
            pstmt.executeUpdate();
            loadEmployerOffersFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteOffer(ActionEvent event) {
        JobOffer selected = offersTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Czy na pewno chcesz bezpowrotnie usunąć to ogłoszenie?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            String sql = "DELETE FROM JobOffers WHERE OfferID = ?";
            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, selected.getId());
                pstmt.executeUpdate();
                offersList.remove(selected);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie możesz usunąć oferty rekrutacyjnej.");
            }
        }
    }

    @FXML private void handleChangeStatusAccept(ActionEvent event) { updateApplicationStatus("W toku"); }
    @FXML private void handleChangeStatusReject(ActionEvent event) { updateApplicationStatus("Odrzucony"); }

    private void updateApplicationStatus(String newStatusName) {
        int index = candidatesListView.getSelectionModel().getSelectedIndex();
        JobOffer currentOffer = offersTable.getSelectionModel().getSelectedItem();
        
        if (index < 0 || currentOffer == null) {
            showAlert(Alert.AlertType.WARNING, "Wybór", "Zaznacz aplikację kandydata.");
            return;
        }

        int appId = currentApplicationsIdList.get(index);
        String sql = "UPDATE Applications SET StatusID = ? WHERE ApplicationID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, appStatusMap.getOrDefault(newStatusName, 1));
            pstmt.setInt(2, appId);
            pstmt.executeUpdate();
            
            loadCandidatesForOfferFromDB(currentOffer.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        UserSession.clear();
        App.setRoot("login");
    }

    private void clearForm() {
        titleField.clear();
        locationField.clear();
        salaryMinField.clear();
        salaryMaxField.clear();
        descriptionArea.clear();
        categoryCombo.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}