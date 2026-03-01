package com.oceanview.controller;

import com.google.gson.Gson;
import com.oceanview.dao.UserDAO;
import com.oceanview.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {

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

        User user = userDAO.authenticateUser(username, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loggedInUser", user.getUsername());
            session.setMaxInactiveInterval(30 * 60); // Session expires after 30 minutes of inactivity

            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Login successful");
            jsonResponse.put("username", user.getUsername());
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Invalid username or password");
        }

        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
}