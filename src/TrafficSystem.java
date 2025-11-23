import java.util.List;

// TrafficSystem contains the logic for managing traffic light phases and
// simple lane capacity heuristics. It is intentionally lightweight — the
// update logic computes simple priorities and rotates phases based on queues.
public class TrafficSystem {

    private static final int PHASE_DURATION_FRAMES = 100;

    private static final int VEHICLE_LENGTH = 45;
    private static final int SAFETY_GAP = 50;
    private static final int LANE_LENGTH_NORTH = 270; // from y=-30 to y=240
    private static final int LANE_LENGTH_SOUTH = 280; // from y=700 to y=420
    private static final int LANE_LENGTH_EAST = 330;  // from x=800 to x=470
    private static final int LANE_LENGTH_WEST = 330;  // from x=-30 to x=300

    private static final int CAPACITY_NORTH = LANE_LENGTH_NORTH / (VEHICLE_LENGTH + SAFETY_GAP);
    private static final int CAPACITY_SOUTH = LANE_LENGTH_SOUTH / (VEHICLE_LENGTH + SAFETY_GAP);
    private static final int CAPACITY_EAST = LANE_LENGTH_EAST / (VEHICLE_LENGTH + SAFETY_GAP);
    private static final int CAPACITY_WEST = LANE_LENGTH_WEST / (VEHICLE_LENGTH + SAFETY_GAP);

    // Return true when no vehicle is currently inside the intersection box.
    // Used to ensure phase changes do not send conflicting traffic through
    // the intersection at the same time.
    private static boolean intersectionIsClear(List<Vehicle> vehicles) {

        int intersectionLeft = 301;
        int intersectionRight = 469;
        
        int intersectionTop = 241;
        int intersectionBottom = 419;

        for (Vehicle vehicle : vehicles) {
            boolean isInIntersection = vehicle.x >= intersectionLeft
                    && vehicle.x <= intersectionRight
                    && vehicle.y >= intersectionTop
                    && vehicle.y <= intersectionBottom;

            if (isInIntersection) {
                return false;
            }
        }

        return true;

    }

    // TrafficHub stores the current phase state (which direction is green),
    // timers, and pending phase requests waiting for the intersection to clear.
    public static class TrafficHub {

        public boolean northOn;
        public boolean southOn;
        public boolean eastOn;
        public boolean westOn;
        
        public MovementDirection phase;
        public int timer;
        public int phaseDuration;
        public MovementDirection pendingPhase;
        public int pendingTimer;

        // Initialize hub with a default phase and timers.
        public TrafficHub() {
            this.northOn = false;
            this.southOn = false;
            this.eastOn = true;
            this.westOn = false;
            this.phase = MovementDirection.East;
            this.timer = 0;
            this.phaseDuration = PHASE_DURATION_FRAMES;
            this.pendingPhase = null;
            this.pendingTimer = 0;
        }
        
    }

    // Set `vehicle.moving` depending on whether the hub currently allows
    // vehicles of that direction to pass at their lane boundary. This is a
    // simple gate (on/off) used by the vehicle update loop.
    public static void checkLights(Vehicle vehicle, TrafficHub hub) {

        if ((!hub.southOn && vehicle.dir == MovementDirection.South && vehicle.y == 420)
                || (!hub.northOn && vehicle.dir == MovementDirection.North && vehicle.y == 240)
                || (!hub.westOn && vehicle.dir == MovementDirection.East && vehicle.x == 300)
                || (!hub.eastOn && vehicle.dir == MovementDirection.West && vehicle.x == 470)) {
            vehicle.moving = false;
        } else {
            vehicle.moving = true;
        }

    }

