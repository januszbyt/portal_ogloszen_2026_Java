module org.example {
    requires javafx.controls;           
    requires javafx.fxml;                
    
    requires transitive javafx.graphics; // Do obsługi głównego okna aplikacji 
    requires transitive javafx.base;     // Do obsługi akcji i zdarzeń 
    
    requires transitive java.sql;
	requires java.desktop;        // Do połączenia z bazą danych  (SQL)
	requires java.prefs;
    opens org.example to javafx.fxml;    
    exports org.example;                
}