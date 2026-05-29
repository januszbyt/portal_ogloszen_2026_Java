package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class WyszukiwarkaController {

    @FXML private HBox panelFiltrow;
    @FXML private Label lblWelcome;

    // Kontrolki filtrowania i wyszukiwania
    @FXML private TextField txtSearchUser;
    @FXML private Button btnSearchUser;
    @FXML private TextField txtTitleFilter;
    @FXML private TextField txtSearchUser1;
    @FXML private Button btnSearchUserHistory;
    @FXML private ComboBox<String> comboCategoryFilter;
    @FXML private TextField txtLocationFilter;
    @FXML private TextField txtMinSalaryFilter;

    // Tabela Dostępnych Ofert
    @FXML private TableView<JobOffer> tableUsers;
    @FXML private TableColumn<JobOffer, String> colUserId; // Tytuł
    @FXML private TableColumn<JobOffer, String> colUserRole; // Kategoria
    @FXML private TableColumn<JobOffer, String> colUserEmail; // Lokalizacja
    @FXML private TableColumn<JobOffer, String> colUserDate; // Wynagrodzenie
    @FXML private TableColumn<JobOffer, String> colUserStatus; // Status

    // Tabela Historii Ogłoszeń
    @FXML private TableView<JobOffer> tableUsers1;
    @FXML private TableColumn<JobOffer, String> colUserId1; // Tytuł
    @FXML private TableColumn<JobOffer, String> colUserRole1; // Kategoria
    @FXML private TableColumn<JobOffer, String> colUserEmail1; // Lokalizacja
    @FXML private TableColumn<JobOffer, String> colUserDate1; // Wynagrodzenie
    @FXML private TableColumn<JobOffer, String> colUserStatus1; // Status

    @FXML private Button btnViewOffer1;
    @FXML private Button btnViewOffer11;

    private static final ObservableList<JobOffer> allOffers = FXCollections.observableArrayList();
    private final ObservableList<JobOffer> myApplications = FXCollections.observableArrayList();
    private FilteredList<JobOffer> filteredOffers;
    private FilteredList<JobOffer> filteredHistory;

    // Ta metoda odpala się AUTOMATYCZNIE przy starcie aplikacji
    @FXML
    public void initialize() {
        // Powiąż właściwość 'managed' z 'visible' dla panelu filtrów
        panelFiltrow.managedProperty().bind(panelFiltrow.visibleProperty());

        // Schowaj panel filtrów na starcie
        panelFiltrow.setVisible(false);
        panelFiltrow.setPrefHeight(0);
        panelFiltrow.setMinHeight(0);

        // Ustawienie przywitania zalogowanego użytkownika z sesji
        UserSession session = UserSession.getInstance();
        if (session != null && session.getFirstName() != null) {
            lblWelcome.setText("Witaj, " + session.getFirstName() + "!");
        }

        // Bindowanie kolumn tabeli Dostępnych Ofert
        colUserId.setCellValueFactory(new PropertyValueFactory<>("title"));
        colUserRole.setCellValueFactory(new PropertyValueFactory<>("category"));
        colUserEmail.setCellValueFactory(new PropertyValueFactory<>("location"));
        colUserDate.setCellValueFactory(new PropertyValueFactory<>("salaryRange"));
        colUserStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Bindowanie kolumn tabeli Historii
        colUserId1.setCellValueFactory(new PropertyValueFactory<>("title"));
        colUserRole1.setCellValueFactory(new PropertyValueFactory<>("category"));
        colUserEmail1.setCellValueFactory(new PropertyValueFactory<>("location"));
        colUserDate1.setCellValueFactory(new PropertyValueFactory<>("salaryRange"));
        colUserStatus1.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Ustawienie wartości ComboBox kategorii
        comboCategoryFilter.setItems(FXCollections.observableArrayList("Wszystkie", "IT / Software", "Budownictwo", "Finanse", "Sprzedaż"));
        comboCategoryFilter.getSelectionModel().selectFirst();

        // Pobranie danych z bazy
        loadOffersFromDatabase();
        loadHistoryFromDatabase();

        // Konfiguracja filtrowania i sortowania dla tabeli głównej
        filteredOffers = new FilteredList<>(allOffers, b -> true);
        SortedList<JobOffer> sortedOffers = new SortedList<>(filteredOffers);
        sortedOffers.comparatorProperty().bind(tableUsers.comparatorProperty());
        tableUsers.setItems(sortedOffers);

        // Konfiguracja filtrowania i sortowania dla tabeli historii
        filteredHistory = new FilteredList<>(myApplications, b -> true);
        SortedList<JobOffer> sortedHistory = new SortedList<>(filteredHistory);
        sortedHistory.comparatorProperty().bind(tableUsers1.comparatorProperty());
        tableUsers1.setItems(sortedHistory);

        // Słuchacz zmian w polu szybkiego wyszukiwania historii
        txtSearchUser1.textProperty().addListener((observable, oldValue, newValue) -> filterHistoryData());
        if (btnSearchUserHistory != null) {
            btnSearchUserHistory.setOnAction(event -> filterHistoryData());
        }

        // Słuchacz zmian w polu szybkiego wyszukiwania oraz filtrach
        txtSearchUser.textProperty().addListener((observable, oldValue, newValue) -> filterData());
        txtTitleFilter.textProperty().addListener((observable, oldValue, newValue) -> filterData());
        txtLocationFilter.textProperty().addListener((observable, oldValue, newValue) -> filterData());
        txtMinSalaryFilter.textProperty().addListener((observable, oldValue, newValue) -> filterData());
        comboCategoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> filterData());

        // Podpięcie przycisku Szukaj
        btnSearchUser.setOnAction(event -> filterData());

        // Obsługa kliknięcia przycisku aplikowania (Podgląd ogłoszenia) i wygaszania (disable) przy braku selekcji
        btnViewOffer1.disableProperty().bind(tableUsers.getSelectionModel().selectedItemProperty().isNull());
        btnViewOffer11.disableProperty().bind(tableUsers1.getSelectionModel().selectedItemProperty().isNull());

        btnViewOffer1.setOnAction(event -> handleViewDetails());
        btnViewOffer11.setOnAction(event -> handleViewDetailsHistory());
    }

    private void loadOffersFromDatabase() {
        allOffers.clear();
        String query = "SELECT j.OfferID as id, j.Title, COALESCE(c.CategoryName, 'Inne') as CategoryName, j.Location, " +
                       "j.SalaryMIN, j.SalaryMAX, " +
                       "j.Description, COALESCE(os.StatusName, 'Aktywna') as StatusName " +
                       "FROM JobOffers j " +
                       "LEFT JOIN Categories c ON j.CategoryID = c.CategoryID " +
                       "LEFT JOIN OfferStatuses os ON j.OfferStatusID = os.OfferStatusID " +
                       "WHERE COALESCE(os.StatusName, 'Aktywna') = 'Aktywna'";

        System.out.println("Rozpoczynam pobieranie ofert z bazy danych dla wyszukiwarki...");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                JobOffer offer = new JobOffer(
                    rs.getInt("id"),
                    rs.getString("Title"),
                    rs.getString("CategoryName"),
                    rs.getString("Location"),
                    rs.getBigDecimal("SalaryMIN"),
                    rs.getBigDecimal("SalaryMAX"),
                    rs.getString("Description")
                );
                allOffers.add(offer);
            }
            System.out.println("Pomyślnie załadowano " + allOffers.size() + " aktywnych ofert z bazy danych do wyszukiwarki!");
        } catch (SQLException e) {
            System.err.println("⚠️ BŁĄD BAZY DANYCH (Wyszukiwarka): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadHistoryFromDatabase() {
        myApplications.clear();
        UserSession session = UserSession.getInstance();
        int loggedInCandidateId = session != null ? session.getUserId() : 1;

        String query = "SELECT j.OfferID as id, j.Title, COALESCE(c.CategoryName, 'Inne') as CategoryName, j.Location, " +
                       "j.SalaryMIN, j.SalaryMAX, " +
                       "j.Description, COALESCE(aps.StatusName, 'Przesłano') as app_status " +
                       "FROM Applications a " +
                       "JOIN JobOffers j ON a.OfferID = j.OfferID " +
                       "LEFT JOIN Categories c ON j.CategoryID = c.CategoryID " +
                       "LEFT JOIN ApplicationStatuses aps ON a.StatusID = aps.StatusID " +
                       "WHERE a.CandidateID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, loggedInCandidateId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JobOffer offer = new JobOffer(
                        rs.getInt("id"),
                        rs.getString("Title"),
                        rs.getString("CategoryName"),
                        rs.getString("Location"),
                        rs.getBigDecimal("SalaryMIN"),
                        rs.getBigDecimal("SalaryMAX"),
                        rs.getString("Description")
                    );
                    offer.setStatus(rs.getString("app_status"));
                    myApplications.add(offer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterData() {
        filteredOffers.setPredicate(offer -> {
            // Szybkie wyszukiwanie
            String searchKeyword = txtSearchUser.getText() == null ? "" : txtSearchUser.getText().toLowerCase();
            if (!searchKeyword.isEmpty() && !offer.getTitle().toLowerCase().contains(searchKeyword) && !offer.getDescription().toLowerCase().contains(searchKeyword)) {
                return false;
            }
            // Filtr: Tytuł
            String titleFilter = txtTitleFilter.getText() == null ? "" : txtTitleFilter.getText().toLowerCase();
            if (!titleFilter.isEmpty() && !offer.getTitle().toLowerCase().contains(titleFilter)) {
                return false;
            }
            // Filtr: Kategoria (Branża)
            String catFilter = comboCategoryFilter.getValue();
            if (catFilter != null && !catFilter.equals("Wszystkie") && !offer.getCategory().equals(catFilter)) {
                return false;
            }
            // Filtr: Lokalizacja
            String locFilter = txtLocationFilter.getText() == null ? "" : txtLocationFilter.getText().toLowerCase();
            if (!locFilter.isEmpty() && !offer.getLocation().toLowerCase().contains(locFilter)) {
                return false;
            }
            // Filtr: Minimalne wynagrodzenie
            String minSalText = txtMinSalaryFilter.getText() == null ? "" : txtMinSalaryFilter.getText().trim();
            if (!minSalText.isEmpty()) {
                try {
                    double minSalValue = Double.parseDouble(minSalText.replace(',', '.'));
                    String salaryRange = offer.getSalaryRange();
                    if (salaryRange != null) {
                        String firstPart = salaryRange.split(" - ")[0].replace(',', '.').replaceAll("[^0-9.]", "");
                        if (!firstPart.isEmpty()) {
                            double actualMin = Double.parseDouble(firstPart);
                            if (actualMin < minSalValue) {
                                return false;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignorujemy błędy parsowania
                }
            }
            return true; 
        });
    }

    private void handleViewDetails() {
        JobOffer selectedOffer = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            showAlert(Alert.AlertType.WARNING, "Brak wyboru", "Proszę wybrać ofertę z tabeli!");
            return;
        }

        UserSession session = UserSession.getInstance();
        int loggedInCandidateId = session != null ? session.getUserId() : 1;

        // Sprawdzenie czy kandydat już aplikował na tę ofertę
        boolean alreadyApplied = false;
        String checkQuery = "SELECT 1 FROM Applications WHERE OfferID = ? AND CandidateID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
            pstmt.setInt(1, selectedOffer.getId());
            pstmt.setInt(2, loggedInCandidateId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    alreadyApplied = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Szczegóły oferty");
        alert.setHeaderText(selectedOffer.getTitle() + " - " + selectedOffer.getCategory());
        
        String dialogContent = "Firma/Lokalizacja: " + selectedOffer.getLocation() + "\n" +
                               "Wynagrodzenie: " + selectedOffer.getSalaryRange() + "\n\n" +
                               "Opis oferty:\n" + selectedOffer.getDescription() + "\n\n";

        ButtonType applyButtonType = new ButtonType("Aplikuj na to stanowisko", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeButtonType = new ButtonType("Zamknij", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(closeButtonType);

        if (alreadyApplied) {
            dialogContent += "⚠ Już aplikowałeś na to ogłoszenie! Status: W toku.";
        } else {
            alert.getButtonTypes().add(0, applyButtonType);
        }

        alert.setContentText(dialogContent);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == applyButtonType) {
            applyForJob(selectedOffer);
        }
    }

    private void handleViewDetailsHistory() {
        JobOffer selectedOffer = tableUsers1.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            showAlert(Alert.AlertType.WARNING, "Brak wyboru", "Proszę wybrać ofertę z tabeli historii!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Szczegóły oferty");
        alert.setHeaderText(selectedOffer.getTitle() + " - " + selectedOffer.getCategory());
        
        String dialogContent = "Firma/Lokalizacja: " + selectedOffer.getLocation() + "\n" +
                               "Wynagrodzenie: " + selectedOffer.getSalaryRange() + "\n\n" +
                               "Opis oferty:\n" + selectedOffer.getDescription() + "\n\n" +
                               "⚠ Już aplikowałeś na to ogłoszenie! Status: " + selectedOffer.getStatus() + ".";

        alert.setContentText(dialogContent);
        alert.showAndWait();
    }

    private void applyForJob(JobOffer offer) {
        UserSession session = UserSession.getInstance();
        int loggedInCandidateId = session != null ? session.getUserId() : 1;

        // Domyślnie nowa aplikacja ma status 'Oczekująca' (ID = 1 w bazie danych dla StatusName = 'Oczekująca')
        String insertQuery = "INSERT INTO Applications (OfferID, CandidateID, StatusID) VALUES (?, ?, 1)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            
            pstmt.setInt(1, offer.getId());
            pstmt.setInt(2, loggedInCandidateId);
            pstmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Sukces!", "Pomyślnie wysłano aplikację na stanowisko " + offer.getTitle() + "!");
            loadHistoryFromDatabase(); // Odświeżenie historii aplikacji
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zapisać aplikacji w bazie danych.");
        }
    }

    // Twoja metoda podpięta pod przycisk "Filtry"
    @FXML
    private void obslugaPrzyciskuFiltry() {
        boolean pokazywac = !panelFiltrow.isVisible(); 
        
        panelFiltrow.setVisible(pokazywac);

        if (pokazywac) {
            // Pokazuje filtry i przywraca rozmiar ze Scene Buildera
            panelFiltrow.setPrefHeight(HBox.USE_COMPUTED_SIZE); 
            panelFiltrow.setMinHeight(HBox.USE_COMPUTED_SIZE);
        } else {
            // Chowa filtry i zwija przestrzeń do zera
            panelFiltrow.setPrefHeight(0);
            panelFiltrow.setMinHeight(0);
            // Wyczyszczenie filtrów po schowaniu panelu
            txtTitleFilter.clear();
            comboCategoryFilter.getSelectionModel().selectFirst();
            txtLocationFilter.clear();
            txtMinSalaryFilter.clear();
            filterData();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        // Czyszczenie sesji użytkownika
        UserSession.clear();
        
        // Przekierowanie do widoku logowania
        App.setRoot("login");
    }

    private void filterHistoryData() {
        filteredHistory.setPredicate(offer -> {
            String searchKeyword = txtSearchUser1.getText() == null ? "" : txtSearchUser1.getText().toLowerCase().trim();
            if (searchKeyword.isEmpty()) {
                return true;
            }
            
            // Wyszukiwanie wszystkiego poza wynagrodzeniem
            if (offer.getTitle() != null && offer.getTitle().toLowerCase().contains(searchKeyword)) {
                return true;
            }
            if (offer.getCategory() != null && offer.getCategory().toLowerCase().contains(searchKeyword)) {
                return true;
            }
            if (offer.getLocation() != null && offer.getLocation().toLowerCase().contains(searchKeyword)) {
                return true;
            }
            if (offer.getDescription() != null && offer.getDescription().toLowerCase().contains(searchKeyword)) {
                return true;
            }
            if (offer.getStatus() != null && offer.getStatus().toLowerCase().contains(searchKeyword)) {
                return true;
            }
            
            return false;
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void handleMojProfil(ActionEvent event) throws IOException {
        App.setRoot("profil");
    }
    
}
