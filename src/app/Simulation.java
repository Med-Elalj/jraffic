package app;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Simulation {

    private GraphicsContext gc;

    // Road and lane sizes
    private static final int ROAD_WIDTH = 100;
    private static final int LANE_COUNT = 2;
    private static final int LANE_WIDTH = ROAD_WIDTH / LANE_COUNT;
    private static final int LIGHT_SIZE = 20;

    public Simulation(GraphicsContext gc) {
        this.gc = gc;
    }

    public void start() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                render();
            }
        };
        timer.start();
    }

    private void render() {
        // Clear canvas
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, 900, 900);

        // Draw vertical road
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(400, 0, ROAD_WIDTH, 900);

        // Draw horizontal road
        gc.fillRect(0, 400, 900, ROAD_WIDTH);

        // Draw lanes (simple lines)
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(2);

        // Vertical lanes
        for (int i = 1; i < LANE_COUNT; i++) {
            double x = 400 + i * LANE_WIDTH;
            gc.strokeLine(x, 0, x, 900);
        }

        // Horizontal lanes
        for (int i = 1; i < LANE_COUNT; i++) {
            double y = 400 + i * LANE_WIDTH;
            gc.strokeLine(0, y, 900, y);
        }

        // Draw traffic light placeholders (red/green)
        // North-South lights
        gc.setFill(Color.RED);
        gc.fillOval(390, 390, LIGHT_SIZE, LIGHT_SIZE); // north
        gc.fillOval(490, 490, LIGHT_SIZE, LIGHT_SIZE); // south

        // East-West lights
        gc.fillOval(390, 490, LIGHT_SIZE, LIGHT_SIZE); // west
        gc.fillOval(490, 390, LIGHT_SIZE, LIGHT_SIZE); // east
    }

    // Empty spawn methods
    public void spawnVehicleFromSouth() {}
    public void spawnVehicleFromNorth() {}
    public void spawnVehicleFromEast() {}
    public void spawnVehicleFromWest() {}
    public void spawnVehicleRandom() {}
}
