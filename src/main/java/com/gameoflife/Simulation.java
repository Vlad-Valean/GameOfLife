package com.gameoflife;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gameoflife.cell.AsexualCell;
import com.gameoflife.cell.SexualCell;
import com.gameoflife.visualization.Visualizer;
import com.gameoflife.util.Position;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;


public class Simulation {

    private static final Logger log = LoggerFactory.getLogger(Simulation.class);

    public static void main(String[] args) {
        log.info(Constants.SIMULATION_STARTING_MESSAGE);

        ExecutorService executor = Executors.newCachedThreadPool();

        World world = new World(executor);

        Random random = new Random();
        log.info(Constants.INITIAL_FOOD_UNITS_MESSAGE);
        for (int i = 0; i < Constants.INITIAL_FOOD_UNITS; i++) {
            int x = random.nextInt(world.getWidth());
            int y = random.nextInt(world.getHeight());
            Position pos = new Position(x, y);

            world.spawnFood(pos, 1);
        }

        log.info(Constants.INITIAL_ASEXUAL_CELLS_MESSAGE);
        for (int i = 0; i < Constants.INITIAL_ASEXUAL_CELLS; i++) {
            int x = random.nextInt(world.getWidth());
            int y = random.nextInt(world.getHeight());
            Position pos = new Position(x, y);

            AsexualCell cell = new AsexualCell(world, pos);
            world.registerCell(cell, pos);
            executor.submit(cell);
        }

        log.info(Constants.INITIAL_SEXUAL_CELLS_MESSAGE);
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

                log.info(Constants.SIMULATION_ENDING_MESSAGE);
                Thread.sleep(Constants.SHUTDOWN_DELAY_MS);

                executor.shutdownNow();

            } catch (InterruptedException e) {
                log.warn("Shutdown monitor interrupted.", e);
                Thread.currentThread().interrupt();
            }
        };

        executor.submit(shutdownMonitor);

        log.info(Constants.SETUP_COMPLETE_MESSAGE);
    }
}