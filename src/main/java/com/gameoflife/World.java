package main.java.com.gameoflife;

import main.java.com.gameoflife.cell.Cell;
import main.java.com.gameoflife.util.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class World {

    private final int width;
    private final int height;
    private final Random random = new Random();
    private final ExecutorService executor;

    private final ConcurrentHashMap<Position, AtomicInteger> foodGrid;
    private final ConcurrentHashMap<Position, Cell> cellGrid;

    private final Object partnerLock = new Object();
    private Cell waitingPartner = null;

    public record WorldSnapshot(Map<Position, Cell> cells, Map<Position, AtomicInteger> food) {}

    public World(ExecutorService executor) {
        this.width = Constants.WORLD_WIDTH;
        this.height = Constants.WORLD_HEIGHT;
        this.foodGrid = new ConcurrentHashMap<>();
        this.cellGrid = new ConcurrentHashMap<>();
        this.executor = executor;

        System.out.println("World class created with size " + this.width + "x" + this.height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getAliveCellCount() {
        return cellGrid.size();
    }

    public WorldSnapshot getSnapshots() {
        Map<Position, Cell> cellSnapshot = new HashMap<>(this.cellGrid);
        Map<Position, AtomicInteger> foodSnapshot = new HashMap<>(this.foodGrid);
        return new WorldSnapshot(cellSnapshot, foodSnapshot);
    }

    public boolean tryConsumeFood(Position pos) {
        AtomicInteger foodCount = foodGrid.get(pos);
        if (foodCount != null) {
            int currentValue;
            do {
                currentValue = foodCount.get();
                if (currentValue <= 0) {
                    return false;
                }
            } while (!foodCount.compareAndSet(currentValue, currentValue - 1));
            return true;
        }
        return false;
    }

    public void spawnFood(Position pos, int quantity) {
        foodGrid.computeIfAbsent(pos, k -> new AtomicInteger(0)).addAndGet(quantity);
    }

    public void registerCell(Cell cell, Position pos) {
        cellGrid.put(pos, cell);
    }

    public void unregisterCell(Cell cell, Position pos) {
        cellGrid.remove(pos, cell);
    }

    public boolean tryMoveCell(Cell cell, Position oldPos, Position newPos) {
        if (cellGrid.containsKey(newPos)) {
            return false;
        }
        if (cellGrid.putIfAbsent(newPos, cell) == null) {
            unregisterCell(cell, oldPos);
            return true;
        }
        return false;
    }

    public void startChildCell(Cell cell) {
        registerCell(cell, cell.getPosition());
        executor.submit(cell);
    }

    public Position findEmptyAdjacent(Position pos) {
        int startX = random.nextInt(3) - 1;
        int startY = random.nextInt(3) - 1;

        for (int i = 0; i < 3; i++) {
            int dx = (startX + i) % 3;
            for (int j = 0; j < 3; j++) {
                int dy = (startY + j) % 3;
                if (dx == 0 && dy == 0) continue;

                Position newPos = pos.getAdjacent(dx, dy, width, height);
                if (!cellGrid.containsKey(newPos)) {
                    return newPos;
                }
            }
        }
        return null;
    }

    public Cell findPartner(Cell requester) {
        synchronized (partnerLock) {
            if (waitingPartner == null) {
                waitingPartner = requester;
                return null;
            } else {
                Cell partner = waitingPartner;
                waitingPartner = null;
                return partner;
            }
        }
    }
}
