package com.oceanview.controller;

import com.google.gson.Gson;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dto.BillDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/bill")
public class BillServlet extends HttpServlet {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> jsonResponse = new HashMap<>();

        String reservationNumber = request.getParameter("reservationNumber");

        if (reservationNumber == null || reservationNumber.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Reservation number is required.");
            out.print(gson.toJson(jsonResponse));
            out.flush();
            return;
        }

        BillDTO bill = reservationDAO.getReservationDetails(reservationNumber);

        if (bill != null) {
            jsonResponse.put("status", "success");
            jsonResponse.put("bill", bill);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Reservation not found.");
        }

        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
}