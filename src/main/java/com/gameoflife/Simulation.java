package main.java.com.gameoflife;

import main.java.com.gameoflife.visualization.Visualizer;
import main.java.com.gameoflife.util.Position;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;


public class Simulation {

    public static void main(String[] args) {
        System.out.println("Simulation is starting...");

        World world = new World();

        Random random = new Random();
        System.out.println("Spawning " + Constants.INITIAL_FOOD_UNITS + " units of food...");
        for (int i = 0; i < Constants.INITIAL_FOOD_UNITS; i++) {
            int x = random.nextInt(world.getWidth());
            int y = random.nextInt(world.getHeight());
            Position pos = new Position(x, y);

            world.spawnFood(pos, 1);
        }

        ExecutorService executor = Executors.newCachedThreadPool();

        Visualizer visualizer = new Visualizer(world);
        executor.submit(visualizer);

        System.out.println("Simulation setup complete. Visualizer is running.");
    }
}