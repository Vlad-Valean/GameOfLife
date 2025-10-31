package main.java.com.gameoflife.cell;

import main.java.com.gameoflife.Constants;
import main.java.com.gameoflife.World;
import main.java.com.gameoflife.util.Position;
import java.util.Random;

public abstract class BaseCell implements Cell {

    protected final World world;
    protected Position position;
    protected volatile boolean isAlive = true;
    protected State state = State.HUNGRY;
    protected int mealsEaten = 0;
    protected long starveTimerStart = 0;
    protected final Random random = new Random();

    public BaseCell(World world, Position position) {
        this.world = world;
        this.position = position;
        this.starveTimerStart = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (isAlive) {
            try {
                switch (state) {
                    case IDLE:
                        idle();
                        break;
                    case HUNGRY:
                        findFood(false);
                        break;
                    case STARVING:
                        findFood(true);
                        break;
                    case REPRODUCING:
                        reproduce();
                        break;
                }
                Thread.sleep(Constants.SIM_STEP_MS);
            } catch (InterruptedException e) {
                this.isAlive = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    private void idle() throws InterruptedException {
        Thread.sleep(Constants.T_FULL);

        if (mealsEaten >= Constants.MIN_MEALS_TO_REPRODUCE) {
            this.state = State.REPRODUCING;
        } else {
            this.state = State.HUNGRY;
            this.starveTimerStart = System.currentTimeMillis();
        }
    }

    private void findFood(boolean isStarving) {
        if (isStarving) {
            long timeElapsed = System.currentTimeMillis() - this.starveTimerStart;
            if (timeElapsed > Constants.T_STARVE) {
                die();
                return;
            }
        } else {
            this.state = State.STARVING;
        }

        if (world.tryConsumeFood(position)) {
            mealsEaten++;
            this.state = State.IDLE;
        } else {
            moveToRandomAdjacent();
        }
    }

    private void moveToRandomAdjacent() {
        Position newPos = position.getAdjacent(world.getWidth(), world.getHeight());
        if (world.tryMoveCell(this, position, newPos)) {
            this.position = newPos;
        }
    }

    protected void die() {
        this.isAlive = false;
        world.unregisterCell(this, position);
        int foodDropped = random.nextInt(
                Constants.FOOD_DROPPED_ON_DEATH_MAX - Constants.FOOD_DROPPED_ON_DEATH_MIN + 1)
                + Constants.FOOD_DROPPED_ON_DEATH_MIN;
        world.spawnFood(position, foodDropped);
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
        public void partnerFound() {}

    public abstract void reproduce();

    @Override
    public abstract char getDisplayChar();
}
