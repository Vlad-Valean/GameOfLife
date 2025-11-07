package com.gameoflife;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gameoflife.cell.Cell;
import com.gameoflife.cell.State;
import com.gameoflife.util.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class World {

    private static final Logger log = LoggerFactory.getLogger(World.class);

    private final int width;
    private final int height;
    private final Random random = new Random();
    private final ExecutorService executor;

    private final ConcurrentHashMap<Position, AtomicInteger> foodGrid;
    private final ConcurrentHashMap<Position, Cell> cellGrid;
    private final ConcurrentHashMap<Position, Cell> waitingGrid;

    public record WorldSnapshot(Map<Position, Cell> cells, Map<Position, AtomicInteger> food) {}

    public World(ExecutorService executor) {
        this.width = Constants.WORLD_WIDTH;
        this.height = Constants.WORLD_HEIGHT;
        this.foodGrid = new ConcurrentHashMap<>();
        this.cellGrid = new ConcurrentHashMap<>();
        this.waitingGrid = new ConcurrentHashMap<>();
        this.executor = executor;

        log.info(Constants.WORLD_CREATED_MESSAGE);
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

    public boolean registerCell(Cell cell, Position pos) {
        return cellGrid.putIfAbsent(pos, cell) == null;
    }
    public void unregisterCell(Cell cell, Position pos) {
        cellGrid.remove(pos, cell);
    }

    public boolean tryMoveCell(Cell cell, Position oldPos, Position newPos) {
        if (cellGrid.putIfAbsent(newPos, cell) == null) {
            unregisterCell(cell, oldPos);
            return true;
        }
        return false;
    }

    public Cell findAndPartnerAdjacent(Cell requester) {
        Position pos = requester.getPosition();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Position neighborPos = pos.getAdjacent(dx, dy, width, height);
                Cell potentialPartner = waitingGrid.get(neighborPos);
                if (potentialPartner != null &&
                        potentialPartner.isAlive() &&
                        potentialPartner.getState() == State.REPRODUCING) {
                    if (waitingGrid.remove(neighborPos, potentialPartner)) {
                        potentialPartner.partnerFound();
                        synchronized (potentialPartner) {
                            potentialPartner.notifyAll();
                        }
                        return potentialPartner;
                    }
                }
            }
        }
        return null;
    }

    public boolean registerWaiter(Cell cell) {
        return waitingGrid.putIfAbsent(cell.getPosition(), cell) == null;
    }

    public void removeWaiter(Cell cell) {
        waitingGrid.remove(cell.getPosition(), cell);
    }

    public boolean startChildCell(Cell cell) {
        boolean result = registerCell(cell, cell.getPosition());
        if(result) executor.submit(cell);
        return result;
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

    public Cell findNearestWaiter(Position pos) {
        Cell nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Cell waiter : waitingGrid.values()) {
            if (waiter.getPosition().equals(pos)) {
                continue;
            }

            double distance = pos.distanceTo(waiter.getPosition());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = waiter;
            }
        }
        return nearest;
    }
}