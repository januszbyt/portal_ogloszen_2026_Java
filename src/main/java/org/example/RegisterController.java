package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RegisterController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private RadioButton userRadio;

    @FXML
    private RadioButton businessRadio;

    @FXML
    private ToggleGroup roleGroup;

    @FXML
    private Button registerButton;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private Button btnZmianaMotywu;

    private boolean isDarkMode = false;

    @FXML
    public void zmienMotyw(ActionEvent event) {
        Scene scene = btnZmianaMotywu.getScene();
        Pane root = (Pane) scene.getRoot();

        if (isDarkMode) {
            root.getStyleClass().remove("dark-mode");
            btnZmianaMotywu.setText("🌙 Ciemny Motyw");
            isDarkMode = false;
        } else {
            root.getStyleClass().add("dark-mode");
            btnZmianaMotywu.setText("☀ Jasny Motyw"); 
            isDarkMode = true;
        }
    }

    @FXML
    public void initialize() {
        // Słuchacz zmiany roli w celu dynamicznego dostosowania formularza
        roleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == businessRadio) {
                lastNameField.setVisible(false);
                lastNameField.setManaged(false);
                firstNameField.setPromptText("Nazwa firmy");
                lastNameField.clear();
            } else {
                lastNameField.setVisible(true);
                lastNameField.setManaged(true);
                firstNameField.setPromptText("Imię");
            }
        });
    }
 // Metoda pomocnicza do wyświetlania okienek z komunikatami
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Metoda do szyfrowania hasła 

    
// Metoda wywoływana po kliknięciu przycisku "Zarejestruj się"
    @FXML
    void handleRegister(ActionEvent event) {
        // Pobieranie danych wpisanych przez użytkownika z usunięciem zbędnych spacji
        String firstName = firstNameField.getText() != null ? firstNameField.getText().trim() : "";
        String lastName = lastNameField.getText() != null ? lastNameField.getText().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().trim() : "";                     
        String password = passwordField.getText() != null ? passwordField.getText() : "";
        String confirmPassword = confirmPasswordField.getText() != null ? confirmPasswordField.getText() : "";

        // Sprawdzanie, który radioButton jest zaznaczony
        boolean isEmployer = businessRadio.isSelected();

        // 1. Walidacje
        if (firstName.isEmpty() || (!isEmployer && lastName.isEmpty()) || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Wszystkie pola są wymagane!");
            return;
        }

        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Wprowadzony adres email ma niepoprawny format!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Podane hasła nie są identyczne!");
            return;
        }

        if (password.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Słabe hasło", "Hasło musi mieć co najmniej 8 znaków!");
            return;
        }

        boolean hasUppercase = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        if (!hasUppercase || !hasDigit || !hasSpecial) {
            showAlert(Alert.AlertType.WARNING, "Słabe hasło", 
                "Hasło musi zawierać co najmniej:\n- jedną wielką literę,\n- jedną cyfrę,\n- jeden znak specjalny."
            );
            return;
        }

        RadioButton selectedRole = (RadioButton) roleGroup.getSelectedToggle();
        if (selectedRole == null) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Proszę wybrać rolę!");
            return;
        }
        
        // Ustalenie wartości roli do bazy. Pracownik czy Pracodawca
        String roleValue = isEmployer ? "Employer" : "Candidate";

        // Zapytania SQL
        String insertUserSql = "INSERT INTO Users (Email, PasswordHash, Role, IsBlocked) VALUES (?, ?, ?, 0)";
        String insertCandidateSql = "INSERT INTO Candidates (CandidateID, FirstName, LastName) VALUES (?, ?, ?)";
        String insertEmployerSql = "INSERT INTO Employers (EmployerID, CompanyName) VALUES (?, ?)";

        // 2. Połączenie z bazą i transakcja
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Wyłączamy AutoCommit, aby zapewnić integralność danych (transakcja)
            conn.setAutoCommit(false); 

            // Wstawiamy użytkownika i pobieramy wygenerowany klucz 
            try (PreparedStatement userStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, email);
                userStmt.setString(2, SecurityUtils.hashPassword(password));
                userStmt.setString(3, roleValue);
                
                int affectedRows = userStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Tworzenie użytkownika nie powiodło się, brak zmodyfikowanych wierszy.");
                }

                // Pobieramy nadany UserID
                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedUserId = generatedKeys.getInt(1);

                        // Wstawiamy dane do tabeli podrzędnej na podstawie roli
                        if (isEmployer) {
                            try (PreparedStatement empStmt = conn.prepareStatement(insertEmployerSql)) {
                                empStmt.setInt(1, generatedUserId); // Klucz obcy
                                empStmt.setString(2, firstName);    // Pole firstName przechowuje nazwę firmy
                                empStmt.executeUpdate();
                            }
                        } else {
                            try (PreparedStatement candStmt = conn.prepareStatement(insertCandidateSql)) {
                                candStmt.setInt(1, generatedUserId); // Klucz obcy
                                candStmt.setString(2, firstName);
                                candStmt.setString(3, lastName);
                                candStmt.executeUpdate();
                            }
                        }
                    } else {
                        throw new SQLException("Tworzenie użytkownika nie powiodło się, nie uzyskano ID.");
                    }
                }
                
                // Tutaj zatwierdzenie transakcji, jeśli wszystko przebiegło pomyślnie
                conn.commit(); 
                
                showAlert(Alert.AlertType.INFORMATION, "Rejestracja pomyślna", 
                    "Konto dla roli: " + selectedRole.getText() + " zostało pomyślnie utworzone!\nMożesz się teraz zalogować."
                );

                // Przełączenie na logowanie
                App.setRoot("login");

            } catch (SQLException e) {
                // Wycofanie zmian, jeśli wystąpi jakiś błąd
                conn.rollback(); 
                e.printStackTrace();
                
                // Sprawdzenie czy email już nie istnieje w bazie
                if(e.getErrorCode() == 1062) {
                    showAlert(Alert.AlertType.ERROR, "Błąd rejestracji", "Użytkownik o podanym adresie email już istnieje!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Błąd bazy danych", "Nie udało się zarejestrować użytkownika.");
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
    void switchToLogin(ActionEvent event) throws IOException {
        App.setRoot("login");
    }
}

//naprawa