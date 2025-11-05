package main.java.com.gameoflife.util;

import java.util.Random;

public record Position(int x, int y) {

    private static final Random random = new Random();

    public Position getAdjacent(int width, int height) {
        int dx = random.nextInt(3) - 1;
        int dy = random.nextInt(3) - 1;

        if (dx == 0 && dy == 0) {
            if (random.nextBoolean()) {
                dx = (random.nextBoolean() ? 1 : -1);
            } else {
                dy = (random.nextBoolean() ? 1 : -1);
            }
        }

        int newX = (this.x + dx + width) % width;
        int newY = (this.y + dy + height) % height;

        return new Position(newX, newY);
    }

    public Position getAdjacent(int dx, int dy, int width, int height) {
        int newX = (this.x + dx + width) % width;
        int newY = (this.y + dy + height) % height;
        return new Position(newX, newY);
    }

    public double distanceTo(Position other) {
        long dx = this.x - other.x();
        long dy = this.y - other.y();
        return Math.hypot(dx, dy);
    }
}
