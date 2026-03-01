package com.oceanview.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // 1. Create a private static instance of the class itself.
    // This is the ONLY instance that will ever exist.
    private static Connection connection = null;

    // Database credentials (XAMPP defaults)
    private static final String URL = "jdbc:mysql://localhost:3306/oceanview_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // XAMPP has no password by default

    // 2. Make the constructor PRIVATE.
    // This stops other classes from saying "new DBConnection()" and making multiple connections.
    private DBConnection() {
    }

    // 3. Provide a public static method to get the single connection.
    public static Connection getConnection() {
        try {
            // If the connection doesn't exist or is closed, create it.
            if (connection == null || connection.isClosed()) {
                // Load the MySQL driver we added in the pom.xml
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established successfully!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
        // Return the single, shared connection
        return connection;
    }
}