package com.oceanview.controller;
import com.google.gson.Gson;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.model.Guest;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

            try {
                String name = request.getParameter("name");
                String address = request.getParameter("address");
                String contact = request.getParameter("contactNumber");
                int roomId = Integer.parseInt(request.getParameter("roomId"));
                LocalDate checkIn = LocalDate.parse(request.getParameter("checkInDate"));
                LocalDate checkOut = LocalDate.parse(request.getParameter("checkOutDate"));

                Guest guest = new Guest();
                guest.setName(name);
                guest.setAddress(address);
                guest.setContactNumber(contact);

                Room room = new Room();
                room.setRoomId(roomId);

                Reservation reservation = new Reservation();
                String resNumber = "RES-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
                reservation.setReservationNumber(resNumber);
                reservation.setGuest(guest);
                reservation.setRoom(room);
                reservation.setCheckInDate(checkIn);
                reservation.setCheckOutDate(checkOut);

                boolean isCreated = reservationDAO.createReservation(reservation);

                if (isCreated) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "Reservation created successfully.");
                    jsonResponse.put("reservationNumber", resNumber);
                    jsonResponse.put("totalCost", reservation.getTotalCost());
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Failed to create reservation.");
                }

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Invalid input data: " + e.getMessage());
            }

            out.print(gson.toJson(jsonResponse));
            out.flush();
        }
    }

