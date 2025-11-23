import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrafficSystemTest {

    @Test
    void testAdd() {
        TrafficSystem c = new TrafficSystem();
        assertEquals(5, c.add());
    }
}
