import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TrafficSystemTest {

    private TrafficSystem.TrafficHub hub;
    private List<Vehicle> vehicles;

    @BeforeEach
    void setUp() {
        hub = new TrafficSystem.TrafficHub();
        vehicles = new ArrayList<>();
    }

    @Test
    void testIntersectionIsClear_NoVehicles() {
        assertTrue(TrafficSystem.intersectionIsClear(vehicles));
    }

    @Test
    void testIntersectionIsClear_WithVehicleInIntersection() {
        Vehicle vehicle = Vehicle.spawn(350, 300, MovementDirection.North, Vehicle.randColorName());
        vehicles.add(vehicle);
        assertFalse(TrafficSystem.intersectionIsClear(vehicles));
    }

    @Test
    void testIntersectionIsClear_WithVehicleOutsideIntersection() {
        Vehicle vehicle = Vehicle.spawn(100, 100, MovementDirection.North, Vehicle.randColorName());
        vehicles.add(vehicle);
        assertTrue(TrafficSystem.intersectionIsClear(vehicles));
    }

    @Test
    void testCheckLights_VehicleStoppedAtRedLight() {
        Vehicle vehicle = Vehicle.spawn(300, 420, MovementDirection.South, Vehicle.randColorName());
        vehicle.dir = MovementDirection.South;
        hub.southOn = false;
        TrafficSystem.checkLights(vehicle, hub);
        assertFalse(vehicle.moving);
    }

    @Test
    void testCheckLights_VehicleMovingAtGreenLight() {
        Vehicle vehicle = Vehicle.spawn(300, 420, MovementDirection.South, Vehicle.randColorName());
        vehicle.dir = MovementDirection.South;
        hub.southOn = true;
        TrafficSystem.checkLights(vehicle, hub);
        assertTrue(vehicle.moving);
    }

    @Test
    void testUpdateLights_ChangePhaseWhenTimerExceeds() {
        hub.phase = MovementDirection.North;
        hub.timer = 100;
        TrafficSystem.updateLights(hub, vehicles);
        assertNotEquals(MovementDirection.North, hub.phase);
    }

    @Test
    void testUpdateLights_PendingPhaseClearedWhenIntersectionIsClear() {
        hub.phase = MovementDirection.North;
        hub.pendingPhase = MovementDirection.South;
        TrafficSystem.updateLights(hub, vehicles);
        assertEquals(MovementDirection.South, hub.phase);
        assertNull(hub.pendingPhase);
    }
}
