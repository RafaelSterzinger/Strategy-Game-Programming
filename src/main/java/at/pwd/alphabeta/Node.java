package at.pwd.alphabeta;

import at.pwd.game.State;

import java.util.*;

public class Node {
    private final State state;
    private final int action;

    private float value;
    private final List<Node> children;

    public Node(State state, int action) {
        this.state = state;
        this.action = action;
        children=new ArrayList<>();
    }

    public List<Node> expand() {
        if (children.isEmpty()) {
            for (Integer action : state.getActionList()) {
                children.add(new Node(state.step(action), action));
            }
        }
        return children;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }



    public State getState() {
        return state;
    }

    public int getAction() {
        return action;
    }

    @Override
    public String toString() {
        return state.getId();
    }
}