package com.oceanview.dao;

import com.oceanview.model.Guest;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import java.sql.*;
import java.time.temporal.ChronoUnit;

public class ReservationDAO {

    public boolean createReservation(Reservation reservation) {
        String guestSql = "INSERT INTO guests (name, address, contact_number) VALUES (?, ?, ?)";
        String reservationSql = "INSERT INTO reservations (reservation_number, guest_id, room_id, check_in_date, check_out_date, total_cost) VALUES (?, ?, ?, ?, ?, ?)";
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

            long nights = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
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
}