package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;

public class RegisterController {

    // Odwołania do elementów z pliku FXML (fx:id)

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

    // Metoda wywoływana po kliknięciu przycisku "Zarejestruj się"
    @FXML
    void handleRegister(ActionEvent event) {
        // Pobieranie danych wpisanych przez użytkownika z usunięciem zbędnych spacji
        String firstName = firstNameField.getText() != null ? firstNameField.getText().trim() : "";
        String lastName = lastNameField.getText() != null ? lastNameField.getText().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText() : "";
        String confirmPassword = confirmPasswordField.getText() != null ? confirmPasswordField.getText() : "";

        boolean isEmployer = businessRadio.isSelected();

        // 1. Walidacja: Sprawdzenie czy wymagane pola nie są puste
        if (firstName.isEmpty() || (!isEmployer && lastName.isEmpty()) || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Wszystkie pola są wymagane!");
            return;
        }

        // 2. Walidacja: Format adresu email (musi zawierać @ i domenę)
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Wprowadzony adres email ma niepoprawny format! Upewnij się, że zawiera znak '@' oraz poprawną domenę (np. nazwa@domena.pl).");
            return;
        }

        // 3. Walidacja: Sprawdzenie czy hasła są identyczne
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Podane hasła nie są identyczne!");
            return;
        }

        // 4. Walidacja: Wymogi siły hasła: min 8 znaków, co najmniej jedna wielka litera, cyfra i znak specjalny
        if (password.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Słabe hasło", "Hasło musi mieć co najmniej 8 znaków!");
            return;
        }

        boolean hasUppercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
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
                "Hasło musi zawierać co najmniej:\n" +
                "- jedną wielką literę,\n" +
                "- jedną cyfrę,\n" +
                "- jeden znak specjalny (np. !, @, #, $, % itp.)."
            );
            return;
        }

        // 5. Walidacja: Sprawdzenie, którą rolę wybrał użytkownik (Kandydat czy Pracodawca)
        RadioButton selectedRole = (RadioButton) roleGroup.getSelectedToggle();
        if (selectedRole == null) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Proszę wybrać rolę (Kandydat lub Pracodawca)!");
            return;
        }
        String role = selectedRole.getText();

        // Jeśli wszystko jest poprawne, logujemy w konsoli i wyświetlamy sukces
        System.out.println("--- Nowa Rejestracja ---");
        if (isEmployer) {
            System.out.println("Nazwa firmy: " + firstName);
        } else {
            System.out.println("Imię: " + firstName);
            System.out.println("Nazwisko: " + lastName);
        }
        System.out.println("Email: " + email);
        System.out.println("Rola: " + role);
        System.out.println("------------------------");

        showAlert(Alert.AlertType.INFORMATION, "Rejestracja pomyślna", 
            "Konto dla roli: " + role + " zostało pomyślnie utworzone!\n" +
            "Możesz się teraz zalogować."
        );

        // Po pomyślnej rejestracji automatycznie przełączamy na ekran logowania
        try {
            App.setRoot("login");
        } catch (IOException e) {
            System.err.println("Błąd podczas przełączania ekranu: " + e.getMessage());
        }
    }

    // Metoda przełączająca użytkownika na ekran logowania
    @FXML
    void switchToLogin(ActionEvent event) throws IOException {

        // Wywołanie metody setRoot z klasy App
        App.setRoot("login");
    }
}