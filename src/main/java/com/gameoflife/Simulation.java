package main.java.com.gameoflife;

import main.java.com.gameoflife.cell.AsexualCell;
import main.java.com.gameoflife.cell.SexualCell;
import main.java.com.gameoflife.visualization.Visualizer;
import main.java.com.gameoflife.util.Position;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;


public class Simulation {

    public static void main(String[] args) {
        System.out.println(Constants.SIMULATION_STARTING_MESSAGE);

        ExecutorService executor = Executors.newCachedThreadPool();

        World world = new World(executor);

        Random random = new Random();
        System.out.println(Constants.INITIAL_FOOD_UNITS_MESSAGE);
        for (int i = 0; i < Constants.INITIAL_FOOD_UNITS; i++) {
            int x = random.nextInt(world.getWidth());
            int y = random.nextInt(world.getHeight());
            Position pos = new Position(x, y);

            world.spawnFood(pos, 1);
        }

        System.out.println(Constants.INITIAL_ASEXUAL_CELLS_MESSAGE);
        for (int i = 0; i < Constants.INITIAL_ASEXUAL_CELLS; i++) {
            int x = random.nextInt(world.getWidth());
            int y = random.nextInt(world.getHeight());
            Position pos = new Position(x, y);

            AsexualCell cell = new AsexualCell(world, pos);
            world.registerCell(cell, pos);
            executor.submit(cell);
        }

        System.out.println(Constants.INITIAL_SEXUAL_CELLS_MESSAGE);
        for (int i = 0; i < Constants.INITIAL_SEXUAL_CELLS; i++) {
            int x = random.nextInt(world.getWidth());
            int y = random.nextInt(world.getHeight());
            Position pos = new Position(x, y);

            SexualCell cell = new SexualCell(world, pos);
            world.registerCell(cell, pos);
            executor.submit(cell);
        }


        Visualizer visualizer = new Visualizer(world);
        executor.submit(visualizer);

        Runnable shutdownMonitor = () -> {
            try {
                Thread.sleep(10000);

                while (world.getAliveCellCount() > 0) {
                    Thread.sleep(Constants.MONITOR_THREAD_SLEEP_MS);
                }

                System.out.println(Constants.SIMULATION_ENDING_MESSAGE);
                Thread.sleep(Constants.SHUTDOWN_DELAY_MS);

                executor.shutdownNow();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        executor.submit(shutdownMonitor);

        System.out.println(Constants.SETUP_COMPLETE_MESSAGE);
    }
}
