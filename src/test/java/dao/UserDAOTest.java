package dao;

import com.oceanview.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    public void testAuthenticateUser_ValidCredentials_ReturnsUser() {
        String validUsername = "admin";
        String validPassword = "password123";

        User result = userDAO.authenticateUser(validUsername, validPassword);

        assertNotNull(result);
        assertEquals(validUsername, result.getUsername());
    }

    @Test
    public void testAuthenticateUser_InvalidCredentials_ReturnsNull() {
        String invalidUsername = "fakeuser";
        String invalidPassword = "wrongpassword";

        User result = userDAO.authenticateUser(invalidUsername, invalidPassword);

        assertNull(result);
    }
}