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

// 1. This URL is where the frontend will send the login data
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final Gson gson = new Gson(); // Our JSON converter from the pom.xml

    // 2. We use doPost because login data (passwords) should be hidden, not in the URL
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 3. Set the response type to JSON so the frontend can read it easily
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> jsonResponse = new HashMap<>();

        // 4. Grab the credentials sent by the frontend
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 5. Ask our DAO if this user exists in the database
        User user = userDAO.authenticateUser(username, password);

        if (user != null) {
            // SUCCESS! Create a server session to remember this user
            HttpSession session = request.getSession();
            session.setAttribute("loggedInUser", user.getUsername());
            session.setMaxInactiveInterval(30 * 60); // Session expires after 30 minutes of inactivity

            // Prepare a success message in JSON
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Login successful");
            jsonResponse.put("username", user.getUsername());
        } else {
            // FAILURE!
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized status code
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Invalid username or password");
        }

        // 6. Convert our response map into actual JSON and send it to the frontend
        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
}