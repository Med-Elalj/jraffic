package app;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputHandler {

    public static void attach(Scene scene, Simulation simulation) {
        scene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();

            switch (key) {
                case UP -> simulation.spawnVehicleFromSouth();
                case DOWN -> simulation.spawnVehicleFromNorth();
                case LEFT -> simulation.spawnVehicleFromEast();
                case RIGHT -> simulation.spawnVehicleFromWest();
                case R -> simulation.spawnVehicleRandom();
                case ESCAPE -> System.exit(0);
            }
        });
    }
}
