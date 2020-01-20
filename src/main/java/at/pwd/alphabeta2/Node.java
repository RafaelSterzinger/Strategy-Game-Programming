package at.pwd.alphabeta2;

import at.pwd.game.State;

import java.util.List;
import java.util.Stack;

public class Node {
    private Node parent;

    private State state;
    private int action;
    private int depth;

    private float value;
    private List<Node> children;

    public Node(Node parent, State state, int action, int depth) {
        this.parent = parent;
        this.state = state;
        this.action = action;
        this.depth = depth;
    }

    public void expand(int depth) {
        Stack<Node> stack = new Stack<>();
        stack.push(this);
        while (!stack.isEmpty()) {
            Node current = stack.pop();
            State currentState = current.getState();
            if (current.depth == depth || currentState.isDone()) {
                current.value = currentState.getValue();;
            } else {
                current.expand().forEach(stack::push);
            }
        }
    }

    private List<Node> expand() {
        if(children.isEmpty()) {
            for (Integer action : state.getActionList()) {
                children.add(new Node(this, state.step(action), action, depth + 1));
            }
        }
        return children;
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
                children.forEach(stack::push);
            }
        }
        return null;
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

    public int getDepth() {
        return depth;
    }

    public List<Node> getChildren() {
        return children;
    }

}
