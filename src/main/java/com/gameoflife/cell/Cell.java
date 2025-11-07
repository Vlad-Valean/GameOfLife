package com.gameoflife.cell;

import com.gameoflife.util.Position;

public interface Cell extends Runnable {
    void moveToRandomAdjacent();

    Position getPosition();
    void partnerFound();
    char getDisplayChar();
    State getState();
    boolean isAlive();
}
