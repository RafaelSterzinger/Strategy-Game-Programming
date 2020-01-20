package at.pwd.alphabeta;

import at.pwd.game.State;

import java.util.*;

public class Node {
    private float value;
    private int move;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }
}
