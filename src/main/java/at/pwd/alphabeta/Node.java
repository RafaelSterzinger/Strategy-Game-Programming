package at.pwd.alphabeta;

import at.pwd.game.State;

import java.util.*;

public class Node {
    private Node parent;

    private State state;
    private int action;

    private float value;
    private List<Node> children;

    public Node(Node parent, State state, int action) {
        this.parent = parent;
        this.state = state;
        this.action = action;
        children=new ArrayList<>();
    }

    public List<Node> expand() {
        if (children.isEmpty()) {
            for (Integer action : state.getActionList()) {
                children.add(new Node(this, state.step(action), action));
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

    public Node getNode(State state) {
        int playerId = state.getPlayerTurn();
        Stack<Node> stack = new Stack<>();
        stack.push(this);
        while (!stack.isEmpty()) {
            Node current = stack.pop();
            State currentState = current.getState();
            if (state.equals(currentState)) {
                System.out.println("successful changed");
                return current;
            } else if (currentState.getPlayerTurn() == playerId && !currentState.isDone()) {
                current.getChildren().forEach(stack::push);
            }
        }
        return new Node(null, state, -1);
    }

    public Node getParent() {
        return parent;
    }

    public State getState() {
        return state;
    }

    public int getAction() {
        return action;
    }

    public List<Node> getChildren() {
        return children;
    }
}

//    private float alphaBeta(int depth, float alpha, float beta, boolean maximizingPlayer) {
//        if (depth == 0 || state.isDone()) {
//            return state.getValue();
//        }
//        this.expand();
//        List<Integer> actions = state.getActionList();
//        if (maximizingPlayer) {
//            for (Node child : children) {
//                float value = child.alphaBeta(depth - 1, alpha, beta, state.getPlayerTurn() == child.getState().getPlayerTurn());
//                alpha = Math.max(alpha, value);
//                if (alpha > beta) {
//                    break;
//                }
//            }
//            return value;
//        } else {
//            for (Integer action : actions) {
//                State nextState = state.step(action);
//                node = alphaBeta(nextState, depth - 1, alpha, beta, !(state.getPlayerTurn() == nextState.getPlayerTurn()));
//                node.setMove(action);
//                beta = Math.min(beta, node.getValue());
//                if (alpha > beta) {
//                    break;
//                }
//            }
//            return node;
//        }
//    }
