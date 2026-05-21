package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private RadioButton userRadio;

    @FXML
    private RadioButton businessRadio;

    @FXML
    private ToggleGroup roleGroup;

    @FXML
    private Hyperlink registerLink;

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        RadioButton selectedRole = (RadioButton) roleGroup.getSelectedToggle();
        
        if (selectedRole != null) {
            String role = selectedRole.getText();
            System.out.println("Próba logowania:");
            System.out.println("Email: " + email);
            System.out.println("Hasło: " + (password.isEmpty() ? "brak" : "****"));
            System.out.println("Rola: " + role);
        }

        // Tutaj dodasz logikę sprawdzania bazy danych
    }

    
    // --- ZMIENIONA METODA PONIŻEJ ---
    @FXML
    private void handleRegister(ActionEvent event) throws IOException {
        System.out.println("Przejście do ekranu rejestracji...");
        // Wywołujemy metodę setRoot z klasy App, podając nazwę pliku FXML (bez rozszerzenia .fxml)
        App.setRoot("register"); 
    }

    @FXML
    private void handleSkip(ActionEvent event) throws IOException {
        System.out.println("Kontynuacja bez logowania...");
        
        // Tutaj w przyszłości możesz dodać przejście do głównego okna aplikacji, np. App.setRoot("main_view");
    }
}