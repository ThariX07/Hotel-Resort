package com.oceanview.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// 1. This annotation tells the server: "If someone visits /test, run this code!"
@WebServlet("/test")
public class TestServlet extends HttpServlet {

    // 2. The doGet method handles standard web requests (like opening a page)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 3. We are telling the browser we are sending back plain text
        response.setContentType("text/plain");

        // 4. This is how we write data back to the screen
        PrintWriter out = response.getWriter();
        out.println("Hello Ocean View Resort! The Java EE backend is ALIVE!");
    }
}