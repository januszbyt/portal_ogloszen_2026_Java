package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.image.Image; 
import javafx.scene.image.ImageView; 
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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


    @FXML
    public void zmienMotyw(ActionEvent event) {
        App.isDarkMode = !App.isDarkMode;
        App.applyTheme(btnZmianaMotywu.getScene());

        if (App.isDarkMode) {
            btnZmianaMotywu.setText("☀ Jasny Motyw"); 
        } else {
            btnZmianaMotywu.setText("🌙 Ciemny Motyw");
        }
    }

    @FXML
    public void initialize() {
        if (btnZmianaMotywu != null) {
            if (App.isDarkMode) {
                btnZmianaMotywu.setText("☀ Jasny Motyw");
            } else {
                btnZmianaMotywu.setText("🌙 Ciemny Motyw");
            }
        }

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

    // DODANA Metoda dodajaca style CSS do okien dialogowych
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

    // ZAKTUALIZOWANA Metoda pomocnicza do wyświetlania okienek (Dodano applyStylesToDialog)
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        applyStylesToDialog(alert); 
        
        alert.showAndWait();
    }

    @FXML
    void handleRegister(ActionEvent event) {
        String firstName = firstNameField.getText() != null ? firstNameField.getText().trim() : "";
        String lastName = lastNameField.getText() != null ? lastNameField.getText().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().trim() : "";                     
        String password = passwordField.getText() != null ? passwordField.getText() : "";
        String confirmPassword = confirmPasswordField.getText() != null ? confirmPasswordField.getText() : "";

        boolean isEmployer = businessRadio.isSelected();

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
        
        String roleValue = isEmployer ? "Employer" : "Candidate";

        String insertUserSql = "INSERT INTO Users (Email, PasswordHash, Role, IsBlocked) VALUES (?, ?, ?, 0)";
        String insertCandidateSql = "INSERT INTO Candidates (CandidateID, FirstName, LastName) VALUES (?, ?, ?)";
        String insertEmployerSql = "INSERT INTO Employers (EmployerID, CompanyName) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 

            try (PreparedStatement userStmt = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, email);
                userStmt.setString(2, SecurityUtils.hashPassword(password));
                userStmt.setString(3, roleValue);
                
                int affectedRows = userStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Tworzenie użytkownika nie powiodło się, brak zmodyfikowanych wierszy.");
                }

                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedUserId = generatedKeys.getInt(1);

                        if (isEmployer) {
                            try (PreparedStatement empStmt = conn.prepareStatement(insertEmployerSql)) {
                                empStmt.setInt(1, generatedUserId); 
                                empStmt.setString(2, firstName);    
                                empStmt.executeUpdate();
                            }
                        } else {
                            try (PreparedStatement candStmt = conn.prepareStatement(insertCandidateSql)) {
                                candStmt.setInt(1, generatedUserId); 
                                candStmt.setString(2, firstName);
                                candStmt.setString(3, lastName);
                                candStmt.executeUpdate();
                            }
                        }
                    } else {
                        throw new SQLException("Tworzenie użytkownika nie powiodło się, nie uzyskano ID.");
                    }
                }
                
                conn.commit(); 
                
                showAlert(Alert.AlertType.INFORMATION, "Rejestracja pomyślna", 
                    "Konto dla roli: " + selectedRole.getText() + " zostało pomyślnie utworzone!\nMożesz się teraz zalogować."
                );

                App.setRoot("login");

            } catch (SQLException e) {
                conn.rollback(); 
                e.printStackTrace();
                
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