package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection = null;

    private static final String URL = "jdbc:mysql://localhost:3306/oceanview_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // XAMPP has no password by default

    private DBConnection() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established successfully!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
        return connection;
    }
}