import com.oceanview.dao.ReservationDAO;
import org.junit.jupiter.api.Test;

public class ReportTest {

    @Test
    public void testGenerateRevenueReport() {
        System.out.println("Executing Management Report Test...");

        ReservationDAO dao = new ReservationDAO();

        dao.generateRevenueReport();

        System.out.println("Report Test Completed.");
    }
}