package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Zwiększamy domyślny rozmiar początkowy na 800x600
        scene = new Scene(loadFXML("login"), 800, 600);
        stage.setScene(scene);
        stage.setTitle("System Ofert Pracy");
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        
        // Ta linijka sprawia, że okno otwiera się na cały ekran (maksymalizacja)
        stage.setMaximized(true); 
        
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}