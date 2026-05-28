package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import java.io.IOException;

public class WyszukiwarkaController {

    @FXML
    private HBox panelFiltrow;

    @FXML
    private Label lblWelcome;

    // Ta metoda odpala się AUTOMATYCZNIE przy starcie aplikacji
    @FXML
    public void initialize() {
        // Powiąż właściwość 'managed' z 'visible' - dzięki temu, gdy panel jest niewidoczny,
        // kontener (VBox) traktuje go, jakby w ogóle go nie było (znika padding, marginesy i spacing)
        panelFiltrow.managedProperty().bind(panelFiltrow.visibleProperty());

        // Schowaj panel i ustaw jego wysokość na 0, zanim użytkownik cokolwiek zobaczy
        panelFiltrow.setVisible(false);
        panelFiltrow.setPrefHeight(0);
        panelFiltrow.setMinHeight(0);

        // Ustawienie przywitania zalogowanego użytkownika z sesji
        UserSession session = UserSession.getInstance();
        if (session != null && session.getFirstName() != null) {
            lblWelcome.setText("Witaj, " + session.getFirstName() + "!");
        }
    }

    // Twoja metoda podpięta pod przycisk "Filtry"
    @FXML
    private void obslugaPrzyciskuFiltry() {
        boolean pokazywac = !panelFiltrow.isVisible(); 
        
        panelFiltrow.setVisible(pokazywac);

        if (pokazywac) {
            // Pokazuje filtry i przywraca rozmiar ze Scene Buildera
            panelFiltrow.setPrefHeight(HBox.USE_COMPUTED_SIZE); 
            panelFiltrow.setMinHeight(HBox.USE_COMPUTED_SIZE);
        } else {
            // Chowa filtry i zwija przestrzeń do zera
            panelFiltrow.setPrefHeight(0);
            panelFiltrow.setMinHeight(0);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        // Czyszczenie sesji użytkownika
        UserSession.clear();
        
        // Przekierowanie do widoku logowania
        App.setRoot("login");
    }
}
