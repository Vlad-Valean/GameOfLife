package main.java.com.gameoflife.cell;

import main.java.com.gameoflife.Constants;
import main.java.com.gameoflife.World;
import main.java.com.gameoflife.util.Position;

public class AsexualCell extends BaseCell {

    public AsexualCell(World world, Position position) {
        super(world, position);
    }

    private boolean spawnCell(Position pos) {
        return pos != null && world.startChildCell(new AsexualCell(world, pos));
    }

    @Override
    public void reproduce() {
        this.mealsEaten = 0;

        Position pos1 = world.findEmptyAdjacent(position);
        Position pos2 = world.findEmptyAdjacent(position);

        boolean spawn1 = spawnCell(pos1);
        boolean spawn2 = (pos2 != null && !pos2.equals(pos1) && spawnCell(pos2));
        boolean spawnSuccessful = spawn1 || spawn2;

        if (!spawnSuccessful) {
            moveToRandomAdjacent();
        }

        this.state = State.HUNGRY;
        this.starveTimerStart = System.currentTimeMillis();
    }

    @Override
    public char getDisplayChar() {
        return switch (this.state) {
            case IDLE -> Constants.DISPLAY_CELLS.ASEXUAL.IDLE();
            case REPRODUCING -> Constants.DISPLAY_CELLS.ASEXUAL.REPRODUCING();
            case HUNGRY -> Constants.DISPLAY_CELLS.ASEXUAL.HUNGRY();
            case STARVING -> Constants.DISPLAY_CELLS.ASEXUAL.STARVING();
            default -> Constants.DISPLAY_CELLS.UNKNOWN;
        };
    }
}
