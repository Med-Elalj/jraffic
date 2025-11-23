import java.awt.Color;

// Represents a vehicle in the simulation including position, direction
// and simple movement/turning logic. Methods are kept small and focused.
public class Vehicle {

    public int x;
    public int y;
    public MovementDirection dir;
    public MovementDirection startDir;
    
    public Color color;
    public String colorName;
    public boolean turned;
    public boolean moving;

    private static final Color BROWN = new Color(160, 32, 240);

    public Vehicle(int x, int y, MovementDirection dir, Color color, String colorName) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.startDir = dir;
        this.color = color;
        this.colorName = colorName;
        this.turned = false;
        this.moving = false;
    }

    // Factory method to create a new Vehicle from a color name and start
    // position. Centralizes mapping from string to Color.
    public static Vehicle spawn(int x, int y, MovementDirection dir, String colorName) {
        // Map the simple color name to an actual Color object. Centralizing
        // this logic makes it easy to add new colors later.
        Color color;
        switch (colorName) {
            case "Blue":
                color = Color.BLUE;
                break;
            case "Yellow":
                color = Color.YELLOW;
                break;
            case "Brown":
                color = BROWN;
                break;
            default:
                // Fallback to blue for unknown names to avoid null colors.
                color = Color.BLUE;
        }
        return new Vehicle(x, y, dir, color, colorName);
    }

    // Move the vehicle one simulation step along its current direction.
    // The step size is intentionally small for smooth animation.
    public void step() {
        // Move one small increment in the current direction. The signs are
        // chosen to match the coordinate system used in the UI.
        switch (this.dir) {
            case North:
                this.y += 2; // moving down the screen
                break;
            case South:
                this.y -= 2; // moving up the screen
                break;
            case West:
                this.x -= 2; // moving left on the screen
                break;
            case East:
                this.x += 2; // moving right on the screen
                break;
        }
    }

    // Check and perform a turn when the vehicle reaches a turning point.
    // Uses color as a simple routing decision in this demo simulation.
    public void turnCheck() {

        // Check for arrival at predefined turning points. This demo uses
        // vehicle color as a simple routing signal: Yellow & Brown decide
        // which way to turn when they reach a specific tile.
        if (this.dir == MovementDirection.North) {
            // Vehicles coming from the north (moving downwards)
            if (this.x == 360 && this.y == 310 && this.color.equals(BROWN)) {
                this.dir = MovementDirection.West;
                this.turned = true;
            } else if (this.x == 360 && this.y == 360 && this.color.equals(Color.YELLOW)) {
                this.dir = MovementDirection.East;
                this.turned = true;
            }
        } else if (this.dir == MovementDirection.South) {
            // Vehicles coming from the south (moving upwards)
            if (this.y == 310 && this.x == 410 && this.color.equals(Color.YELLOW)) {
                this.dir = MovementDirection.West;
                this.turned = true;
            } else if (this.y == 360 && this.x == 410 && this.color.equals(BROWN)) {
                this.dir = MovementDirection.East;
                this.turned = true;
            }
        } else if (this.dir == MovementDirection.East) {
            // Vehicles coming from the east (moving rightwards)
            if (this.x == 360 && this.y == 360 && this.color.equals(BROWN)) {
                this.dir = MovementDirection.North;
                this.turned = true;
            } else if (this.x == 410 && this.y == 360 && this.color.equals(Color.YELLOW)) {
                this.dir = MovementDirection.South;
                this.turned = true;
            }
        } else if (this.dir == MovementDirection.West) {
            // Vehicles coming from the west (moving leftwards)
            if (this.x == 360 && this.y == 310 && this.color.equals(Color.YELLOW)) {
                this.dir = MovementDirection.North;
                this.turned = true;
            } else if (this.x == 410 && this.y == 310 && this.color.equals(BROWN)) {
                this.dir = MovementDirection.South;
                this.turned = true;
            }
        }

    }

    // Return a random color name used when spawning vehicles.
    public static String randColorName() {

        int rand = (int) (Math.random() * 3);
        switch (rand) {
            case 0:
                return "Blue";
            case 1:
                return "Yellow";
            default:
                return "Brown";
        }

    }

    // Return true when another vehicle is within a safe distance in the
    // same lane/direction, preventing this vehicle from moving forward.
    public boolean blocked(java.util.List<Vehicle> vehicles) {

        // Determine whether another vehicle is too close ahead in the
        // same lane and direction. If so, this vehicle should not move.
        final int SAFE_DISTANCE = 95;

        for (Vehicle other : vehicles) {
            // Skip self-comparison when multiple references point to same coords
            if (this.x == other.x && this.y == other.y && this.dir == other.dir) {
                continue;
            }
            if (other.dir == this.dir) {
                switch (this.dir) {
                    case North:
                        // Another vehicle with larger y is ahead when moving down
                        if (other.y > this.y && other.y - this.y <= SAFE_DISTANCE) {
                            return true;
                        }
                        break;
                    case South:
                        // Another vehicle with smaller y is ahead when moving up
                        if (other.y < this.y && this.y - other.y <= SAFE_DISTANCE) {
                            return true;
                        }
                        break;
                    case West:
                        // Ahead is a smaller x when moving left
                        if (other.x < this.x && this.x - other.x <= SAFE_DISTANCE) {
                            return true;
                        }
                        break;
                    case East:
                        // Ahead is a larger x when moving right
                        if (other.x > this.x && other.x - this.x <= SAFE_DISTANCE) {
                            return true;
                        }
                        break;
                }
            }
        }

        return false;
    }
}