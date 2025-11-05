package main.java.com.gameoflife.util;

public record Position(int x, int y) {

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