    // Update traffic light phase based on simple queue length heuristics and
    // lane capacity. This method may delay phase changes until the
    // intersection is clear to avoid collisions.
    public static void updateLights(TrafficHub hub, List<Vehicle> vehicles) {
        // Count queued vehicles waiting in each incoming lane (not yet turned).
        // These counts drive the heuristics used to decide phase priorities.
        int qNorth = (int) vehicles.stream()
            .filter(v -> v.startDir == MovementDirection.North && !v.turned && v.y <= 240 && v.y >= -30)
            .count();
        int qSouth = (int) vehicles.stream()
            .filter(v -> v.startDir == MovementDirection.South && !v.turned && v.y >= 420 && v.y <= 700)
            .count();
        int qFromWest = (int) vehicles.stream()
            .filter(v -> v.startDir == MovementDirection.East && !v.turned && v.x >= -30 && v.x <= 300)
            .count();
        int qFromEast = (int) vehicles.stream()
            .filter(v -> v.startDir == MovementDirection.West && !v.turned && v.x >= 470 && v.x <= 800)
            .count();

        int capNorth = CAPACITY_NORTH;
        int capSouth = CAPACITY_SOUTH;
        int capFromWest = CAPACITY_WEST;
        int capFromEast = CAPACITY_EAST;

        // Normalize queue sizes by lane capacity to get a congestion ratio
        // (0..1+). This helps balance long vs short lanes fairly.
        double pNorth = capNorth > 0 ? (double) qNorth / capNorth : 0;
        double pSouth = capSouth > 0 ? (double) qSouth / capSouth : 0;
        double pFromWest = capFromWest > 0 ? (double) qFromWest / capFromWest : 0;
        double pFromEast = capFromEast > 0 ? (double) qFromEast / capFromEast : 0;

        double priorityForPhaseNorth = pNorth;
        double priorityForPhaseSouth = pSouth;
        double priorityForPhaseWest = pFromWest;
        double priorityForPhaseEast = pFromEast;

        double currentPriority = 0;

        switch (hub.phase) {
            case North:
                currentPriority = priorityForPhaseNorth;
                break;
            case South:
                currentPriority = priorityForPhaseSouth;
                break;
            case West:
                currentPriority = priorityForPhaseWest;
                break;
            case East:
                currentPriority = priorityForPhaseEast;
                break;
        }

        // Base timings and an upper bound for adaptive extensions.
        int base = PHASE_DURATION_FRAMES;
        int maxExtra = 400;
        boolean atCapacity = false;

        switch (hub.phase) {
            case North:
                atCapacity = qNorth >= capNorth && capNorth > 0;
                break;
            case South:
                atCapacity = qSouth >= capSouth && capSouth > 0;
                break;
            case West:
                atCapacity = qFromWest >= capFromWest && capFromWest > 0;
                break;
            case East:
                atCapacity = qFromEast >= capFromEast && capFromEast > 0;
                break;
        }

        if (atCapacity) {
            // If the active lane is beyond capacity, reset the timer and
            // give it an extended green up to a capped maximum to drain queue.
            hub.timer = 0;
            hub.phaseDuration = Math.min(base + maxExtra,
                    base + 50 * (int) Math.max(1, Math.max(Math.max(qNorth - capNorth, qSouth - capSouth), Math.max(qFromWest - capFromWest, qFromEast - capFromEast))));
        } else {
            // Otherwise set an adaptive duration proportional to the
            // congestion priority and advance the internal timer.
            hub.phaseDuration = base + (int) (currentPriority * 200);
            hub.timer += 1;
        }

        if (hub.timer >= hub.phaseDuration) {

            hub.timer = 0;
            double highest = 0;
            MovementDirection newPhase = hub.phase;
            if (priorityForPhaseNorth > highest) {
                highest = priorityForPhaseNorth;
                newPhase = MovementDirection.North;
            }
            if (priorityForPhaseSouth > highest) {
                highest = priorityForPhaseSouth;
                newPhase = MovementDirection.South;
            }
            if (priorityForPhaseEast > highest) {
                highest = priorityForPhaseEast;
                newPhase = MovementDirection.East;
            }
            if (priorityForPhaseWest > highest) {
                highest = priorityForPhaseWest;
                newPhase = MovementDirection.West;
            }

            // If there is no demand (highest==0), rotate phases in a fixed
            // order to keep traffic flowing evenly rather than stalling.
            if (highest == 0) {
                switch (hub.phase) {
                    case East:
                        newPhase = MovementDirection.North;
                        break;
                    case North:
                        newPhase = MovementDirection.West;
                        break;
                    case West:
                        newPhase = MovementDirection.South;
                        break;
                    case South:
                        newPhase = MovementDirection.East;
                        break;
                }
            }

            // Attempt to switch to the chosen phase. If the intersection is
            // occupied, schedule it as pending until it clears to avoid
            // allowing conflicting movements.
            if (newPhase != hub.phase) {
                if (intersectionIsClear(vehicles)) {
                    hub.phase = newPhase;
                    hub.pendingPhase = null;
                } else {
                    hub.pendingPhase = newPhase;
                }
            }
            
        }

        // If a phase is pending, wait for the intersection to become clear
        // or for a small timeout before forcing the change to avoid deadlock.
        if (hub.pendingPhase != null) {
            hub.pendingTimer += 1;
            if (intersectionIsClear(vehicles) || hub.pendingTimer > 60) {
                hub.phase = hub.pendingPhase;
                hub.pendingPhase = null;
                hub.pendingTimer = 0;
            }
        } else {
            hub.pendingTimer = 0;
        }

        // Set the explicit boolean flags for which directions currently
        // have a green light. This simplifies checks elsewhere.
        hub.northOn = false;
        hub.southOn = false;
        hub.eastOn = false;
        hub.westOn = false;

        switch (hub.phase) {
            case North:
                hub.northOn = true;
                break;
            case South:
                hub.southOn = true;
                break;
            case West:
                hub.westOn = true;
                break;
            case East:
                hub.eastOn = true;
                break;
        }
    }
}