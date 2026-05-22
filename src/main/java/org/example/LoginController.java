package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Hyperlink registerLink;

    // DODANA METODA: Wstrzykiwanie style.css do okienek
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
        
        // Podpięcie CSS do alertów
        applyStylesToDialog(alert); 
        
        alert.showAndWait();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText() : "";
        
        // 1. Walidacja: Puste pola
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Proszę wprowadzić email oraz hasło!");
            return;
        }

        // 2. Walidacja: Format email
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Wprowadzony adres email ma niepoprawny format!");
            return;
        }

        System.out.println("Próba logowania:");
        System.out.println("Email: " + email);
        System.out.println("Hasło: ****");

        // Tutaj dodasz logikę sprawdzania bazy danych w przyszłości
        showAlert(Alert.AlertType.INFORMATION, "Logowanie", "Pomyślna walidacja danych logowania.\n(Integracja z bazą danych w kolejnym kroku)");
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

    @FXML
    private void handleForgotPassword(ActionEvent event) {
        String email = "";
        
        // 1. Podanie e-maila w pętli
        while (true) {
            // Przekazujemy 'email' do konstruktora, aby po błędzie tekst nie znikał
            TextInputDialog emailDialog = new TextInputDialog(email); 
            emailDialog.setTitle("Odzyskiwanie hasła");
            emailDialog.setHeaderText("Krok 1: Wprowadź swój adres e-mail");
            emailDialog.setContentText("Email:");
            
            // Podpięcie CSS do okna wpisywania e-maila
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
            
            // Walidacja formatu e-maila
            String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
            if (!email.matches(emailRegex)) {
                showAlert(Alert.AlertType.WARNING, "Błąd walidacji", "Wprowadzony adres email ma niepoprawny format!");
                continue; 
            }
            
            break; // Email jest poprawny - wychodzimy z pętli i idziemy dalej
        }
        
        // 2. Generowanie 6-cyfrowego kodu weryfikacyjnego
        Random random = new Random();
        int verificationCode = 100000 + random.nextInt(900000); // 100000 do 999999
        
        // 3. Symulacja wysłania wiadomości
        showAlert(Alert.AlertType.INFORMATION, "Symulacja wysyłki e-maila",
            "Wysłano wiadomość e-mail na adres: " + email + "\n\n" +
            "Twój jednorazowy kod weryfikacyjny to: " + verificationCode + "\n\n" +
            "Zapisz ten kod i wprowadź go w kolejnym kroku.");
            
        // 4. Wprowadzanie kodu weryfikacyjnego 
        while (true) {
            TextInputDialog codeDialog = new TextInputDialog();
            codeDialog.setTitle("Odzyskiwanie hasła");
            codeDialog.setHeaderText("Krok 2: Wprowadź kod weryfikacyjny");
            codeDialog.setContentText("Kod (6 cyfr):");
            
            // Podpięcie CSS do okna kodu
            applyStylesToDialog(codeDialog);
            
            Optional<String> codeResult = codeDialog.showAndWait();
            if (!codeResult.isPresent()) {
                return; // Użytkownik kliknął Anuluj
            }
            
            String enteredCode = codeResult.get().trim();
            if (!enteredCode.equals(String.valueOf(verificationCode))) {
                showAlert(Alert.AlertType.ERROR, "Błąd weryfikacji", "Wprowadzony kod jest niepoprawny! Spróbuj ponownie.");
                continue; // Wraca na początek pętli kodu
            }
            
            break; // Kod poprawny
        }
        
        // 5. Wprowadzanie nowego hasła 
        String newPassword = "";
        while (true) {
            TextInputDialog passwordDialog = new TextInputDialog(newPassword);
            passwordDialog.setTitle("Odzyskiwanie hasła");
            passwordDialog.setHeaderText("Krok 3: Wprowadź nowe hasło");
            passwordDialog.setContentText("Nowe hasło:");
            
            // Podpięcie CSS do okna nowego hasła
            applyStylesToDialog(passwordDialog);
            
            Optional<String> passwordResult = passwordDialog.showAndWait();
            if (!passwordResult.isPresent()) {
                return; // Użytkownik kliknął Anuluj
            }
            
            newPassword = passwordResult.get();
            
            // Walidacja nowego hasła
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
                    "Hasło musi zawierać co najmniej:\n" +
                    "- jedną wielką literę,\n" +
                    "- jedną cyfrę,\n" +
                    "- jeden znak specjalny (np. !, @, #, $, % itp.)."
                );
                continue; 
            }
            
            break; 
        }
        
        // 6. Sukces i symulacja zapisu
        System.out.println("--- Zmiana hasła (Odzyskiwanie) ---");
        System.out.println("Email: " + email);
        System.out.println("Status: Hasło zostało pomyślnie zmienione");
        System.out.println("----------------------------------");
        
        showAlert(Alert.AlertType.INFORMATION, "Sukces", "Twoje hasło zostało pomyślnie zmienione! Możesz się teraz zalogować.");
    }
}