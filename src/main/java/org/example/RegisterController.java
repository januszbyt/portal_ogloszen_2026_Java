package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;

public class RegisterController {

    // Odwołania do elementów z pliku FXML (fx:id)

    @FXML
    private TextField usernameField;

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

    // Metoda wywoływana po kliknięciu przycisku "Zarejestruj się"
    @FXML
    void handleRegister(ActionEvent event) {

        // Pobieranie danych wpisanych przez użytkownika
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Sprawdzenie, którą rolę wybrał użytkownik
        String role = userRadio.isSelected() ? "Użytkownik" : "Przedsiębiorca";

        // Sprawdzenie czy wszystkie pola są wypełnione
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("Błąd: Wypełnij wszystkie pola!");
            return;
        }

        // Sprawdzenie czy hasła są identyczne
        if (!password.equals(confirmPassword)) {
            System.out.println("Błąd: Hasła nie są identyczne!");
            return;
        }

        // Jeśli wszystko jest poprawne, wyświetlamy dane w konsoli
        // W przyszłości tutaj będzie zapis do bazy danych
        System.out.println("--- Nowa Rejestracja ---");
        System.out.println("Login: " + username);
        System.out.println("Email: " + email);
        System.out.println("Rola: " + role);
        System.out.println("------------------------");
    }

    // Metoda przełączająca użytkownika na ekran logowania
    @FXML
    void switchToLogin(ActionEvent event) throws IOException {

        // Wywołanie metody setRoot z klasy App
        App.setRoot("login");
    }
}