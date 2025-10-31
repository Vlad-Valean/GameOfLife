package main.java.com.gameoflife.cell;

import main.java.com.gameoflife.World;
import main.java.com.gameoflife.util.Position;

public class SexualCell extends BaseCell {

    public SexualCell(World world, Position position) {
        super(world, position);
    }

    @Override
    public void reproduce() {
        Cell partner = world.findPartner(this);

        if (partner != null) {
            partner.partnerFound();

            this.mealsEaten = 0;
            this.state = State.HUNGRY;
            this.starveTimerStart = System.currentTimeMillis();

            Position childPos = world.findEmptyAdjacent(position);
            if (childPos != null) {
                Cell child = new SexualCell(world, childPos);
                world.startChildCell(child);
            }
        } else {}
    }

    @Override
    public void partnerFound() {
        this.mealsEaten = 0;
        this.state = State.HUNGRY;
        this.starveTimerStart = System.currentTimeMillis();
    }

    @Override
    public char getDisplayChar() {
        switch (this.state) {
            case IDLE:
                return 'S';
            case REPRODUCING:
                return 'R';
            case HUNGRY:
            case STARVING:
                return 's';
            default:
                return '?';
        }
    }
}