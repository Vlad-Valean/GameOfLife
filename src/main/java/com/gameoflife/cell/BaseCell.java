package com.gameoflife.cell;

import com.gameoflife.Constants;
import com.gameoflife.World;
import com.gameoflife.util.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class BaseCell implements Cell {

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



    public abstract void reproduce() throws InterruptedException;

    @Override
    public abstract char getDisplayChar();



    public void moveToTarget(Position target) {
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
                this.position = newPos;
                return;
            }
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