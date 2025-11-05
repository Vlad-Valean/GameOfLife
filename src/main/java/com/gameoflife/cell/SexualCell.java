package main.java.com.gameoflife.cell;

import main.java.com.gameoflife.Constants;
import main.java.com.gameoflife.World;
import main.java.com.gameoflife.util.Position;

public class SexualCell extends BaseCell {

    public SexualCell(World world, Position position) {
        super(world, position);
    }

    @Override
    public void reproduce() throws InterruptedException {
        this.state = State.REPRODUCING;
        this.partnerFoundFlag = false;
        boolean foundPartner = false;
        try {
            Cell partner = world.findAndPartnerAdjacent(this);
            if (partner != null) {
                foundPartner = true;
                spawnChild(partner);
            } else if (world.registerWaiter(this)) {
                synchronized (this) {
                    wait(Constants.T_PARTNER_SEARCH_MS);
                }
                foundPartner = this.partnerFoundFlag;
                world.removeWaiter(this);
            }
        } finally {
            if (foundPartner) {
                this.mealsEaten = 0;
                this.state = State.HUNGRY;
                this.starveTimerStart = System.currentTimeMillis();
            } else {
                Cell targetCell = world.findNearestWaiter(this.position);
                Position targetPos = (targetCell != null) ? targetCell.getPosition() : null;
                moveToTarget(targetPos);
            }
            this.partnerFoundFlag = false;
        }
    }

    private void spawnChild(Cell partner) {
        Position childPos = world.findEmptyAdjacent(position);
        if (childPos == null) {
            childPos = world.findEmptyAdjacent(partner.getPosition());
        }
        if (childPos != null) {
            Cell child = new SexualCell(world, childPos);
            world.startChildCell(child);
        }
    }

    @Override
    public char getDisplayChar() {
        return switch (this.state) {
            case IDLE -> Constants.DISPLAY_CELLS.SEXUAL.IDLE();
            case REPRODUCING -> Constants.DISPLAY_CELLS.SEXUAL.REPRODUCING();
            case HUNGRY -> Constants.DISPLAY_CELLS.SEXUAL.HUNGRY();
            case STARVING -> Constants.DISPLAY_CELLS.SEXUAL.STARVING();
            default -> Constants.DISPLAY_CELLS.UNKNOWN;
        };
    }
}