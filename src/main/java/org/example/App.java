package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image; // DODANO: Import klasy Image
import javafx.stage.Stage;

import java.io.IOException;
import java.util.prefs.Preferences; // DODANO: Import do trwałego zapisywania ustawień

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    
    // Inicjalizacja pamięci ustawień dla naszej aplikacji
    private static final Preferences prefs = Preferences.userNodeForPackage(App.class);
    
    // Teraz na starcie pobieramy zapisany stan (domyślnie false, jeśli to pierwsze uruchomienie)
    public static boolean isDarkMode = prefs.getBoolean("isDarkMode", false);

    @Override
    public void start(Stage stage) throws IOException {
        // Zwiększamy domyślny rozmiar początkowy na 800x600
        scene = new Scene(loadFXML("login"), 800, 600);
        
        // Aplikowanie motywu przy starcie
        applyTheme(scene);

        stage.setScene(scene);
        stage.setTitle("System Ofert Pracy");
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        
         // Dodanie LogoIcon do naszego programu
        try {
            // Ścieżka wskazuje na folder resources/org/example/pictures/
            Image applicationIcon = new Image(getClass().getResourceAsStream("/org/example/pictures/LogoIcon.png"));
            stage.getIcons().add(applicationIcon);
        } catch (NullPointerException e) {
            System.err.println("Błąd: Nie znaleziono pliku LogoIcon.png w podanej ścieżce.");
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas ładowania ikony: " + e.getMessage());
        }
        

        // Ta linijka sprawia, że okno otwiera się na cały ekran (maksymalizacja)
        stage.setMaximized(true); 
        
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        // Automatyczne aplikowanie motywu po każdej zmianie widoku
        applyTheme(scene);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    // Metoda do aplikacji CSS do korzenia (root) sceny
    public static void applyTheme(Scene currentScene) {
        // Zapisywanie wybranego motywu do trwałej pamięci systemowej przy każdej jego zmianie
        prefs.putBoolean("isDarkMode", isDarkMode);

        if (currentScene == null || currentScene.getRoot() == null) return;

        if (isDarkMode) {
            if (!currentScene.getRoot().getStyleClass().contains("dark-mode")) {
                currentScene.getRoot().getStyleClass().add("dark-mode");
            }
        } else {
            currentScene.getRoot().getStyleClass().remove("dark-mode");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}