package main.java.com.gameoflife.visualization;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import main.java.com.gameoflife.Constants;
import main.java.com.gameoflife.World;
import main.java.com.gameoflife.cell.Cell;
import main.java.com.gameoflife.util.Position;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Visualizer implements Runnable {

    private final World world;
    private Terminal terminal;

    public Visualizer(World world) {
        this.world = world;
        try {
            DefaultTerminalFactory factory = new DefaultTerminalFactory();
            factory.setInitialTerminalSize(new TerminalSize(
                    Constants.WORLD_WIDTH + 2,
                    Constants.WORLD_HEIGHT + 4
            ));
            this.terminal = factory.createTerminal();
            this.terminal.enterPrivateMode();
            this.terminal.clearScreen();
            this.terminal.setCursorVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                World.WorldSnapshot snapshot = world.getSnapshots();
                Map<Position, Cell> cells = snapshot.cells();
                Map<Position, AtomicInteger> food = snapshot.food();

                terminal.clearScreen();

                drawBorders();
                putString(0, 0, "Game of Life");

                terminal.setForegroundColor(TextColor.Factory.fromString("#E6DB74"));
                for (Map.Entry<Position, AtomicInteger> entry : food.entrySet()) {
                    if (entry.getValue().get() > 0) {
                        Position pos = entry.getKey();
                        terminal.setCursorPosition(pos.x() + 1, pos.y() + 1);
                        terminal.putCharacter('*');
                    }
                }

                for (Map.Entry<Position, Cell> entry : cells.entrySet()) {
                    Position pos = entry.getKey();
                    char displayChar = entry.getValue().getDisplayChar();

                    switch (displayChar) {
                        case 'A': terminal.setForegroundColor(TextColor.Factory.fromString("#A6E22E")); break;
                        case 'a': terminal.setForegroundColor(TextColor.Factory.fromString("#F92672")); break;
                        case 'S': terminal.setForegroundColor(TextColor.Factory.fromString("#66D9EF")); break;
                        case 's': terminal.setForegroundColor(TextColor.Factory.fromString("#FD971F")); break;
                        case 'R': terminal.setForegroundColor(TextColor.Factory.fromString("#AE81FF")); break;
                        default: terminal.setForegroundColor(TextColor.ANSI.WHITE);
                    }

                    terminal.setCursorPosition(pos.x() + 1, pos.y() + 1);
                    terminal.putCharacter(displayChar);
                }

                int totalCells = cells.size();
                int totalFood = food.values().stream().mapToInt(AtomicInteger::get).sum();
                String status = String.format("Cells: %-5d | Food: %-5d", totalCells, totalFood);
                putString(1, Constants.WORLD_HEIGHT + 2, status);

                terminal.flush();

                Thread.sleep(Constants.VISUALIZER_REFRESH_MS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (terminal != null) {
                    terminal.exitPrivateMode();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawBorders() throws IOException {
        terminal.setForegroundColor(TextColor.ANSI.WHITE);
        int width = Constants.WORLD_WIDTH;
        int height = Constants.WORLD_HEIGHT;

        terminal.setCursorPosition(0, 1); terminal.putCharacter('+');
        terminal.setCursorPosition(width + 1, 1); terminal.putCharacter('+');
        terminal.setCursorPosition(0, height + 2); terminal.putCharacter('+');
        terminal.setCursorPosition(width + 1, height + 2); terminal.putCharacter('+');

        for (int x = 1; x <= width; x++) {
            terminal.setCursorPosition(x, 1); terminal.putCharacter('-');
            terminal.setCursorPosition(x, height + 2); terminal.putCharacter('-');
        }
        for (int y = 2; y <= height + 1; y++) {
            terminal.setCursorPosition(0, y); terminal.putCharacter('|');
            terminal.setCursorPosition(width + 1, y); terminal.putCharacter('|');
        }
    }

    private void putString(int x, int y, String s) throws IOException {
        terminal.setCursorPosition(x, y);
        for (char c : s.toCharArray()) {
            terminal.putCharacter(c);
        }
    }
}