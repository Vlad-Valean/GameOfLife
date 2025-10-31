package main.java.com.gameoflife.cell;

import main.java.com.gameoflife.util.Position;

public interface Cell extends Runnable {
    Position getPosition();
    boolean isAlive();
    char getDisplayChar();
    void partnerFound();
}
