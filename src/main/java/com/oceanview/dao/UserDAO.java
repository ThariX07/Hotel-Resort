package com.oceanview.dao;

import com.oceanview.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // This method checks the database for a matching username and password
    public User authenticateUser(String username, String password) {
        User user = null;

        // 1. Write the SQL query (Using ? prevents SQL injection hacking!)
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        // 2. Get our Singleton database connection
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 3. Fill in the question marks in the SQL query
            stmt.setString(1, username);
            stmt.setString(2, password);

            // 4. Execute the query and get the result
            ResultSet rs = stmt.executeQuery();

            // 5. If a row comes back, the user exists! Package them into our User Model.
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
            }

        } catch (SQLException e) {
            System.out.println("Error during authentication: " + e.getMessage());
        }

        // Returns the user object if found, or 'null' if login failed
        return user;
    }
}