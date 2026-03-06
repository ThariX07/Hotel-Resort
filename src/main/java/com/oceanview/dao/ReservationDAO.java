package com.oceanview.dao;

import com.oceanview.model.Guest;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import java.sql.*;
import java.time.temporal.ChronoUnit;

public class ReservationDAO {

    public boolean createReservation(Reservation reservation, int userId) {
        String guestSql = "INSERT INTO guests (name, address, contact_number, email) VALUES (?, ?, ?, ?)";
        String reservationSql = "INSERT INTO reservations (reservation_number, guest_id, room_id, check_in_date, check_out_date, total_cost, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String roomSql = "SELECT price_per_night FROM rooms WHERE room_id = ?";

        Connection conn = null;
        PreparedStatement guestStmt = null;
        PreparedStatement resStmt = null;
        PreparedStatement roomStmt = null;
        ResultSet generatedKeys = null;
        ResultSet roomRs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            guestStmt = conn.prepareStatement(guestSql, Statement.RETURN_GENERATED_KEYS);
            guestStmt.setString(1, reservation.getGuest().getName());
            guestStmt.setString(2, reservation.getGuest().getAddress());
            guestStmt.setString(3, reservation.getGuest().getContactNumber());
            guestStmt.setString(4, reservation.getGuest().getEmail());
            guestStmt.executeUpdate();

            generatedKeys = guestStmt.getGeneratedKeys();
            int guestId = 0;
            if (generatedKeys.next()) {
                guestId = generatedKeys.getInt(1);
                reservation.getGuest().setGuestId(guestId);
            } else {
                throw new SQLException("Creating guest failed, no ID obtained.");
            }

            roomStmt = conn.prepareStatement(roomSql);
            roomStmt.setInt(1, reservation.getRoom().getRoomId());
            roomRs = roomStmt.executeQuery();

            double pricePerNight = 0.0;
            if (roomRs.next()) {
                pricePerNight = roomRs.getDouble("price_per_night");
            } else {
                throw new SQLException("Room not found.");
            }

            long nights = java.time.temporal.ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
            if (nights <= 0) nights = 1;

            double totalCost = nights * pricePerNight;
            reservation.setTotalCost(totalCost);

            resStmt = conn.prepareStatement(reservationSql);
            resStmt.setString(1, reservation.getReservationNumber());
            resStmt.setInt(2, guestId);
            resStmt.setInt(3, reservation.getRoom().getRoomId());
            resStmt.setDate(4, Date.valueOf(reservation.getCheckInDate()));
            resStmt.setDate(5, Date.valueOf(reservation.getCheckOutDate()));
            resStmt.setDouble(6, totalCost);
            resStmt.setInt(7, userId);
            resStmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (roomRs != null) roomRs.close();
                if (guestStmt != null) guestStmt.close();
                if (roomStmt != null) roomStmt.close();
                if (resStmt != null) resStmt.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public com.oceanview.dto.BillDTO getReservationDetails(String reservationNumber) {
        String sql = "SELECT r.reservation_number, g.name, rm.room_number, rm.room_type, " +
                "r.check_in_date, r.check_out_date, rm.price_per_night, r.total_cost " +
                "FROM reservations r " +
                "JOIN guests g ON r.guest_id = g.guest_id " +
                "JOIN rooms rm ON r.room_id = rm.room_id " +
                "WHERE r.reservation_number = ?";

        com.oceanview.dto.BillDTO bill = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reservationNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                java.time.LocalDate checkIn = rs.getDate("check_in_date").toLocalDate();
                java.time.LocalDate checkOut = rs.getDate("check_out_date").toLocalDate();
                long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
                if (nights <= 0) nights = 1;

                bill = new com.oceanview.dto.BillDTO.BillBuilder()
                        .setReservationNumber(rs.getString("reservation_number"))
                        .setGuestName(rs.getString("name"))
                        .setRoomNumber(rs.getString("room_number"))
                        .setRoomType(rs.getString("room_type"))
                        .setCheckInDate(checkIn)
                        .setCheckOutDate(checkOut)
                        .setNumberOfNights(nights)
                        .setPricePerNight(rs.getDouble("price_per_night"))
                        .setTotalCost(rs.getDouble("total_cost"))
                        .build();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bill;
    }

    public java.util.List<com.oceanview.dto.BillDTO> getAllReservations() {
        java.util.List<com.oceanview.dto.BillDTO> allReservations = new java.util.ArrayList<>();

        String sql = "SELECT r.reservation_number, g.name, rm.room_number, rm.room_type, " +
                "r.check_in_date, r.check_out_date, rm.price_per_night, r.total_cost " +
                "FROM reservations r " +
                "JOIN guests g ON r.guest_id = g.guest_id " +
                "JOIN rooms rm ON r.room_id = rm.room_id " +
                "ORDER BY r.check_in_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                java.time.LocalDate checkIn = rs.getDate("check_in_date").toLocalDate();
                java.time.LocalDate checkOut = rs.getDate("check_out_date").toLocalDate();
                long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
                if (nights <= 0) nights = 1;

                com.oceanview.dto.BillDTO dto = new com.oceanview.dto.BillDTO.BillBuilder()
                        .setReservationNumber(rs.getString("reservation_number"))
                        .setGuestName(rs.getString("name"))
                        .setRoomNumber(rs.getString("room_number"))
                        .setRoomType(rs.getString("room_type"))
                        .setCheckInDate(checkIn)
                        .setCheckOutDate(checkOut)
                        .setNumberOfNights(nights)
                        .setPricePerNight(rs.getDouble("price_per_night"))
                        .setTotalCost(rs.getDouble("total_cost"))
                        .build();

                allReservations.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allReservations;
    }

    public java.util.List<java.util.Map<String, Object>> getRevenueReportData() {
        java.util.List<java.util.Map<String, Object>> reportData = new java.util.ArrayList<>();

        String sql = "SELECT r.room_type, COUNT(res.reservation_number) as total_bookings, SUM(res.total_cost) as total_revenue " +
                "FROM rooms r " +
                "LEFT JOIN reservations res ON r.room_id = res.room_id " +
                "GROUP BY r.room_type";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                java.util.Map<String, Object> row = new java.util.HashMap<>();
                row.put("roomType", rs.getString("room_type"));
                row.put("totalBookings", rs.getInt("total_bookings"));
                double revenue = rs.getObject("total_revenue") != null ? rs.getDouble("total_revenue") : 0.0;
                row.put("totalRevenue", revenue);

                reportData.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportData;
    }
}