package dao;

import com.oceanview.model.Guest;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class ReservationDAOTest {

    private ReservationDAO reservationDAO;

    @BeforeEach
    public void setUp() {
        reservationDAO = new ReservationDAO();
    }

    @Test
    public void testCreateReservation_HappyPath_ReturnsTrue() {
        Guest guest = new Guest();
        guest.setName("Test User");
        guest.setAddress("123 Test Street");
        guest.setContactNumber("0771234567");

        Room room = new Room();
        room.setRoomId(1);

        Reservation reservation = new Reservation();
        reservation.setReservationNumber("RES-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setCheckInDate(LocalDate.now());
        reservation.setCheckOutDate(LocalDate.now().plusDays(3));

        boolean result = reservationDAO.createReservation(reservation);

        assertTrue(result);
        assertTrue(reservation.getTotalCost() > 0);
        assertTrue(reservation.getGuest().getGuestId() > 0);
    }

    @Test
    public void testCreateReservation_UnhappyPath_InvalidRoom_ReturnsFalse() {
        Guest guest = new Guest();
        guest.setName("Fail User");
        guest.setAddress("404 Error Ave");
        guest.setContactNumber("0000000000");

        Room room = new Room();
        room.setRoomId(999);

        Reservation reservation = new Reservation();
        reservation.setReservationNumber("RES-FAIL1");
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setCheckInDate(LocalDate.now());
        reservation.setCheckOutDate(LocalDate.now().plusDays(1));

        boolean result = reservationDAO.createReservation(reservation);

        assertFalse(result);
    }
}