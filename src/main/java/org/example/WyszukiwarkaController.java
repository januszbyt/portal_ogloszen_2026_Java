package org.example;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region; // DODANO: Do ustalania rozmiaru
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow; // DODANO: Do nałożenia cienia na ikonę
import javafx.scene.paint.Color;       // DODANO: Do określenia koloru cienia
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class WyszukiwarkaController {

    @FXML private TabPane tabPane;
    @FXML private HBox panelFiltrow;
    @FXML private Label lblWelcome;

    // Kontrolki filtrowania i wyszukiwania
    @FXML private TextField txtSearchUser;
    @FXML private TextField txtTitleFilter;
    @FXML private TextField txtSearchUser1;
    @FXML private ComboBox<String> comboCategoryFilter;
    @FXML private TextField txtLocationFilter;
    @FXML private TextField txtMinSalaryFilter;

    // Tabela Dostępnych Ofert
    @FXML private TableView<JobOffer> tableUsers;
    @FXML private TableColumn<JobOffer, String> colCompany;   // Pracodawca
    @FXML private TableColumn<JobOffer, String> colUserId;    // Tytuł
    @FXML private TableColumn<JobOffer, String> colUserRole;  // Kategoria
    @FXML private TableColumn<JobOffer, String> colUserEmail; // Lokalizacja
    @FXML private TableColumn<JobOffer, String> colUserDate;  // Wynagrodzenie
    @FXML private TableColumn<JobOffer, String> colUserStatus;// Status

    // Tabela Historii Ogłoszeń
    @FXML private TableView<JobOffer> tableUsers1;
    @FXML private TableColumn<JobOffer, String> colCompany1;   // Pracodawca
    @FXML private TableColumn<JobOffer, String> colUserId1;
    @FXML private TableColumn<JobOffer, String> colUserRole1;
    @FXML private TableColumn<JobOffer, String> colUserEmail1;
    @FXML private TableColumn<JobOffer, String> colUserDate1;
    @FXML private TableColumn<JobOffer, String> colUserStatus1;
    
    @FXML private Button btnViewOffer1;
    @FXML private Button btnViewOffer11;
    
    // Przycisk do zmiany motywu
    @FXML private Button btnToggleTheme;

    private static final ObservableList<JobOffer> allOffers = FXCollections.observableArrayList();
    private final ObservableList<JobOffer> myApplications = FXCollections.observableArrayList();
    private FilteredList<JobOffer> filteredOffers;
    private FilteredList<JobOffer> filteredHistory;

    @FXML
    public void initialize() {
        if (btnToggleTheme != null) {
            if (App.isDarkMode) {
                btnToggleTheme.setText("☀ Jasny Motyw");
            } else {
                btnToggleTheme.setText("🌙 Ciemny Motyw");
            }
        }

        panelFiltrow.managedProperty().bind(panelFiltrow.visibleProperty());
        panelFiltrow.setVisible(false);
        panelFiltrow.setPrefHeight(0);
        panelFiltrow.setMinHeight(0);

        UserSession session = UserSession.getInstance();
        if (session != null && session.getFirstName() != null) {
            lblWelcome.setText("Witaj, " + session.getFirstName() + "!");
        }

        colCompany.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("title"));
        colUserRole.setCellValueFactory(new PropertyValueFactory<>("category"));
        colUserEmail.setCellValueFactory(new PropertyValueFactory<>("location"));
        colUserDate.setCellValueFactory(new PropertyValueFactory<>("salaryRange"));
        colUserStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colCompany1.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        colUserId1.setCellValueFactory(new PropertyValueFactory<>("title"));
        colUserRole1.setCellValueFactory(new PropertyValueFactory<>("category"));
        colUserEmail1.setCellValueFactory(new PropertyValueFactory<>("location"));
        colUserDate1.setCellValueFactory(new PropertyValueFactory<>("salaryRange"));
        colUserStatus1.setCellValueFactory(new PropertyValueFactory<>("status"));

        comboCategoryFilter.setItems(FXCollections.observableArrayList("Wszystkie", "IT / Software", "Budownictwo", "Finanse", "Sprzedaż"));
        comboCategoryFilter.getSelectionModel().selectFirst();

        loadOffersFromDatabase();
        loadHistoryFromDatabase();

        filteredOffers = new FilteredList<>(allOffers, b -> true);
        SortedList<JobOffer> sortedOffers = new SortedList<>(filteredOffers);
        sortedOffers.comparatorProperty().bind(tableUsers.comparatorProperty());
        tableUsers.setItems(sortedOffers);

        filteredHistory = new FilteredList<>(myApplications, b -> true);
        SortedList<JobOffer> sortedHistory = new SortedList<>(filteredHistory);
        sortedHistory.comparatorProperty().bind(tableUsers1.comparatorProperty());
        tableUsers1.setItems(sortedHistory);

        txtSearchUser1.textProperty().addListener((observable, oldValue, newValue) -> filterHistoryData());
        txtSearchUser.textProperty().addListener((observable, oldValue, newValue) -> filterData());
        txtTitleFilter.textProperty().addListener((observable, oldValue, newValue) -> filterData());
        txtLocationFilter.textProperty().addListener((observable, oldValue, newValue) -> filterData());
        txtMinSalaryFilter.textProperty().addListener((observable, oldValue, newValue) -> filterData());
        comboCategoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> filterData());

        tabPane.getSelectionModel().selectedIndexProperty().addListener(
            (ChangeListener<Number>) (obs, oldIndex, newIndex) -> {
                if (newIndex.intValue() == 0) {
                    loadOffersFromDatabase();
                    filterData();
                } else if (newIndex.intValue() == 1) {
                    loadHistoryFromDatabase();
                    filterHistoryData();
                }
            }
        );

        btnViewOffer1.disableProperty().bind(tableUsers.getSelectionModel().selectedItemProperty().isNull());
        btnViewOffer11.disableProperty().bind(tableUsers1.getSelectionModel().selectedItemProperty().isNull());

        btnViewOffer1.setOnAction(event -> handleViewDetails());
        btnViewOffer11.setOnAction(event -> handleViewDetailsHistory());
    }

    private void loadOffersFromDatabase() {
        allOffers.clear();

        UserSession session = UserSession.getInstance();
        int candidateId = session != null ? session.getUserId() : -1;

        String query = "SELECT j.OfferID as id, j.Title, COALESCE(c.CategoryName, 'Inne') as CategoryName, j.Location, " +
                       "j.SalaryMIN, j.SalaryMAX, j.Description, " +
                       "COALESCE(e.CompanyName, 'Brak danych') as CompanyName, " +
                       "COALESCE(aps.StatusName, 'Nie aplikowano') as CandidateStatus " +
                       "FROM JobOffers j " +
                       "LEFT JOIN Categories c ON j.CategoryID = c.CategoryID " +
                       "LEFT JOIN OfferStatuses os ON j.OfferStatusID = os.OfferStatusID " +
                       "LEFT JOIN Employers e ON j.EmployerID = e.EmployerID " +
                       "LEFT JOIN Applications a ON j.OfferID = a.OfferID AND a.CandidateID = ? " +
                       "LEFT JOIN ApplicationStatuses aps ON a.StatusID = aps.StatusID " +
                       "WHERE COALESCE(os.StatusName, 'Aktywna') = 'Aktywna'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, candidateId);
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
                    offer.setCompanyName(rs.getString("CompanyName"));
                    offer.setStatus(rs.getString("CandidateStatus"));
                    allOffers.add(offer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHistoryFromDatabase() {
        myApplications.clear();
        UserSession session = UserSession.getInstance();
        int loggedInCandidateId = session != null ? session.getUserId() : 1;

        String query = "SELECT j.OfferID as id, j.Title, COALESCE(c.CategoryName, 'Inne') as CategoryName, j.Location, " +
                       "j.SalaryMIN, j.SalaryMAX, j.Description, " +
                       "COALESCE(aps.StatusName, 'Przesłano') as app_status, " +
                       "COALESCE(e.CompanyName, 'Brak danych') as CompanyName " +
                       "FROM Applications a " +
                       "JOIN JobOffers j ON a.OfferID = j.OfferID " +
                       "LEFT JOIN Categories c ON j.CategoryID = c.CategoryID " +
                       "LEFT JOIN ApplicationStatuses aps ON a.StatusID = aps.StatusID " +
                       "LEFT JOIN Employers e ON j.EmployerID = e.EmployerID " +
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
                    offer.setCompanyName(rs.getString("CompanyName"));
                    myApplications.add(offer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterData() {
        filteredOffers.setPredicate(offer -> {
            String searchKeyword = txtSearchUser.getText() == null ? "" : txtSearchUser.getText().toLowerCase().trim();
            if (!searchKeyword.isEmpty()) {
                boolean matchesTitle = offer.getTitle() != null && offer.getTitle().toLowerCase().contains(searchKeyword);
                boolean matchesDesc = offer.getDescription() != null && offer.getDescription().toLowerCase().contains(searchKeyword);
                boolean matchesCompany = offer.getCompanyName() != null && offer.getCompanyName().toLowerCase().contains(searchKeyword);
                boolean matchesCategory = offer.getCategory() != null && offer.getCategory().toLowerCase().contains(searchKeyword);
                
                if (!matchesTitle && !matchesDesc && !matchesCompany && !matchesCategory) {
                    return false;
                }
            }
            String titleFilter = txtTitleFilter.getText() == null ? "" : txtTitleFilter.getText().toLowerCase();
            if (!titleFilter.isEmpty() && !offer.getTitle().toLowerCase().contains(titleFilter)) {
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
            String minSalText = txtMinSalaryFilter.getText() == null ? "" : txtMinSalaryFilter.getText().trim();
            if (!minSalText.isEmpty()) {
                try {
                    double minSalValue = Double.parseDouble(minSalText.replace(',', '.'));
                    if (offer.getSalaryMax() != null) {
                        if (offer.getSalaryMax().doubleValue() < minSalValue) {
                            return false;
                        }
                    } else if (offer.getSalaryMin() != null) {
                        if (offer.getSalaryMin().doubleValue() < minSalValue) {
                            return false;
                        }
                    }
                } catch (NumberFormatException e) { }
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

        boolean hasCv = false;
        String cvQuery = "SELECT CVFilePath FROM Candidates WHERE CandidateID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(cvQuery)) {
            pstmt.setInt(1, loggedInCandidateId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String cvPath = rs.getString("CVFilePath");
                    if (cvPath != null && !cvPath.trim().isEmpty()) {
                        hasCv = true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Szczegóły oferty");
        alert.setHeaderText(selectedOffer.getTitle() + " - " + selectedOffer.getCategory());
        applyStylesToDialog(alert);

        String dialogContent = "Firma/Lokalizacja: " + selectedOffer.getLocation() + "\n" +
                               "Wynagrodzenie: " + selectedOffer.getSalaryRange() + "\n\n" +
                               "Opis oferty:\n" + selectedOffer.getDescription() + "\n\n";

        ButtonType applyButtonType = new ButtonType("Aplikuj na to stanowisko", ButtonBar.ButtonData.OK_DONE);
        ButtonType withdrawButtonType = new ButtonType("Zrezygnuj z aplikacji", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeButtonType = new ButtonType("Zamknij", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(closeButtonType);

        if (alreadyApplied) {
            dialogContent += "⚠ Już aplikowałeś na to ogłoszenie! Status: " + selectedOffer.getStatus() + ".";
            alert.getButtonTypes().add(0, withdrawButtonType);
        } else {
            alert.getButtonTypes().add(0, applyButtonType);
            if (!hasCv) {
                dialogContent += "⚠ Uwaga: Brak załączonego CV w Twoim profilu! Aplikowanie prześle puste zgłoszenie.";
            } else {
                dialogContent += "✓ Posiadasz załączone CV w profilu.";
            }
        }

        TextArea textArea = new TextArea(dialogContent);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(500);
        textArea.setPrefHeight(300);
        alert.getDialogPane().setContent(textArea);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == applyButtonType) {
                if (!hasCv) {
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("POTWIERDZENIE");
                    confirmAlert.setHeaderText("Uwaga: Nie masz dodanego pliku CV!");
                    confirmAlert.setContentText("Czy na pewno chcesz aplikować na tę ofertę bez załączonego pliku CV?\n(Zalecamy najpierw dodać go w zakładce 'Mój Profil')");
                    applyStylesToDialog(confirmAlert);
                    
                    Optional<ButtonType> confirmation = confirmAlert.showAndWait();
                    if (confirmation.isPresent() && confirmation.get() != ButtonType.OK) {
                        return; 
                    }
                }
                applyForJob(selectedOffer);
            } else if (result.get() == withdrawButtonType) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("POTWIERDZENIE");
                confirmAlert.setHeaderText("Wycofanie aplikacji");
                confirmAlert.setContentText("Czy na pewno chcesz wycofać swoją aplikację na stanowisko " + selectedOffer.getTitle() + "?");
                applyStylesToDialog(confirmAlert);

                Optional<ButtonType> confirmation = confirmAlert.showAndWait();
                if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                    withdrawApplication(selectedOffer.getId(), loggedInCandidateId, selectedOffer.getTitle());
                }
            }
        }
    }

    private void handleViewDetailsHistory() {
        JobOffer selectedOffer = tableUsers1.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            showAlert(Alert.AlertType.WARNING, "Brak wyboru", "Proszę wybrać ofertę z tabeli historii!");
            return;
        }

        UserSession session = UserSession.getInstance();
        int loggedInCandidateId = session != null ? session.getUserId() : 1;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Szczegóły oferty");
        alert.setHeaderText(selectedOffer.getTitle() + " - " + selectedOffer.getCategory());
        applyStylesToDialog(alert);

        String dialogContent = "Firma/Lokalizacja: " + selectedOffer.getLocation() + "\n" +
                               "Wynagrodzenie: " + selectedOffer.getSalaryRange() + "\n\n" +
                               "Opis oferty:\n" + selectedOffer.getDescription() + "\n\n" +
                               "⚠ Już aplikowałeś na to ogłoszenie! Status: " + selectedOffer.getStatus() + ".";

        ButtonType withdrawButtonType = new ButtonType("Zrezygnuj z aplikacji", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeButtonType = new ButtonType("Zamknij", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(closeButtonType);
        alert.getButtonTypes().add(0, withdrawButtonType);

        TextArea textArea = new TextArea(dialogContent);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(500);
        textArea.setPrefHeight(300);
        alert.getDialogPane().setContent(textArea);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == withdrawButtonType) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("POTWIERDZENIE");
            confirmAlert.setHeaderText("Wycofanie aplikacji");
            confirmAlert.setContentText("Czy na pewno chcesz wycofać swoją aplikację na stanowisko " + selectedOffer.getTitle() + "?");
            applyStylesToDialog(confirmAlert);

            Optional<ButtonType> confirmation = confirmAlert.showAndWait();
            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                withdrawApplication(selectedOffer.getId(), loggedInCandidateId, selectedOffer.getTitle());
            }
        }
    }

    private void withdrawApplication(int offerId, int candidateId, String offerTitle) {
        String deleteQuery = "DELETE FROM Applications WHERE OfferID = ? AND CandidateID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, offerId);
            pstmt.setInt(2, candidateId);
            pstmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Pomyślnie wycofano aplikację na stanowisko " + offerTitle + ".");

            loadOffersFromDatabase();
            filterData();
            loadHistoryFromDatabase();
            filterHistoryData();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się wycofać aplikacji z bazy danych.");
        }
    }

    private void applyForJob(JobOffer offer) {
        UserSession session = UserSession.getInstance();
        int loggedInCandidateId = session != null ? session.getUserId() : 1;

        String insertQuery = "INSERT INTO Applications (OfferID, CandidateID, StatusID) VALUES (?, ?, 1)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setInt(1, offer.getId());
            pstmt.setInt(2, loggedInCandidateId);
            pstmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Sukces!", "Pomyślnie wysłano aplikację na stanowisko " + offer.getTitle() + "!");
            loadOffersFromDatabase();
            filterData();
            loadHistoryFromDatabase(); 
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zapisać aplikacji w bazie danych.");
        }
    }

    @FXML
    private void obslugaPrzyciskuFiltry() {
        boolean pokazywac = !panelFiltrow.isVisible();
        panelFiltrow.setVisible(pokazywac);

        if (pokazywac) {
            panelFiltrow.setPrefHeight(HBox.USE_COMPUTED_SIZE);
            panelFiltrow.setMinHeight(HBox.USE_COMPUTED_SIZE);
        } else {
            panelFiltrow.setPrefHeight(0);
            panelFiltrow.setMinHeight(0);
            txtTitleFilter.clear();
            comboCategoryFilter.getSelectionModel().selectFirst();
            txtLocationFilter.clear();
            txtMinSalaryFilter.clear();
            filterData();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        UserSession.clear();
        App.setRoot("login");
    }

    private void filterHistoryData() {
        filteredHistory.setPredicate(offer -> {
            String searchKeyword = txtSearchUser1.getText() == null ? "" : txtSearchUser1.getText().toLowerCase().trim();
            if (searchKeyword.isEmpty()) {
                return true;
            }

            if (offer.getTitle() != null && offer.getTitle().toLowerCase().contains(searchKeyword)) return true;
            if (offer.getCategory() != null && offer.getCategory().toLowerCase().contains(searchKeyword)) return true;
            if (offer.getLocation() != null && offer.getLocation().toLowerCase().contains(searchKeyword)) return true;
            if (offer.getDescription() != null && offer.getDescription().toLowerCase().contains(searchKeyword)) return true;
            if (offer.getStatus() != null && offer.getStatus().toLowerCase().contains(searchKeyword)) return true;

            return false;
        });
    }

    @FXML
    private void handleRefreshOffers(ActionEvent event) {
        loadOffersFromDatabase();
        filterData();
    }

    @FXML
    private void handleRefreshHistory(ActionEvent event) {
        loadHistoryFromDatabase();
        filterHistoryData();
    }

   
    // NOWA FUNKCJA STYLOWANIA OKIEN (Z ROZMIARAMI I CIENIEM) =============================
    
    private void applyStylesToDialog(Dialog<?> dialog) {
        try {
            // Wczytanie CSS
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("wyszukiwarka.css").toExternalForm());
            
            // Tryb ciemny
            if (App.isDarkMode) {
                dialog.getDialogPane().getStyleClass().add("dark-mode");
            }

            // Wymuszenie dopasowania wielkości okna do zawartości tekstu
            dialog.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
            dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

            // Wczytanie grafiki i nałożenie stylów
            try {
                Image logoIcon = new Image(getClass().getResourceAsStream("/org/example/pictures/LogoIcon.png"));
                
                Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                stage.getIcons().add(logoIcon);
                
                ImageView iconView = new ImageView(logoIcon);
                // Zmniejszamy logo, żeby miało więcej luzu wewnątrz białej ramki
                iconView.setFitWidth(34);
                iconView.setFitHeight(34);
                iconView.setPreserveRatio(true); // Gwarantuje idealne proporcje (pomaga w centrowaniu)
                
                // Tworzymy pojemnik na ikonę
                javafx.scene.layout.StackPane iconContainer = new javafx.scene.layout.StackPane(iconView);
                iconContainer.setAlignment(javafx.geometry.Pos.CENTER); // Wymusza idealne ułożenie na środku
                iconContainer.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12px; -fx-padding: 8px;");
                
                // Nakładamy miękki, rozmyty cień
                DropShadow dropShadow = new DropShadow();
                dropShadow.setColor(Color.rgb(0, 0, 0, 0.25)); // Subtelny, dość przezroczysty czarny
                dropShadow.setRadius(12); // Większy promień = większe i bardziej miękkie rozmycie
                dropShadow.setSpread(0.05); // Bardzo małe nasycenie brzegów cienia
                dropShadow.setOffsetY(3); // Cień rzucany lekko w dół
                iconContainer.setEffect(dropShadow);

                dialog.setGraphic(iconContainer);
                
            } catch (Exception e) {
                System.err.println("Nie udało się załadować LogoIcon.png: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Nie udało się załadować stylów dla okna dialogowego.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        applyStylesToDialog(alert);
        alert.showAndWait();
    }

    @FXML
    private void handleMojProfil(ActionEvent event) throws IOException {
        App.setRoot("profil");
    }

    @FXML
    private void handleToggleTheme(ActionEvent event) {
        App.isDarkMode = !App.isDarkMode;

        if (btnToggleTheme != null && btnToggleTheme.getScene() != null) {
            App.applyTheme(btnToggleTheme.getScene());
        } else if (tabPane != null && tabPane.getScene() != null) {
            App.applyTheme(tabPane.getScene());
        }

        if (btnToggleTheme != null) {
            if (App.isDarkMode) {
                btnToggleTheme.setText("☀ Jasny Motyw"); 
            } else {
                btnToggleTheme.setText("🌙 Ciemny Motyw");
            }
        } else if (event.getSource() instanceof Button) {
            Button clickedBtn = (Button) event.getSource();
            if (App.isDarkMode) {
                clickedBtn.setText("☀ Jasny Motyw"); 
            } else {
                clickedBtn.setText("🌙 Ciemny Motyw");
            }
        }
    }
}