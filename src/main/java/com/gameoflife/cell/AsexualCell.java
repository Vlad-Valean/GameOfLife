package com.gameoflife.cell;

import com.gameoflife.Constants;
import com.gameoflife.World;
import com.gameoflife.util.Position;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsexualCell extends BaseCell {

    private static final Logger log = LoggerFactory.getLogger(AsexualCell.class);

    public AsexualCell(World world, Position position) {
        super(world, position);
    }

    private boolean spawnCell(Position pos) {
        if (pos != null) {
            log.info("{} (asexual) spawning child at {}.", this, pos);
            return world.startChildCell(new AsexualCell(world, pos));
        }
        return false;
    }

    @Override
    public void reproduce() {
        this.mealsEaten = 0;

        Position pos1 = world.findEmptyAdjacent(position);
        Position pos2 = world.findEmptyAdjacent(position);

        boolean spawn1 = spawnCell(pos1);
        boolean spawn2 = (pos2 != null && !pos2.equals(pos1) && spawnCell(pos2));
        boolean spawnSuccessful = spawn1 || spawn2;

        if (spawnSuccessful) {
            log.info("{} (asexual) successfully reproduced.", this);
        } else {
            log.debug("{} (asexual) failed to reproduce, moving.", this);
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