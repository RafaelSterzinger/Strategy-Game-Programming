package at.pwd.alphabeta;

import at.pwd.game.State;

import java.util.*;

public class Node {
    private float value;
    private Integer move;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Integer getMove() {
        return move;
    }

    public void setMove(Integer move) {
        this.move = move;
    }
}
