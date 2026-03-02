package com.oceanview.controller;

import com.google.gson.Gson;
import com.oceanview.dao.UserDAO;
import com.oceanview.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> jsonResponse = new HashMap<>();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Username and password are required.");
            out.print(gson.toJson(jsonResponse));
            out.flush();
            return;
        }

        User newUser = new User();
        newUser.setUsername(username.trim());
        newUser.setPassword(password);

        boolean isRegistered = userDAO.registerUser(newUser);

        if (isRegistered) {
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Account created successfully! You can now log in.");
        } else {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Username already exists. Please choose another.");
        }

        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
}