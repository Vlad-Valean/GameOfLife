package main.java.com.gameoflife;

import main.java.com.gameoflife.util.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class World {

    private final int width;
    private final int height;
    private final Random random = new Random();

    private final ConcurrentHashMap<Position, AtomicInteger> foodGrid;

    public World() {
        this.width = Constants.WORLD_WIDTH;
        this.height = Constants.WORLD_HEIGHT;
        this.foodGrid = new ConcurrentHashMap<>();

        System.out.println("World class created with size " + this.width + "x" + this.height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void spawnFood(Position pos, int quantity) {
        foodGrid.computeIfAbsent(pos, k -> new AtomicInteger(0)).addAndGet(quantity);
    }

    private void clearConsole() {
        for (int i = 0; i < height; ++i) {
            System.out.println();
        }
    }

    public void printWorld() {
        clearConsole();

        Map<Position, AtomicInteger> foodSnapshot = new HashMap<>(this.foodGrid);

        StringBuilder sb = new StringBuilder();
        sb.append("Game of Life Simulation\n");

        sb.append("=".repeat(width + 2)).append("\n");

        for (int y = 0; y < height; y++) {
            sb.append("|");
            for (int x = 0; x < width; x++) {
                Position currentPos = new Position(x, y);

                AtomicInteger foodCount = foodSnapshot.get(currentPos);

                if (foodCount != null && foodCount.get() > 0) {
                    sb.append('*');
                } else {
                    sb.append(' ');
                }
            }
            sb.append("|\n");
        }

        sb.append("=".repeat(width + 2)).append("\n");

        int totalFood = foodSnapshot.values().stream().mapToInt(AtomicInteger::get).sum();
        sb.append("Cells: 0 | Food: ").append(totalFood).append("\n");

        System.out.print(sb);
        System.out.flush();
    }
}