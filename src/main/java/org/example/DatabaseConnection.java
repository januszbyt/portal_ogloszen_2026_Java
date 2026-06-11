package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    //  Adres serwera uczelnianego i nazwa bazy TESTBJ
    private static final String URL = "jdbc:mysql://localhost:3306/TESTBJ?useUnicode=true&characterEncoding=utf8"; 
    
    //  Dane do logowania bazy danych
    private static final String USER = "root"; 
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

//naprawa