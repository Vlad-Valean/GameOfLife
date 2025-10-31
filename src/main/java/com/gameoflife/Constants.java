package main.java.com.gameoflife;

public final class Constants {

    public static final int WORLD_WIDTH = 50;
    public static final int WORLD_HEIGHT = 20;

    public static final long VISUALIZER_REFRESH_MS = 250;

    public static final int INITIAL_FOOD_UNITS = 100;
    public static final int FOOD_DROPPED_ON_DEATH_MIN = 1;
    public static final int FOOD_DROPPED_ON_DEATH_MAX = 5;

    public static final long T_FULL = 5000;
    public static final long T_STARVE = 10000;
    public static final long SIM_STEP_MS = 500;

    public static final int MIN_MEALS_TO_REPRODUCE = 10;
    public static final int INITIAL_ASEXUAL_CELLS = 10;
    public static final int INITIAL_SEXUAL_CELLS = 10;
}