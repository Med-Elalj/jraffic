package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static final int WIDTH = 900;
    public static final int HEIGHT = 900;

    @Override
    public void start(Stage stage) {

        Canvas canvas = new Canvas(WIDTH, HEIGHT);

        Simulation simulation = new Simulation(canvas.getGraphicsContext2D());
        simulation.start();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);
        InputHandler.attach(scene, simulation);

        stage.setTitle("Traffic Simulation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
