package com.bank.personalfinance.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class DatabaseCon {
    private static final String URL = "jdbc:mysql://localhost:3306/banka_db";
    private static final String USER = "root";
    private static final String PASSWORD = "arda4545"; //HERKES BURADAKİ ŞİFREYİ KLONLADIKTAN SONRA KENDİ MYSQL ŞİFRESİ YAPSIN

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
