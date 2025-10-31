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

        Position pos1 = world.findEmptyAdjacent(position);
        if (pos1 != null) {
            Position pos2 = world.findEmptyAdjacent(position);

            if (pos2 != null && !pos2.equals(pos1)) {
                Cell child1 = new AsexualCell(world, pos1);
                Cell child2 = new AsexualCell(world, pos2);

                world.startChildCell(child1);
                world.startChildCell(child2);
            }
        }

        this.state = State.HUNGRY;
        this.starveTimerStart = System.currentTimeMillis();
    }

    @Override
    public char getDisplayChar() {
        switch (this.state) {
            case IDLE:
            case REPRODUCING:
                return 'A';
            case HUNGRY:
            case STARVING:
                return 'a';
            default:
                return '?';
        }
    }
}
