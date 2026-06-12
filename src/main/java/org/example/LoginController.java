package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image; 
import javafx.scene.image.ImageView; 

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

// Dodane importy do bazy danych i szyfrowania
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;

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

    // DODANO: Inicjalizacja przy wejściu na widok logowania
    @FXML
    public void initialize() {
        // Synchronizacja przycisku i logo z globalnym stanem motywu przy ładowaniu
        if (App.isDarkMode) {
            btnZmianaMotywu.setText("☀ Jasny Motyw"); 
            try {
                logoImageView.setImage(new Image(getClass().getResourceAsStream("/org/example/pictures/LogoWhite.png")));
            } catch (Exception e) { /* ignoruj błąd braku obrazka */ }
        } else {
            btnZmianaMotywu.setText("🌙 Ciemny Motyw");
            try {
                logoImageView.setImage(new Image(getClass().getResourceAsStream("/org/example/pictures/Logo.png")));
            } catch (Exception e) { /* ignoruj błąd braku obrazka */ }
        }
    }

    // DODANO/ZMIENIONO: Obsługa globalnej flagi w App.java
    @FXML
    public void zmienMotyw(ActionEvent event) {
        // Przełączenie globalnej flagi na przeciwną
        App.isDarkMode = !App.isDarkMode;
        
        // Aplikowanie motywu na całą scenę
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

    // Metoda dodajaca style CSS
    private void applyStylesToDialog(Dialog<?> dialog) {
        try {
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
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

    // Metoda do szyfrowania hasła (identyczna jak przy rejestracji)


    @FXML
    private void handleLogin(ActionEvent event) {
        // Zawsze czyścimy poprzednią sesję przed nowym logowaniem
        UserSession.clear();

        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText() : "";
        
        // Walidacja: Puste pola
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Proszę wprowadzić email oraz hasło!");
            return;
        }

        // Walidacja: Format email
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Wprowadzony adres email ma niepoprawny format!");
            return;
        }

        // Weryfikacja danych logowania w bazie danych
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
                    // Sprawdzenie czy konto nie jest zablokowane
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
                    
                    // Inicjalizacja sesji użytkownika (przekazujemy imię/nazwę firmy)
                    UserSession.init(userId, email, role, name);
                    
                    // Przekierowanie zależne od roli użytkownika
                    if ("Employer".equals(role)) {
                        App.setRoot("pracodawca"); // Upewnij się, że plik to pracodawca.fxml
                    } else if ("Candidate".equals(role)) {
                        App.setRoot("wyszukiwarka"); // Przekierowanie kandydata z serwera
                    } else if ("Admin".equals(role)) {
                        App.setRoot("AdminPanel"); // Twoje przekierowanie dla admina
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
        
        //  Podanie e-maila 
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

            // Sprawdzenie czy ten email w ogóle istnieje w bazie
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
        
        // Generowanie 6-cyfrowego kodu weryfikacyjnego
        Random random = new Random();
        int verificationCode = 100000 + random.nextInt(900000); 
        
        // Symulacja wysłania wiadomości
        showAlert(Alert.AlertType.INFORMATION, "Symulacja wysyłki e-maila",
            "Wysłano wiadomość e-mail na adres: " + email + "\n\n" +
            "Twój jednorazowy kod weryfikacyjny to: " + verificationCode + "\n\n" +
            "Zapisz ten kod i wprowadź go w kolejnym kroku.");
            
        //  Wprowadzanie kodu weryfikacyjnego 
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
        
        //  Wprowadzanie nowego hasła 
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
        
        //  Zapis nowego hasła do bazy danych 
        String updateSql = "UPDATE Users SET PasswordHash = ? WHERE Email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            
            // Szyfrujemy nowe hasło przed zapisem
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