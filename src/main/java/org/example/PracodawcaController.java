package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
    
    @FXML private Button btnToggleTheme;

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

        if (btnToggleTheme != null) {
            if (App.isDarkMode) {
                btnToggleTheme.setText("☀ Jasny Motyw");
            } else {
                btnToggleTheme.setText("🌙 Ciemny Motyw");
            }
        }

        loadDictionariesFromDB();
        categoryCombo.setItems(FXCollections.observableArrayList(categoryMap.keySet()));

        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salaryRange")); 
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

        candidatesListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleCandidateDoubleClick();
            }
        });
    }

    @FXML
    private void handleToggleTheme(ActionEvent event) {
        App.isDarkMode = !App.isDarkMode;

        if (btnToggleTheme != null && btnToggleTheme.getScene() != null) {
            App.applyTheme(btnToggleTheme.getScene());
        }

        if (btnToggleTheme != null) {
            if (App.isDarkMode) {
                btnToggleTheme.setText("☀ Jasny Motyw"); 
            } else {
                btnToggleTheme.setText("🌙 Ciemny Motyw");
            }
        }
    }

    private void handleCandidateDoubleClick() {
        int index = candidatesListView.getSelectionModel().getSelectedIndex();
        if (index < 0) return; 

        int appId = currentApplicationsIdList.get(index);
        String sql = "SELECT c.FirstName, c.LastName, c.CVFilePath, c.LinkedinURL, c.GithubURL, u.Email, s.StatusName " +
                     "FROM Applications a " +
                     "JOIN Candidates c ON a.CandidateID = c.CandidateID " +
                     "JOIN Users u ON c.CandidateID = u.UserID " +
                     "JOIN ApplicationStatuses s ON a.StatusID = s.StatusID " +
                     "WHERE a.ApplicationID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, appId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("FirstName");
                    String lastName = rs.getString("LastName");
                    String email = rs.getString("Email");
                    String cvPath = rs.getString("CVFilePath");
                    String linkedin = rs.getString("LinkedinURL") != null ? rs.getString("LinkedinURL") : "Brak";
                    String github = rs.getString("GithubURL") != null ? rs.getString("GithubURL") : "Brak";
                    String status = rs.getString("StatusName");

                    Dialog<Void> dialog = new Dialog<>();
                    dialog.setTitle("Profil Kandydata");
                    dialog.setHeaderText("Szczegółowe dane kandydata");

                    applyStylesToDialog(dialog);

                    ButtonType closeButton = new ButtonType("Zamknij", ButtonBar.ButtonData.CANCEL_CLOSE);
                    dialog.getDialogPane().getButtonTypes().add(closeButton);

                    VBox dialogContent = new VBox(12);
                    dialogContent.setPadding(new Insets(20));
                    dialogContent.setPrefWidth(450);

                    Label lblName = new Label("👤 Imię i Nazwisko: " + firstName + " " + lastName);
                    lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    
                    Label lblEmail = new Label("✉ Adres E-mail: " + email);
                    Label lblStatus = new Label("📋 Status aplikacji: " + status);
                    Label lblLinkedin = new Label("🔗 LinkedIn: " + linkedin);
                    Label lblGithub = new Label("💻 GitHub: " + github);

                    dialogContent.getChildren().addAll(lblName, lblEmail, lblStatus, lblLinkedin, lblGithub);

                    if (cvPath != null && !cvPath.isEmpty()) {
                        Button btnOpenCv = new Button("📄 Wyświetl plik CV (PDF)");
                        btnOpenCv.setMaxWidth(Double.MAX_VALUE);
                        
                        btnOpenCv.setOnAction(e -> {
                            try {
                                java.io.File file = new java.io.File(cvPath);
                                
                                if (!file.exists()) {
                                    file = new java.io.File("cv/" + file.getName());
                                }

                                if (file.exists()) {
                                    if (java.awt.Desktop.isDesktopSupported()) {
                                        java.awt.Desktop.getDesktop().open(file);
                                    } else {
                                        new ProcessBuilder("cmd", "/c", "start", file.getAbsolutePath()).start();
                                    }
                                } else {
                                    showAlert(Alert.AlertType.ERROR, "Błąd pliku", "Nie znaleziono pliku CV na dysku serwera pod ścieżką: " + cvPath);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Błąd systemu", "Nie udało się otworzyć dokumentu PDF: " + ex.getMessage());
                            }
                        });

                        dialogContent.getChildren().add(new Separator());
                        dialogContent.getChildren().add(btnOpenCv);
                    } else {
                        Label lblNoCv = new Label("❌ Brak załączonego pliku CV dla tej aplikacji.");
                        lblNoCv.setStyle("-fx-text-fill: #d9534f; -fx-font-style: italic;");
                        dialogContent.getChildren().add(new Separator());
                        dialogContent.getChildren().add(lblNoCv);
                    }

                    dialog.getDialogPane().setContent(dialogContent);
                    
                    // DODANO: Znajdujemy wygenerowany przycisk "Zamknij" i nakładamy mu czerwoną klasę CSS
                    Button closeBtnNode = (Button) dialog.getDialogPane().lookupButton(closeButton);
                    if (closeBtnNode != null) {
                        closeBtnNode.getStyleClass().add("button-danger");
                    }
                    
                    dialog.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Nie udało się wczytać profilu kandydata.");
        }
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

        String sql = "SELECT a.ApplicationID, c.FirstName, c.LastName, s.StatusName FROM Applications a " +
                     "JOIN Candidates c ON a.CandidateID = c.CandidateID " +
                     "JOIN ApplicationStatuses s ON a.StatusID = s.StatusID " +
                     "WHERE a.OfferID = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, offerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String candidateInfo = rs.getString("FirstName") + " " + rs.getString("LastName") + " (" + rs.getString("StatusName") + ")";
                
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
            
            if (sMin.isEmpty()) {
                pstmt.setNull(5, java.sql.Types.DECIMAL);
            } else {
                pstmt.setBigDecimal(5, new BigDecimal(sMin));
            }

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
        salaryMinField.setText(selectedOfferForEdit.getSalaryMin() != null ? selectedOfferForEdit.getSalaryMin().toString() : "");
        salaryMaxField.setText(selectedOfferForEdit.getSalaryMax() != null ? selectedOfferForEdit.getSalaryMax().toString() : "");
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
        alert.setTitle("Potwierdzenie usunięcia");
        alert.setHeaderText(null);
        applyStylesToDialog(alert); 

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
    private void handleMojProfil(ActionEvent event) throws IOException {
        App.setRoot("profil");
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
        applyStylesToDialog(alert); 
        alert.showAndWait();
    }

    private void applyStylesToDialog(Dialog<?> dialog) {
        try {
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("pracodawca.css").toExternalForm());
            
            if (App.isDarkMode) {
                dialog.getDialogPane().getStyleClass().add("dark-mode");
            }

            dialog.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
            dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

            // Sprawdzenie, czy okienko jest oknem błędu
            boolean isErrorAlert = (dialog instanceof Alert && ((Alert) dialog).getAlertType() == Alert.AlertType.ERROR);

            try {
                Image logoIcon = new Image(getClass().getResourceAsStream("/org/example/pictures/LogoIcon.png"));
                
                Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                stage.getIcons().add(logoIcon);
                
                ImageView iconView = new ImageView(logoIcon);
                iconView.setFitWidth(34);
                iconView.setFitHeight(34);
                iconView.setPreserveRatio(true);
                
                StackPane iconContainer = new StackPane(iconView);
                iconContainer.setAlignment(javafx.geometry.Pos.CENTER);
                
                // DODANO: Zmiana tła, obramowania i koloru cienia, jeśli to okno ERROR
                if (isErrorAlert) {
                    iconContainer.setStyle("-fx-background-color: #fee2e2; -fx-background-radius: 12px; -fx-padding: 8px; -fx-border-color: #ef4444; -fx-border-radius: 12px; -fx-border-width: 2px;");
                    
                    DropShadow dropShadow = new DropShadow();
                    dropShadow.setColor(Color.rgb(239, 68, 68, 0.4)); // Czerwony cień
                    dropShadow.setRadius(12);
                    dropShadow.setSpread(0.1);
                    dropShadow.setOffsetY(3);
                    iconContainer.setEffect(dropShadow);
                } else {
                    iconContainer.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12px; -fx-padding: 8px;");
                    
                    DropShadow dropShadow = new DropShadow();
                    dropShadow.setColor(Color.rgb(0, 0, 0, 0.25)); // Standardowy czarny cień
                    dropShadow.setRadius(12);
                    dropShadow.setSpread(0.05);
                    dropShadow.setOffsetY(3);
                    iconContainer.setEffect(dropShadow);
                }

                dialog.setGraphic(iconContainer);
                
            } catch (Exception e) {
                System.err.println("Nie udało się załadować LogoIcon.png: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Nie udało się załadować stylów dla okna dialogowego.");
        }
    }
}