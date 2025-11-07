package com.gameoflife.cell;

import com.gameoflife.Constants;
import com.gameoflife.World;
import com.gameoflife.util.Position;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SexualCell extends BaseCell {

    private static final Logger log = LoggerFactory.getLogger(SexualCell.class);

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
                log.info("{} mated with {} at {}.", this, partner, position);
                foundPartner = true;
                spawnChild(partner);
            } else if (world.registerWaiter(this)) {
                log.debug("{} is waiting for a partner.", this);
                synchronized (this) {
                    wait(Constants.T_PARTNER_SEARCH_MS);
                }
                foundPartner = this.partnerFoundFlag;
                if (foundPartner) {
                    log.debug("{} was found by a partner while waiting.", this);
                } else {
                    log.debug("{} finished waiting, no partner found.", this);
                }
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
                log.debug("{} found no partner, moving towards {}.", this, targetPos);
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
            log.info("{} and {} spawning a child at {}.", this, partner, childPos);
            Cell child = new SexualCell(world, childPos);
            world.startChildCell(child);
        } else {
            log.warn("{} and {} failed to spawn child, no empty adjacent space.", this, partner);
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