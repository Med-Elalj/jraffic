import java.awt.Color;

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

    public static Vehicle spawn(int x, int y, MovementDirection dir, String colorName) {
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
                color = Color.BLUE;
        }
        return new Vehicle(x, y, dir, color, colorName);
    }

    public void step() {
        switch (this.dir) {
            case North:
                this.y += 2;
                break;
            case South:
                this.y -= 2;
                break;
            case West:
                this.x -= 2;
                break;
            case East:
                this.x += 2;
                break;
        }
    }

    public void turnCheck() {

        if (this.dir == MovementDirection.North) {
            if (this.x == 360 && this.y == 310 && this.color.equals(BROWN)) {
                this.dir = MovementDirection.West;
                this.turned = true;
            } else if (this.x == 360 && this.y == 360 && this.color.equals(Color.YELLOW)) {
                this.dir = MovementDirection.East;
                this.turned = true;
            }
        } else if (this.dir == MovementDirection.South) {
            if (this.y == 310 && this.x == 410 && this.color.equals(Color.YELLOW)) {
                this.dir = MovementDirection.West;
                this.turned = true;
            } else if (this.y == 360 && this.x == 410 && this.color.equals(BROWN)) {
                this.dir = MovementDirection.East;
                this.turned = true;
            }
        } else if (this.dir == MovementDirection.East) {
            if (this.x == 360 && this.y == 360 && this.color.equals(BROWN)) {
                this.dir = MovementDirection.North;
                this.turned = true;
            } else if (this.x == 410 && this.y == 360 && this.color.equals(Color.YELLOW)) {
                this.dir = MovementDirection.South;
                this.turned = true;
            }
        } else if (this.dir == MovementDirection.West) {
            if (this.x == 360 && this.y == 310 && this.color.equals(Color.YELLOW)) {
                this.dir = MovementDirection.North;
                this.turned = true;
            } else if (this.x == 410 && this.y == 310 && this.color.equals(BROWN)) {
                this.dir = MovementDirection.South;
                this.turned = true;
            }
        }

    }

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

    public boolean blocked(java.util.List<Vehicle> vehicles) {

        final int SAFE_DISTANCE = 95;

        for (Vehicle other : vehicles) {
            if (this.x == other.x && this.y == other.y && this.dir == other.dir) {
                continue;
            }
            if (other.dir == this.dir) {
                switch (this.dir) {
                    case North:
                        if (other.y > this.y && other.y - this.y <= SAFE_DISTANCE) {
                            return true;
                        }
                        break;
                    case South:
                        if (other.y < this.y && this.y - other.y <= SAFE_DISTANCE) {
                            return true;
                        }
                        break;
                    case West:
                        if (other.x < this.x && this.x - other.x <= SAFE_DISTANCE) {
                            return true;
                        }
                        break;
                    case East:
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