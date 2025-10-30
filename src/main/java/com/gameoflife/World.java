package main.java.com.gameoflife;

public class World {

    private final int width;
    private final int height;

    public World() {
        this.width = Constants.WORLD_WIDTH;
        this.height = Constants.WORLD_HEIGHT;

        System.out.println("World class created with size " + this.width + "x" + this.height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
