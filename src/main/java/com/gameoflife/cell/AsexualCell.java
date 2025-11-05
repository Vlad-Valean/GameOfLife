package main.java.com.gameoflife.cell;

import main.java.com.gameoflife.World;
import main.java.com.gameoflife.util.Position;

public class AsexualCell extends BaseCell {

    public AsexualCell(World world, Position position) {
        super(world, position);
    }

    @Override
    public void reproduce() {
        this.mealsEaten = 0;
        boolean spawnSuccessful = false;

        Position pos1 = world.findEmptyAdjacent(position);
        if (pos1 != null) {
            Cell child1 = new AsexualCell(world, pos1);
            spawnSuccessful = world.startChildCell(child1);
        }

        Position pos2 = world.findEmptyAdjacent(position);
        if (pos2 != null && !pos2.equals(pos1)) {
            Cell child2 = new AsexualCell(world, pos2);
            spawnSuccessful = world.startChildCell(child2);
        }

        if (!spawnSuccessful) {
            moveToRandomAdjacent();
        }

        this.state = State.HUNGRY;
        this.starveTimerStart = System.currentTimeMillis();
    }

    @Override
    public char getDisplayChar() {
        return switch (this.state) {
            case IDLE, REPRODUCING -> 'A';
            case HUNGRY, STARVING -> 'a';
            default -> '?';
        };
    }
}
