package com.gameoflife.cell;

import com.gameoflife.Constants;
import com.gameoflife.World;
import com.gameoflife.util.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseCell implements Cell {

    private static final Logger log = LoggerFactory.getLogger(BaseCell.class);
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    protected final int id;

    protected final World world;
    protected Position position;
    protected State state = State.HUNGRY;
    protected volatile boolean isAlive = true;
    protected volatile boolean partnerFoundFlag = false;
    protected int mealsEaten = 0;
    protected long starveTimerStart = 0;
    protected final Random random = new Random();


    public BaseCell(World world, Position position) {
        this.world = world;
        this.position = position;
        this.starveTimerStart = System.currentTimeMillis();
        this.id = idCounter.incrementAndGet();
        log.info("{} created at {}.", this, position);
    }

    @Override
    public String toString() {
        return "Cell-" + this.id;
    }

    @Override
    public void run() {
        while (isAlive) {
            try {
                switch (state) {
                    case IDLE:
                        log.trace("{} is IDLE.", this);
                        idle();
                        break;
                    case HUNGRY:
                        log.debug("{} is HUNGRY.", this);
                        findFood(false);
                        break;
                    case STARVING:
                        log.warn("{} is STARVING.", this);
                        findFood(true);
                        break;
                    case REPRODUCING:
                        log.info("{} is REPRODUCING.", this);
                        reproduce();
                        break;
                }
                Thread.sleep(Constants.SIM_STEP_MS);
            } catch (InterruptedException e) {
                this.isAlive = false;
                log.info("{} was interrupted and died.", this);
                Thread.currentThread().interrupt();
            }
        }
    }




    private void idle() throws InterruptedException {
        Thread.sleep(Constants.T_FULL);

        if (mealsEaten >= Constants.MIN_MEALS_TO_REPRODUCE) {
            log.debug("{} has eaten enough, becoming REPRODUCING.", this);
            this.state = State.REPRODUCING;
        } else {
            log.debug("{} is full, becoming HUNGRY.", this);
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
            log.debug("{} ate food at {}. Meals eaten: {}", this, position, mealsEaten);
            this.state = State.IDLE;
        } else {
            log.trace("{} found no food at {}, moving.", this, position);
            moveToRandomAdjacent();
        }
    }



    public abstract void reproduce() throws InterruptedException;

    @Override
    public abstract char getDisplayChar();



    public void moveToTarget(Position target) {
        log.trace("{} moving towards target {}.", this, target);
        if (target == null) {
            moveToRandomAdjacent();
            return;
        }
        int dx = Integer.compare(target.x(), this.position.x());
        int dy = Integer.compare(target.y(), this.position.y());
        if (dx == 0 && dy == 0) {
            moveToRandomAdjacent();
            return;
        }
        Position newPos = this.position.getAdjacent(dx, dy, world.getWidth(), world.getHeight());
        if (world.tryMoveCell(this, this.position, newPos)) {
            this.position = newPos;
        } else {
            moveToRandomAdjacent();
        }
    }

    public void moveToRandomAdjacent() {
        List<Position> possibleMoves = new ArrayList<>(8);

        for (int[] delta : Constants.ADJACENT_DELTAS) {
            possibleMoves.add(this.position.getAdjacent(delta[0], delta[1], world.getWidth(), world.getHeight()));
        }

        Collections.shuffle(possibleMoves, this.random);

        for (Position newPos : possibleMoves) {
            if (world.tryMoveCell(this, this.position, newPos)) {
                log.trace("{} moved randomly to {}.", this, newPos);
                this.position = newPos;
                return;
            }
        }
        log.trace("{} failed to move randomly, all adjacent spots full.", this);
    }



    protected void die() {
        log.info("{} died at {}.", this, position);
        this.isAlive = false;
        world.unregisterCell(this, position);
        int foodDropped = random.nextInt(
                Constants.FOOD_DROPPED_ON_DEATH_MAX - Constants.FOOD_DROPPED_ON_DEATH_MIN + 1)
                + Constants.FOOD_DROPPED_ON_DEATH_MIN;
        log.debug("{} dropped {} food units.", this, foodDropped);
        world.spawnFood(position, foodDropped);
    }



    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public void partnerFound() {
        this.partnerFoundFlag = true;
    }
}