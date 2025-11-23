import java.util.List;

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

    static boolean intersectionIsClear(List<Vehicle> vehicles) {
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
    public int add() {
        return 2 + 3;
    }

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

    public static void updateLights(TrafficHub hub, List<Vehicle> vehicles) {
        int qNorth = (int) vehicles.stream().filter(v -> v.startDir == MovementDirection.North && !v.turned && v.y <= 240 && v.y >= -30).count();
        int qSouth = (int) vehicles.stream().filter(v -> v.startDir == MovementDirection.South && !v.turned && v.y >= 420 && v.y <= 700).count();
        int qFromWest = (int) vehicles.stream().filter(v -> v.startDir == MovementDirection.East && !v.turned && v.x >= -30 && v.x <= 300).count();
        int qFromEast = (int) vehicles.stream().filter(v -> v.startDir == MovementDirection.West && !v.turned && v.x >= 470 && v.x <= 800).count();

        int capNorth = CAPACITY_NORTH;
        int capSouth = CAPACITY_SOUTH;
        int capFromWest = CAPACITY_WEST;
        int capFromEast = CAPACITY_EAST;

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
            hub.timer = 0;
            hub.phaseDuration = Math.min(base + maxExtra, base + 50 * (int) Math.max(1, Math.max(Math.max(qNorth - capNorth, qSouth - capSouth), Math.max(qFromWest - capFromWest, qFromEast - capFromEast))));
        } else {
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

            if (newPhase != hub.phase) {
                if (intersectionIsClear(vehicles)) {
                    hub.phase = newPhase;
                    hub.pendingPhase = null;
                } else {
                    hub.pendingPhase = newPhase;
                }
            }
        }

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