import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class Main extends JPanel implements ActionListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 700;

    private List<Vehicle> vehicleList = new ArrayList<>();
    private TrafficSystem.TrafficHub hub = new TrafficSystem.TrafficHub();
    private Timer timer;
    private Map<String, BufferedImage> vehicleImages = new HashMap<>();

    public Main() {
        loadImages();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        addSouth(vehicleList);
                        break;
                    case KeyEvent.VK_DOWN:
                        addNorth(vehicleList);
                        break;
                    case KeyEvent.VK_LEFT:
                        addWest(vehicleList);
                        break;
                    case KeyEvent.VK_RIGHT:
                        addEast(vehicleList);
                        break;
                    case KeyEvent.VK_R:
                        addRandom(vehicleList);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0);
                        break;
                }
            }
        });

        timer = new Timer(16, this); 
        timer.start();
    }

    private void loadImages() {
        String[] colors = {"Blue", "Yellow", "Brown"};
        String[] directions = {"up", "down", "left", "right"};
        MovementDirection[] dirs = {MovementDirection.North, MovementDirection.South, MovementDirection.West, MovementDirection.East};

        for (int i = 0; i < colors.length; i++) {
            String color = colors[i];
            for (int j = 0; j < directions.length; j++) {
                String dir = directions[j];
                String key = color + "_" + dirs[j].name();
                try {
                    BufferedImage img = ImageIO.read(new File("assets/" + color + "/" + dir + ".png"));
                    vehicleImages.put(key, img);
                } catch (IOException e) {
                    System.err.println("Failed to load image: " + key);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    private void update() {
        TrafficSystem.updateLights(hub, vehicleList);

        List<Vehicle> snapshot = new ArrayList<>(vehicleList);
        for (Vehicle vehicle : vehicleList) {
            TrafficSystem.checkLights(vehicle, hub);
            if (vehicle.moving && !vehicle.blocked(snapshot)) {
                vehicle.step();
            }
            if (!vehicle.turned) {
                vehicle.turnCheck();
            }
        }

        vehicleList.removeIf(v -> v.y > 740 || v.y < -40 || v.x > 840 || v.x < -40);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }

    private void draw(Graphics2D g) {
        g.setColor(new Color(5, 8, 15));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(new Color(18, 20, 25));
        g.fillRect(0, 300, 800, 100);
        g.fillRect(350, 0, 100, 700);

        g.setColor(new Color(12, 15, 20));
        g.fillRect(350, 300, 100, 100);

        g.setColor(new Color(0, 128, 255, 51)); 
        g.fillOval(387, 337, 25, 25);
        g.setColor(new Color(0, 128, 255, 204)); 
        g.drawOval(392, 342, 15, 15);

        drawTrafficLight(g, 310, 260, hub.northOn); 
        drawTrafficLight(g, 310, 410, hub.westOn);  
        drawTrafficLight(g, 460, 410, hub.southOn);
        drawTrafficLight(g, 460, 260, hub.eastOn);  

        for (Vehicle v : vehicleList) {
            String key = v.colorName + "_" + v.dir.name();
            BufferedImage img = vehicleImages.get(key);
            if (img != null) {
                double baseWidth, baseHeight;
                if (v.dir == MovementDirection.North || v.dir == MovementDirection.South) {
                    baseWidth = 30;
                    baseHeight = 45;
                } else {
                    baseWidth = 45;
                    baseHeight = 30;
                }
                int width = (int) Math.round(baseWidth * 1.2);
                int height = (int) Math.round(baseHeight * 1.2);
                int x = v.x - width / 2;
                int y = v.y - height / 2;
                g.drawImage(img, x, y, width, height, null);
            } else {
                g.setColor(v.color);
                int x = v.x;
                int y = v.y;
                int w = (int) Math.round(30 * 1.2);
                int h = (int) Math.round(45 * 1.2);
                g.fillRect(x - w/2, y - h/2, w, h);
            }
        }

        g.setColor(new Color(48, 144, 255, 229)); 
        g.drawString("Arrows to Spawn Vehicles | R Random | ESC Exit", 12, 24);
        g.setColor(new Color(128, 128, 255, 204)); 
    }

    private void drawTrafficLight(Graphics2D g, int x, int y, boolean isOn) {
        Color color = isOn ? new Color(32, 255, 48) : new Color(255, 16, 32);
        g.setColor(color);
        g.fillOval(x, y, 30, 30);
    }

    private static boolean canSpawnVehicle(List<Vehicle> vehicleList, int spawnX, int spawnY, MovementDirection dir) {
        final int MAX_VEHICLES = 28;
        final int MIN_SPAWN_DISTANCE = 80;

        if (vehicleList.size() >= MAX_VEHICLES) {
            return false;
        }

        return !vehicleList.stream().anyMatch(v ->
            v.startDir == dir
            && Math.abs(spawnX - v.x) < MIN_SPAWN_DISTANCE
            && Math.abs(spawnY - v.y) < MIN_SPAWN_DISTANCE
        );
    }

    private static void addVehicleAt(List<Vehicle> vehicleList, int x, int y, MovementDirection dir) {
        if (canSpawnVehicle(vehicleList, x, y, dir)) {
            vehicleList.add(Vehicle.spawn(x, y, dir, Vehicle.randColorName()));
        }
    }

    private static void addSouth(List<Vehicle> vehicleList) {
        addVehicleAt(vehicleList, 410, 700, MovementDirection.South);
    }

    private static void addNorth(List<Vehicle> vehicleList) {
        addVehicleAt(vehicleList, 360, -30, MovementDirection.North);
    }

    private static void addWest(List<Vehicle> vehicleList) {
        addVehicleAt(vehicleList, -30, 360, MovementDirection.East);
    }

    private static void addEast(List<Vehicle> vehicleList) {
        addVehicleAt(vehicleList, 800, 310, MovementDirection.West);
    }

    private static void addRandom(List<Vehicle> vehicleList) {
        int rand = (int) (Math.random() * 4);
        switch (rand) {
            case 0:
                addSouth(vehicleList);
                break;
            case 1:
                addNorth(vehicleList);
                break;
            case 2:
                addWest(vehicleList);
                break;
            case 3:
                addEast(vehicleList);
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Jraffic");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Main());
            frame.pack();
            frame.setVisible(true);
        });
    }
}