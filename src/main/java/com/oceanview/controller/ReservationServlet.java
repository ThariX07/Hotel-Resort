package com.oceanview.controller;

import com.google.gson.Gson;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.model.Guest;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import com.oceanview.dao.EmailUtility;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet("/reservation")
public class ReservationServlet extends HttpServlet {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> jsonResponse = new HashMap<>();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Session expired. Please log in again.");
            out.print(gson.toJson(jsonResponse));
            out.flush();
            return;
        }

        int adminUserId = (int) session.getAttribute("userId");

        try {
            String guestName = request.getParameter("guestName");
            String address = request.getParameter("address");
            String contactNumber = request.getParameter("contactNumber");
            String guestEmail = request.getParameter("guestEmail");

            int roomId = Integer.parseInt(request.getParameter("roomId"));
            LocalDate checkInDate = LocalDate.parse(request.getParameter("checkInDate"));
            LocalDate checkOutDate = LocalDate.parse(request.getParameter("checkOutDate"));

            Guest guest = new Guest();
            guest.setName(guestName);
            guest.setAddress(address);
            guest.setContactNumber(contactNumber);
            guest.setEmail(guestEmail);

            Room room = new Room();
            room.setRoomId(roomId);

            Reservation reservation = new Reservation();
            String generatedResNumber = "RES-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            reservation.setReservationNumber(generatedResNumber);
            reservation.setGuest(guest);
            reservation.setRoom(room);
            reservation.setCheckInDate(checkInDate);
            reservation.setCheckOutDate(checkOutDate);

            boolean success = reservationDAO.createReservation(reservation, adminUserId);

            if (success) {
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Reservation created successfully!");
                jsonResponse.put("reservationNumber", generatedResNumber);

                double totalCost = reservation.getTotalCost();

                java.util.concurrent.CompletableFuture.runAsync(() -> {
                    EmailUtility.sendBookingConfirmation(guestEmail, generatedResNumber, guestName, totalCost);
                });

            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Failed to create reservation in database.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Invalid form data.");
        }

        out.print(gson.toJson(jsonResponse));
        out.flush();
    }
}