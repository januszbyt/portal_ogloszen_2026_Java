package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class KandydatController {

    @FXML private TextField txtSearchOffer;

    // Kontrolki filtrów utworzone w Javie (nie potrzebują już @FXML, trafiają do okienka dialogowego)
    private ComboBox<String> comboCategoryFilter = new ComboBox<>();
    private TextField txtLocationFilter = new TextField();
    private TextField txtMinSalaryFilter = new TextField();

    @FXML private TableView<JobOffer> offersTable;
    @FXML private TableColumn<JobOffer, String> colTitle;
    @FXML private TableColumn<JobOffer, String> colCategory;
    @FXML private TableColumn<JobOffer, String> colLocation;
    @FXML private TableColumn<JobOffer, String> colSalary;

    @FXML private TableView<JobOffer> historyTable;
    @FXML private TableColumn<JobOffer, String> colHistTitle;
    @FXML private TableColumn<JobOffer, String> colHistCategory;
    @FXML private TableColumn<JobOffer, String> colHistLocation;
    @FXML private TableColumn<JobOffer, String> colHistStatus;

    private static final ObservableList<JobOffer> allOffers = FXCollections.observableArrayList();
    private final ObservableList<JobOffer> myApplications = FXCollections.observableArrayList();
    private FilteredList<JobOffer> filteredOffers;
    
    // UWAGA: Podczas testów przypisuję ID na sztywno do 1.
    // Upewnij się, że w tabeli Candidates masz użytkownika z CandidateID = 1.
    private final int loggedInCandidateId = 1; 

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salaryRange"));

        colHistTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colHistCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colHistLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colHistStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Możesz w przyszłości pobierać kategorie z DB, na razie filtry GUI są statyczne
        comboCategoryFilter.setItems(FXCollections.observableArrayList("Wszystkie", "IT / Software", "Budownictwo", "Finanse", "Sprzedaż"));
        comboCategoryFilter.getSelectionModel().selectFirst();
        txtLocationFilter.setPromptText("Lokalizacja (np. Warszawa)");
        txtMinSalaryFilter.setPromptText("Min. wynagrodzenie");

        // Bezpośrednie ładowanie z bazy
        loadOffersFromDatabase();
        loadHistoryFromDatabase();

        filteredOffers = new FilteredList<>(allOffers, b -> true);
        SortedList<JobOffer> sortedOffers = new SortedList<>(filteredOffers);
        sortedOffers.comparatorProperty().bind(offersTable.comparatorProperty());
        offersTable.setItems(sortedOffers);
        
        historyTable.setItems(myApplications);
        txtSearchOffer.textProperty().addListener((observable, oldValue, newValue) -> filterData());
    }

    private void loadOffersFromDatabase() {
        allOffers.clear();
        String query = "SELECT j.OfferID as id, j.Title, c.CategoryName, j.Location, " +
                       "CONCAT(j.SalaryMIN, ' - ', j.SalaryMAX, ' PLN') as salary_range, " +
                       "j.Description, os.StatusName " +
                       "FROM JobOffers j " +
                       "JOIN Categories c ON j.CategoryID = c.CategoryID " +
                       "JOIN OfferStatuses os ON j.OfferStatusID = os.OfferStatusID " +
                       "WHERE os.StatusName = 'Aktywna'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                JobOffer offer = new JobOffer(
                    rs.getInt("id"),
                    rs.getString("Title"),
                    rs.getString("CategoryName"),
                    rs.getString("Location"),
                    rs.getString("salary_range"),
                    rs.getString("Description")
                );
                offer.setStatus(rs.getString("StatusName"));
                allOffers.add(offer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd Bazy", "Nie udało się pobrać ofert z bazy danych.");
        }
    }

    private void loadHistoryFromDatabase() {
        myApplications.clear();
        String query = "SELECT j.OfferID as id, j.Title, c.CategoryName, j.Location, " +
                       "CONCAT(j.SalaryMIN, ' - ', j.SalaryMAX, ' PLN') as salary_range, " +
                       "j.Description, ast.StatusName as app_status " +
                       "FROM Applications a " +
                       "JOIN JobOffers j ON a.OfferID = j.OfferID " +
                       "JOIN Categories c ON j.CategoryID = c.CategoryID " +
                       "JOIN ApplicationStatuses ast ON a.StatusID = ast.StatusID " +
                       "WHERE a.CandidateID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             
            pstmt.setInt(1, loggedInCandidateId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                JobOffer historyOffer = new JobOffer(
                    rs.getInt("id"),
                    rs.getString("Title"),
                    rs.getString("CategoryName"),
                    rs.getString("Location"),
                    rs.getString("salary_range"),
                    rs.getString("Description")
                );
                historyOffer.setStatus(rs.getString("app_status"));
                myApplications.add(historyOffer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applyForJob(JobOffer offer) {
        String insertQuery = "INSERT INTO Applications (OfferID, CandidateID, StatusID, AppliedAt) " +
                             "VALUES (?, ?, (SELECT StatusID FROM ApplicationStatuses WHERE StatusName = 'Oczekująca' LIMIT 1), NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
             
            pstmt.setInt(1, offer.getId()); 
            pstmt.setInt(2, loggedInCandidateId); 
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                loadHistoryFromDatabase(); // Odśwież widok historii
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Aplikacja została wysłana i zapisana w bazie!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // MySQL ekrror 1062 to naruszenie zasady UNIQUE (CandidateID + OfferID w tabeli Applications)
            if (e.getErrorCode() == 1062) { 
                 showAlert(Alert.AlertType.WARNING, "Uwaga", "Istnieje już Twoja aplikacja na to stanowisko w bazie.");
            } else {
                 showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zapisać aplikacji w bazie danych.");
            }
        }
    }

    @FXML
    private void handleViewDetails() {
        JobOffer selectedOffer = offersTable.getSelectionModel().getSelectedItem();
        
        if (selectedOffer == null) {
            showAlert(Alert.AlertType.WARNING, "Brak wyboru", "Zaznacz ofertę z listy.");
            return;
        }

        Alert detailsAlert = new Alert(Alert.AlertType.INFORMATION);
        detailsAlert.setTitle("Szczegóły ogłoszenia");
        detailsAlert.setHeaderText(selectedOffer.getTitle() + " (" + selectedOffer.getLocation() + ")");
        
        TextArea textArea = new TextArea(
            "Kategoria: " + selectedOffer.getCategory() + "\n" +
            "Wynagrodzenie: " + selectedOffer.getSalaryRange() + "\n\n" +
            "Opis stanowiska:\n" + selectedOffer.getDescription()
        );
        textArea.setEditable(false);
        textArea.setWrapText(true);
        detailsAlert.getDialogPane().setContent(textArea);

        ButtonType applyButtonType = new ButtonType("Aplikuj", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Zamknij", ButtonBar.ButtonData.CANCEL_CLOSE);
        detailsAlert.getButtonTypes().setAll(applyButtonType, cancelButtonType);

        Optional<ButtonType> result = detailsAlert.showAndWait();
        if (result.isPresent() && result.get() == applyButtonType) {
            applyForJob(selectedOffer);
        }
    }

    @FXML
    private void obslugaPrzyciskuFiltry() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Filtrowanie ogłoszeń");
        dialog.setHeaderText("Ustaw filtry wyszukiwania");

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(
            new Label("Wybierz branżę:"), comboCategoryFilter,
            new Label("Lokalizacja:"), txtLocationFilter,
            new Label("Minimalne wynagrodzenie:"), txtMinSalaryFilter
        );
        dialog.getDialogPane().setContent(vbox);

        ButtonType btnZastosuj = new ButtonType("Zastosuj filtry", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnWyczysc = new ButtonType("Wyczyść filtry", ButtonBar.ButtonData.LEFT);
        ButtonType btnAnuluj = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(btnZastosuj, btnWyczysc, btnAnuluj);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == btnZastosuj) {
                filterData();
            } else if (result.get() == btnWyczysc) {
                comboCategoryFilter.getSelectionModel().selectFirst();
                txtLocationFilter.clear();
                txtMinSalaryFilter.clear();
                filterData();
            }
        }
    }

    @FXML
    private void handleSearchButton() {
        filterData();
    }

    private void filterData() {
        filteredOffers.setPredicate(offer -> {
            String searchKeyword = txtSearchOffer.getText() == null ? "" : txtSearchOffer.getText().toLowerCase();
            if (!searchKeyword.isEmpty() && !offer.getTitle().toLowerCase().contains(searchKeyword)) {
                return false;
            }
            String catFilter = comboCategoryFilter.getValue();
            if (catFilter != null && !catFilter.equals("Wszystkie") && !offer.getCategory().equals(catFilter)) {
                return false;
            }
            String locFilter = txtLocationFilter.getText() == null ? "" : txtLocationFilter.getText().toLowerCase();
            if (!locFilter.isEmpty() && !offer.getLocation().toLowerCase().contains(locFilter)) {
                return false;
            }
            return true; 
        });
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        App.setRoot("login");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}