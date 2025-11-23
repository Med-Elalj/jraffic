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
    //spawning n checks
}