package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image; 
import javafx.scene.image.ImageView; 
import javafx.scene.layout.Region;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private ImageView logoImageView; // Dodane pole dla logo

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Hyperlink registerLink;

    @FXML
    private Button btnZmianaMotywu;

    @FXML
    public void initialize() {
        if (App.isDarkMode) {
            btnZmianaMotywu.setText("☀ Jasny Motyw"); 
            try {
                logoImageView.setImage(new Image(getClass().getResourceAsStream("/org/example/pictures/LogoWhite.png")));
            } catch (Exception e) { /* ignoruj błąd braku obrazka  */ }
        } else {
            btnZmianaMotywu.setText("🌙 Ciemny Motyw");
            try {
                logoImageView.setImage(new Image(getClass().getResourceAsStream("/org/example/pictures/Logo.png")));
            } catch (Exception e) { /* ignoruj błąd braku obrazka */ }
        }
    }

    @FXML
    public void zmienMotyw(ActionEvent event) {
        App.isDarkMode = !App.isDarkMode;
        App.applyTheme(btnZmianaMotywu.getScene());

        if (App.isDarkMode) {
            btnZmianaMotywu.setText("☀ Jasny Motyw"); 
            try {
                logoImageView.setImage(new Image(getClass().getResourceAsStream("/org/example/pictures/LogoWhite.png")));
            } catch (Exception e) {}
        } else {
            btnZmianaMotywu.setText("🌙 Ciemny Motyw");
            try {
                logoImageView.setImage(new Image(getClass().getResourceAsStream("/org/example/pictures/Logo.png")));
            } catch (Exception e) {}
        }
    }

    // ZAKTUALIZOWANA Metoda dodajaca style CSS do okien dialogowych
    private void applyStylesToDialog(Dialog<?> dialog) {
        try {
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("login_register.css").toExternalForm());
            
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
    private void handleLogin(ActionEvent event) {
        UserSession.clear();

        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText() : "";
        
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Proszę wprowadzić email oraz hasło!");
            return;
        }

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Wprowadzony adres email ma niepoprawny format!");
            return;
        }

        String hashedPassword = SecurityUtils.hashPassword(password);
        String sql = "SELECT u.UserID, u.Role, u.IsBlocked, c.FirstName, e.CompanyName " +
                     "FROM Users u " +
                     "LEFT JOIN Candidates c ON u.UserID = c.CandidateID " +
                     "LEFT JOIN Employers e ON u.UserID = e.EmployerID " +
                     "WHERE u.Email = ? AND u.PasswordHash = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, hashedPassword);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { 
                    boolean isBlocked = rs.getBoolean("IsBlocked");
                    if (isBlocked) {
                        showAlert(Alert.AlertType.ERROR, "Konto zablokowane", "Twoje konto zostało zablokowane. Skontaktuj się z administratorem.");
                        return;
                    }
                    
                    int userId = rs.getInt("UserID");
                    String role = rs.getString("Role");
                    String name;
                    if ("Employer".equals(role)) {
                        name = rs.getString("CompanyName");
                    } else if ("Admin".equals(role)) {
                        name = "Administrator";
                    } else {
                        name = rs.getString("FirstName");
                    }
                    System.out.println("Zalogowano pomyślnie. Rola: " + role + ", Nazwa: " + name);
                    
                    UserSession.init(userId, email, role, name);
                    
                    if ("Employer".equals(role)) {
                        App.setRoot("pracodawca"); 
                    } else if ("Candidate".equals(role)) {
                        App.setRoot("wyszukiwarka"); 
                    } else if ("Admin".equals(role)) {
                        App.setRoot("AdminPanel"); 
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Błąd logowania", "Nieznana rola użytkownika w bazie danych.");
                    }
                    
                } else {
                    showAlert(Alert.AlertType.ERROR, "Błąd logowania", "Nieprawidłowy adres e-mail lub hasło.");
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd połączenia", "Wystąpił problem z połączeniem z bazą danych.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Błąd podczas przełączania ekranu: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) throws IOException {
        System.out.println("Przejście do ekranu rejestracji...");
        App.setRoot("register"); 
    }

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        String email = "";
        
        while (true) {
            TextInputDialog emailDialog = new TextInputDialog(email); 
            emailDialog.setTitle("Odzyskiwanie hasła");
            emailDialog.setHeaderText("Krok 1: Wprowadź swój adres e-mail");
            emailDialog.setContentText("Email:");
            
            applyStylesToDialog(emailDialog);
            
            Optional<String> emailResult = emailDialog.showAndWait();
            if (!emailResult.isPresent()) {
                return; 
            }
            
            email = emailResult.get().trim();
            if (email.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Adres e-mail nie może być pusty!");
                continue; 
            }
            
            String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
            if (!email.matches(emailRegex)) {
                showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Wprowadzony adres email ma niepoprawny format!");
                continue; 
            }

            boolean emailExists = false;
            String checkEmailSql = "SELECT 1 FROM Users WHERE Email = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(checkEmailSql)) {
                
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        emailExists = true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Błąd połączenia", "Problem z weryfikacją adresu e-mail w bazie.");
                return;
            }

            if (!emailExists) {
                showAlert(Alert.AlertType.ERROR, "Nie znaleziono konta", "Konto z podanym adresem e-mail nie istnieje.");
                continue; 
            }
            
            break; 
        }
        
        Random random = new Random();
        int verificationCode = 100000 + random.nextInt(900000); 
        
        showAlert(Alert.AlertType.INFORMATION, "Symulacja wysyłki e-maila",
            "Wysłano wiadomość e-mail na adres: " + email + "\n\n" +
            "Twój jednorazowy kod weryfikacyjny to: " + verificationCode + "\n\n" +
            "Zapisz ten kod i wprowadź go w kolejnym kroku.");
            
        while (true) {
            TextInputDialog codeDialog = new TextInputDialog();
            codeDialog.setTitle("Odzyskiwanie hasła");
            codeDialog.setHeaderText("Krok 2: Wprowadź kod weryfikacyjny");
            codeDialog.setContentText("Kod (6 cyfr):");
            
            applyStylesToDialog(codeDialog);
            
            Optional<String> codeResult = codeDialog.showAndWait();
            if (!codeResult.isPresent()) {
                return; 
            }
            
            String enteredCode = codeResult.get().trim();
            if (!enteredCode.equals(String.valueOf(verificationCode))) {
                showAlert(Alert.AlertType.ERROR, "Błąd weryfikacji", "Wprowadzony kod jest niepoprawny! Spróbuj ponownie.");
                continue; 
            }
            
            break; 
        }
        
        String newPassword = "";
        while (true) {
            TextInputDialog passwordDialog = new TextInputDialog(newPassword);
            passwordDialog.setTitle("Odzyskiwanie hasła");
            passwordDialog.setHeaderText("Krok 3: Wprowadź nowe hasło");
            passwordDialog.setContentText("Nowe hasło:");
            
            applyStylesToDialog(passwordDialog);
            
            Optional<String> passwordResult = passwordDialog.showAndWait();
            if (!passwordResult.isPresent()) {
                return; 
            }
            
            newPassword = passwordResult.get();
            
            if (newPassword.length() < 8) {
                showAlert(Alert.AlertType.WARNING, "Słabe hasło", "Hasło musi mieć co najmniej 8 znaków!");
                continue;
            }
            
            boolean hasUppercase = false;
            boolean hasDigit = false;
            boolean hasSpecial = false;
            
            for (char c : newPassword.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    hasUppercase = true;
                } else if (Character.isDigit(c)) {
                    hasDigit = true;
                } else if (!Character.isLetterOrDigit(c)) {
                    hasSpecial = true;
                }
            }
            
            if (!hasUppercase || !hasDigit || !hasSpecial) {
                showAlert(Alert.AlertType.WARNING, "Słabe hasło", 
                    "Hasło musi zawierać co najmniej:\n- jedną wielką literę,\n- jedną cyfrę,\n- jeden znak specjalny."
                );
                continue; 
            }
            
            break; 
        }
        
        String updateSql = "UPDATE Users SET PasswordHash = ? WHERE Email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            
            pstmt.setString(1, SecurityUtils.hashPassword(newPassword));
            pstmt.setString(2, email);
            
            int rowsUpdated = pstmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("--- Zmiana hasła (Odzyskiwanie) ---");
                System.out.println("Email: " + email);
                System.out.println("Status: Hasło zostało pomyślnie zmienione w bazie danych.");
                
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Twoje hasło zostało pomyślnie zmienione! Możesz się teraz zalogować.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zaktualizować hasła w bazie danych.");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Wystąpił problem podczas próby zmiany hasła.");
        }
    }
}