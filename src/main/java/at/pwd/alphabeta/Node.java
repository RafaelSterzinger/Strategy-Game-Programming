package at.pwd.alphabeta;

import at.pwd.game.State;

import java.util.*;

public class Node {
    private State state;
    private List<Node> children;
    private int depth;

    public Node(State state, int depth) {
        this.state = state;
        this.depth = depth;
    }

    public State getState() {
        return state;
    }

    public boolean isTerminal() {
        return state.isDone();
    }

    public int getValue() {
        return state.getValue();
    }

//    public void expand(int depth) {
//        // expand loop
//        Stack<Node> nodes = new Stack<>();
//        nodes.push(this);
//        while (!nodes.isEmpty()) {
//            Node current = nodes.pop();
//            if()
//            State state = current.getState();
//            List<Integer> actions = state.getActionList();
//            for (Integer action : actions) {
//                State newState = state.step(action);
//                nodes.push(new Node(newState, current.depth + 1));
//            }
//        }
//    }
//
//    public void expand() {
//        List<Node> newChildren = new LinkedList<>();
//        for (Node node : children) {
//            if (node.isTerminal()) {
//                newChildren.add(node);
//            } else {
//                for (Integer action : node.getState().getActionList()) {
//                    newChildren.add(new Node(state.step(action), depth + 1));
//                }
//            }
//        }
//        children = newChildren;
//    }

    @Override
    public String toString() {
        return state.getId();
    }
}
