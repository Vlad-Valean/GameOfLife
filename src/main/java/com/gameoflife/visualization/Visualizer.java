package main.java.com.gameoflife.visualization;

import main.java.com.gameoflife.Constants;
import main.java.com.gameoflife.World;

public class Visualizer implements Runnable {

    private final World world;

    public Visualizer(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        try {
            while (true) {
                world.printWorld();

                Thread.sleep(Constants.VISUALIZER_REFRESH_MS);
            }
        } catch (InterruptedException e) {
            System.out.println("Visualizer thread interrupted. Stopping.");
            Thread.currentThread().interrupt();
        }
    }
}