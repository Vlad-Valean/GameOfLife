package com.gameoflife;

public final class Constants {

    public static final int WORLD_WIDTH = 20;
    public static final int WORLD_HEIGHT = 10;

    public static final long VISUALIZER_REFRESH_MS = 50;

    public static final int INITIAL_FOOD_UNITS = 10;
    public static final int FOOD_DROPPED_ON_DEATH_MIN = 1;
    public static final int FOOD_DROPPED_ON_DEATH_MAX = 5;

    public static final long T_FULL = 5000;
    public static final long T_STARVE = 10000;
    public static final long T_PARTNER_SEARCH_MS = 10000;
    public static final long SIM_STEP_MS = 100;
    public static final int MIN_MEALS_TO_REPRODUCE = 3;

    public static final int INITIAL_ASEXUAL_CELLS = 20;
    public static final int INITIAL_SEXUAL_CELLS = 20;

    public static final long SHUTDOWN_DELAY_MS = 5000;
    public static final long MONITOR_THREAD_SLEEP_MS = 2000;

    public static final int[][] ADJACENT_DELTAS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},          { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
    };



    public static final String SIMULATION_STARTING_MESSAGE = "Simulation is starting...";
    public static final String SIMULATION_ENDING_MESSAGE = "All cells are dead. Shutting down in " + (SHUTDOWN_DELAY_MS / 1000) + " seconds...";
    public static final String INITIAL_FOOD_UNITS_MESSAGE = "Spawning " + INITIAL_FOOD_UNITS + " units of food...";
    public static final String INITIAL_ASEXUAL_CELLS_MESSAGE = "Spawning " + INITIAL_ASEXUAL_CELLS + " asexual cells...";
    public static final String INITIAL_SEXUAL_CELLS_MESSAGE = "Spawning " + INITIAL_SEXUAL_CELLS + " sexual cells...";
    public static final String SETUP_COMPLETE_MESSAGE = "Simulation setup complete. Visualizer is running.";

    public static final String WORLD_CREATED_MESSAGE = "World class created with size " + WORLD_WIDTH + "x" + WORLD_HEIGHT;

    public static final String GAME_TITLE = "Game of Life";

    public static final class DISPLAY_CELLS {
        private DISPLAY_CELLS() {}

        public record CharSet(char IDLE, char REPRODUCING, char HUNGRY, char STARVING) {}
        public static final CharSet SEXUAL = new CharSet('S', 'R', 's', 's');
        public static final CharSet ASEXUAL = new CharSet('A', 'A', 'a', 'a');
        public static final char UNKNOWN = '?';
    }
}