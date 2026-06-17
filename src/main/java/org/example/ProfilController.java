package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region; // DODANO
import javafx.scene.image.Image; // DODANO
import javafx.scene.image.ImageView; // DODANO
import javafx.scene.effect.DropShadow; // DODANO
import javafx.scene.paint.Color; // DODANO
import javafx.stage.Stage; // DODANO

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ProfilController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtOldPassword;
    @FXML private PasswordField txtNewPassword;

    // Elementy UI dla Kandydata
    @FXML private VBox boxCandidate;
    @FXML private TextField txtFirstName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtCvPath;
    @FXML private TextField txtLinkedin;
    @FXML private TextField txtGithub;

    // Elementy UI dla Pracodawcy
    @FXML private VBox boxEmployer;
    @FXML private TextField txtCompanyName;
    @FXML private TextField txtNip;
    @FXML private TextArea txtDescription;

    // Przycisk do zmiany motywu
    @FXML private Button btnToggleTheme; // DODANO

    private int userId;
    private String userRole;
    private String dbPasswordHash;
    private java.io.File selectedCvFile = null;
    private String currentCvPath = null;

    @FXML
    public void initialize() {
        // DODANO: Inicjalizacja tekstu przycisku motywu
        if (btnToggleTheme != null) {
            if (App.isDarkMode) {
                btnToggleTheme.setText("☀ Jasny Motyw");
            } else {
                btnToggleTheme.setText("🌙 Ciemny Motyw");
            }
        }

        UserSession session = UserSession.getInstance();
        if (session == null) {
            try { App.setRoot("login"); } catch (IOException ignored) {}
            return;
        }

        userId = session.getUserId();
        userRole = session.getRole();

        // Dynamiczne ukrywanie sekcji na podstawie aktywnej roli
        if ("Candidate".equals(userRole)) {
            boxEmployer.setVisible(false);
            boxEmployer.setManaged(false);
        } else if ("Employer".equals(userRole)) {
            boxCandidate.setVisible(false);
            boxCandidate.setManaged(false);
        }

        loadUserData();
    }

    private void loadUserData() {
        // Wczytywanie kluczowych danych autoryzacji (Users)
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT Email, PasswordHash FROM Users WHERE UserID = ?")) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                txtEmail.setText(rs.getString("Email"));
                dbPasswordHash = rs.getString("PasswordHash");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Wczytywanie dedykowanych danych (Kandydat / Pracodawca)
        if ("Candidate".equals(userRole)) {
            String sql = "SELECT FirstName, LastName, CVFilePath, LinkedinURL, GithubURL FROM Candidates WHERE CandidateID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    txtFirstName.setText(rs.getString("FirstName"));
                    txtLastName.setText(rs.getString("LastName"));
                    currentCvPath = rs.getString("CVFilePath");
                    if (currentCvPath != null) {
                        java.io.File file = new java.io.File(currentCvPath);
                        txtCvPath.setText(file.getName());
                    }
                    txtLinkedin.setText(rs.getString("LinkedinURL"));
                    txtGithub.setText(rs.getString("GithubURL"));
                }
            } catch (SQLException e) { e.printStackTrace(); }
        } else if ("Employer".equals(userRole)) {
            String sql = "SELECT CompanyName, Description, NIP FROM Employers WHERE EmployerID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    txtCompanyName.setText(rs.getString("CompanyName"));
                    txtDescription.setText(rs.getString("Description"));
                    txtNip.setText(rs.getString("NIP"));
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        String email = txtEmail.getText() == null ? "" : txtEmail.getText().trim();
        String oldPass = txtOldPassword.getText() == null ? "" : txtOldPassword.getText();
        String newPass = txtNewPassword.getText() == null ? "" : txtNewPassword.getText();

        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Pole e-mail nie może być puste.");
            return;
        }

        // Walidacja zmiany hasła
        if (!newPass.isEmpty()) {
            if (oldPass.isEmpty() || !dbPasswordHash.equals(SecurityUtils.hashPassword(oldPass))) {
                showAlert(Alert.AlertType.ERROR, "Błąd autoryzacji", "Aby zmienić hasło, musisz podać poprawne OBECNE hasło.");
                return;
            }
            if (newPass.length() < 8 || !newPass.matches(".*[A-Z].*") || !newPass.matches(".*\\d.*") || !newPass.matches(".*[^a-zA-Z0-9].*")) {
                showAlert(Alert.AlertType.WARNING, "Słabe hasło", "Nowe hasło musi mieć min. 8 znaków, wielką literę, cyfrę i znak specjalny.");
                return;
            }
        }

        // Walidacja Modulo 11 dla NIPu pracodawcy
        if ("Employer".equals(userRole)) {
            String nip = txtNip.getText() == null ? "" : txtNip.getText().trim();
            if (!nip.isEmpty() && !isValidNIP(nip)) {
                showAlert(Alert.AlertType.ERROR, "Błędny NIP", "Podany numer NIP jest niepoprawny matematycznie (błędna suma kontrolna).");
                return;
            }
        }

        // Zapis transakcyjny do bazy
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Transakcja

            String userSql = newPass.isEmpty() ? "UPDATE Users SET Email = ? WHERE UserID = ?" 
                                               : "UPDATE Users SET Email = ?, PasswordHash = ? WHERE UserID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(userSql)) {
                pstmt.setString(1, email);
                if (newPass.isEmpty()) {
                    pstmt.setInt(2, userId);
                } else {
                    String hashedNewPass = SecurityUtils.hashPassword(newPass);
                    pstmt.setString(2, hashedNewPass);
                    pstmt.setInt(3, userId);
                    dbPasswordHash = hashedNewPass; // Pamiętaj nowe hasło na przyszłość
                }
                pstmt.executeUpdate();
            }

            // Aktualizacja tabel powiązanych
            if ("Candidate".equals(userRole)) {
                if (selectedCvFile != null) {
                    try {
                        java.io.File cvDir = new java.io.File("cv");
                        if (!cvDir.exists()) {
                            cvDir.mkdirs();
                        }
                        java.io.File destFile = new java.io.File(cvDir, selectedCvFile.getName());
                        java.nio.file.Files.copy(selectedCvFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        
                        currentCvPath = destFile.getAbsolutePath();
                        txtCvPath.setText(destFile.getName());
                        selectedCvFile = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zapisać pliku CV na dysku.");
                        conn.rollback();
                        return;
                    }
                }
                String candSql = "UPDATE Candidates SET FirstName=?, LastName=?, CVFilePath=?, LinkedinURL=?, GithubURL=? WHERE CandidateID=?";
                try (PreparedStatement pstmt = conn.prepareStatement(candSql)) {
                    pstmt.setString(1, txtFirstName.getText() == null ? "" : txtFirstName.getText().trim());
                    pstmt.setString(2, txtLastName.getText() == null ? "" : txtLastName.getText().trim());
                    pstmt.setString(3, currentCvPath);
                    pstmt.setString(4, txtLinkedin.getText() == null ? "" : txtLinkedin.getText().trim());
                    pstmt.setString(5, txtGithub.getText() == null ? "" : txtGithub.getText().trim());
                    pstmt.setInt(6, userId);
                    pstmt.executeUpdate();
                }
            } else if ("Employer".equals(userRole)) {
                String empSql = "UPDATE Employers SET CompanyName=?, Description=?, NIP=? WHERE EmployerID=?";
                try (PreparedStatement pstmt = conn.prepareStatement(empSql)) {
                    pstmt.setString(1, txtCompanyName.getText() == null ? "" : txtCompanyName.getText().trim());
                    pstmt.setString(2, txtDescription.getText() == null ? "" : txtDescription.getText().trim());
                    pstmt.setString(3, txtNip.getText() == null ? "" : txtNip.getText().trim());
                    pstmt.setInt(4, userId);
                    pstmt.executeUpdate();
                }
            }

            conn.commit();
            txtOldPassword.clear();
            txtNewPassword.clear();
            showAlert(Alert.AlertType.INFORMATION, "Sukces", "Twoje dane profilowe zostały poprawnie zapisane.");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd zapisu", "Nie udało się zapisać zmian. Upewnij się, że podany adres e-mail nie jest już zajęty przez kogoś innego.");
        }
    }

    @FXML
    private void handleDeleteAccount(ActionEvent event) {
        // Specyfikacja wymaga weryfikacji hasła przed usunięciem konta
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Trwałe usunięcie konta");
        dialog.setHeaderText("Podaj obecne hasło, aby ostatecznie potwierdzić usunięcie konta.\nUwaga: Ta akcja jest nieodwracalna i skasuje wszystkie Twoje ogłoszenia oraz aplikacje!");
        applyStylesToDialog(dialog); // DODANO
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField pwd = new PasswordField();
        pwd.setPromptText("Twoje hasło...");
        dialog.getDialogPane().setContent(new VBox(10, new Label("Hasło:"), pwd));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) return pwd.getText();
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (!dbPasswordHash.equals(SecurityUtils.hashPassword(result.get()))) {
                showAlert(Alert.AlertType.ERROR, "Odmowa dostępu", "Podano nieprawidłowe hasło. Konto nie zostało usunięte.");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Users WHERE UserID = ?")) {
                
                pstmt.setInt(1, userId);
                pstmt.executeUpdate(); // Dzięki więzom ON DELETE CASCADE baza automatycznie skasuje ogłoszenia/aplikacje
                
                UserSession.clear();
                App.setRoot("login");
                showAlert(Alert.AlertType.INFORMATION, "Konto usunięte", "Twoje konto oraz powiązane z nim dane zniknęły z systemu.");
                
            } catch (SQLException | IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się poprawnie skasować konta z bazy danych.");
            }
        }
    }

    @FXML
    private void handleUploadCv(ActionEvent event) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Wybierz plik CV (PDF)");
        fileChooser.getExtensionFilters().add(
            new javafx.stage.FileChooser.ExtensionFilter("Pliki PDF (*.pdf)", "*.pdf")
        );
        java.io.File file = fileChooser.showOpenDialog(txtCvPath.getScene().getWindow());
        if (file != null) {
            selectedCvFile = file;
            txtCvPath.setText(file.getName());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        if ("Candidate".equals(userRole)) {
            App.setRoot("wyszukiwarka");
        } else if ("Employer".equals(userRole)) {
            App.setRoot("pracodawca");
        } else {
            App.setRoot("login");
        }
    }

    // Obsługa zmiany motywu
    @FXML
    private void handleToggleTheme(ActionEvent event) {
        App.isDarkMode = !App.isDarkMode;
        App.applyTheme(btnToggleTheme.getScene());

        if (App.isDarkMode) {
            btnToggleTheme.setText("☀ Jasny Motyw");
        } else {
            btnToggleTheme.setText("🌙 Ciemny Motyw");
        }
    }

    // Funkcja stylowania okien (taka sama jak w Wyszukiwarce)
    private void applyStylesToDialog(Dialog<?> dialog) {
        try {
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("profil.css").toExternalForm());
            if (App.isDarkMode) {
                dialog.getDialogPane().getStyleClass().add("dark-mode");
            }
            dialog.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
            dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

            try {
                Image logoIcon = new Image(getClass().getResourceAsStream("/org/example/pictures/LogoIcon.png"));
                Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                stage.getIcons().add(logoIcon);
                
                ImageView iconView = new ImageView(logoIcon);
                iconView.setFitWidth(34);
                iconView.setFitHeight(34);
                iconView.setPreserveRatio(true);
                
                javafx.scene.layout.StackPane iconContainer = new javafx.scene.layout.StackPane(iconView);
                iconContainer.setAlignment(javafx.geometry.Pos.CENTER);
                iconContainer.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12px; -fx-padding: 8px;");
                
                DropShadow dropShadow = new DropShadow();
                dropShadow.setColor(Color.rgb(0, 0, 0, 0.25));
                dropShadow.setRadius(12);
                dropShadow.setOffsetY(3);
                iconContainer.setEffect(dropShadow);

                dialog.setGraphic(iconContainer);
            } catch (Exception e) {}
        } catch (Exception e) {}
    }

    // Walidacja poprawności NIP w oparciu o algorytm Modulo 11
    private boolean isValidNIP(String nip) {
        if (nip == null || !nip.matches("\\d{10}")) return false;
        int[] weights = {6, 5, 7, 2, 3, 4, 5, 6, 7};
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(nip.charAt(i)) * weights[i];
        }
        int control = sum % 11;
        return control != 10 && control == Character.getNumericValue(nip.charAt(9));
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        applyStylesToDialog(alert); // DODANO
        alert.showAndWait();
    }
}