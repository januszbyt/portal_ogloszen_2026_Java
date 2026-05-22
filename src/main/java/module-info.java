module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql; //  Wymagane do połączenia z bazą danych

    opens org.example to javafx.fxml;
    exports org.example;
}