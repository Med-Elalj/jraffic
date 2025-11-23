import java.util.List;

public class TrafficSystem {
    private static final int PHASE_DURATION_FRAMES = 180;
    private static final int STARVATION_THRESHOLD_FRAMES = 180;

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

    public static class TrafficHub {
        public boolean northOn;
        public boolean southOn;
        public boolean eastOn;
        public boolean westOn;
        public MovementDirection phase;
        public int timer;
        public int phaseDuration;
        public int starveNorth;
        public int starveSouth;
        public int starveEast;
        public int starveWest;
        public MovementDirection pendingPhase;

        public TrafficHub() {
            this.northOn = false;
            this.southOn = false;
            this.eastOn = true;
            this.westOn = false;
            this.phase = MovementDirection.East;
            this.timer = 0;
            this.phaseDuration = PHASE_DURATION_FRAMES;
            this.starveNorth = 0;
            this.starveSouth = 0;
            this.starveEast = 0;
            this.starveWest = 0;
            this.pendingPhase = null;
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
        hub.timer += 1;

        boolean hasEast = vehicles.stream().anyMatch(v -> v.startDir == MovementDirection.East && !v.turned);
        boolean hasNorth = vehicles.stream().anyMatch(v -> v.startDir == MovementDirection.North && !v.turned);
        boolean hasWest = vehicles.stream().anyMatch(v -> v.startDir == MovementDirection.West && !v.turned);
        boolean hasSouth = vehicles.stream().anyMatch(v -> v.startDir == MovementDirection.South && !v.turned);

        Object[][] directions = {
            {MovementDirection.East, hasEast},
            {MovementDirection.North, hasNorth},
            {MovementDirection.West, hasWest},
            {MovementDirection.South, hasSouth}
        };

        for (Object[] dirInfo : directions) {
            MovementDirection dir = (MovementDirection) dirInfo[0];
            boolean hasCarsInLane = (boolean) dirInfo[1];
            if (dir == hub.phase) {
                switch (dir) {
                    case East:
                        hub.starveEast = 0;
                        break;
                    case North:
                        hub.starveNorth = 0;
                        break;
                    case West:
                        hub.starveWest = 0;
                        break;
                    case South:
                        hub.starveSouth = 0;
                        break;
                }
            } else if (hasCarsInLane) {
                switch (dir) {
                    case East:
                        hub.starveEast += 1;
                        break;
                    case North:
                        hub.starveNorth += 1;
                        break;
                    case West:
                        hub.starveWest += 1;
                        break;
                    case South:
                        hub.starveSouth += 1;
                        break;
                }
            }
        }

        if (hub.timer >= hub.phaseDuration) {
            hub.timer = 0;
            MovementDirection newPhase = determineNextPhase(hub, directions);

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
            if (intersectionIsClear(vehicles)) {
                hub.phase = hub.pendingPhase;
                hub.pendingPhase = null;
            }
        }

        hub.northOn = false;
        hub.southOn = false;
        hub.eastOn = false;
        hub.westOn = false;

        if (hub.pendingPhase == null) {
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

    private static MovementDirection determineNextPhase(TrafficHub hub, Object[][] directions) {
        int maxStarve = Math.max(Math.max(hub.starveNorth, hub.starveSouth), Math.max(hub.starveEast, hub.starveWest));

        if (maxStarve > STARVATION_THRESHOLD_FRAMES) {
            if (hub.starveNorth == maxStarve && hasCars(MovementDirection.North, directions)) {
                return MovementDirection.North;
            } else if (hub.starveSouth == maxStarve && hasCars(MovementDirection.South, directions)) {
                return MovementDirection.South;
            } else if (hub.starveEast == maxStarve && hasCars(MovementDirection.East, directions)) {
                return MovementDirection.East;
            } else if (hub.starveWest == maxStarve && hasCars(MovementDirection.West, directions)) {
                return MovementDirection.West;
            }
        }

        MovementDirection[] nextPhases;
        switch (hub.phase) {
            case East:
                nextPhases = new MovementDirection[]{MovementDirection.North, MovementDirection.West, MovementDirection.South, MovementDirection.East};
                break;
            case North:
                nextPhases = new MovementDirection[]{MovementDirection.West, MovementDirection.South, MovementDirection.East, MovementDirection.North};
                break;
            case West:
                nextPhases = new MovementDirection[]{MovementDirection.South, MovementDirection.East, MovementDirection.North, MovementDirection.West};
                break;
            case South:
                nextPhases = new MovementDirection[]{MovementDirection.East, MovementDirection.North, MovementDirection.West, MovementDirection.South};
                break;
            default:
                nextPhases = new MovementDirection[]{};
        }

        for (MovementDirection phase : nextPhases) {
            if (hasCars(phase, directions)) {
                return phase;
            }
        }

        return hub.phase;
    }

    private static boolean hasCars(MovementDirection dir, Object[][] directions) {
        for (Object[] direction : directions) {
            if ((MovementDirection) direction[0] == dir) {
                return (boolean) direction[1];
            }
        }
        return false;
    }
}