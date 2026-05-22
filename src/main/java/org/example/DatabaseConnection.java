package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    //  Adres serwera uczelnianego i nazwa bazy TESTBJ
    private static final String URL = "jdbc:mysql://s41925.eduweb.pwste.edu.pl:3306/TESTBJ"; 
    
    //  Dane do logowania bazy danych
    private static final String USER = "projektWarsztaty"; 
    private static final String PASSWORD = "Baza!@ProjekT26";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}