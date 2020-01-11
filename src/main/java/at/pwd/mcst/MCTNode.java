package at.pwd.mcst;

import at.pwd.game.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MCTNode {
    private static final Random RANDOM = new Random();

    private State state;
    private MCTNode parent;
    private Integer action;

    private List<MCTNode> children;
    private MCTNode bestSuccessor;

    private int winCount;
    private int visitCount;

    private List<Integer> possibleActions;

    private double currentValue;

    private static final double C = 1.0 / Math.sqrt(2.0);

    public MCTNode(State state) {
        this.state = state;
        children = new ArrayList<>();
        possibleActions = state.getActionList();
    }

    public State getState() {
        return state;
    }

    public boolean isTerminal() {
        return state.isDone();
    }

    public int getResult() {
        return state.getWinner();
    }

    public boolean isFullyExpanded() {
        return possibleActions.isEmpty();
    }

    public void update(boolean won) {
        winCount += won ? 1 : 0;
        visitCount++;
        double wC = this.winCount;
        double vC = this.visitCount;
        this.currentValue = wC / vC + C * Math.sqrt(2 * Math.log(visitCount) / vC);
        MCTNode best = null;
        double value = 0;
        for (MCTNode m : children) {
            if (best == null || m.currentValue > value) {
                value = m.currentValue;
                best = m;
            }
        }
        bestSuccessor = best;
    }

    /**
     * Expands this node with a random action on a copy of this state and returns the child
     *
     * @return the new child
     */
    public MCTNode expand() {
        assert !possibleActions.isEmpty();
        action = possibleActions.remove(RANDOM.nextInt(possibleActions.size()));
        MCTNode child = new MCTNode(this.state.step(action));
        child.parent = this;
        this.children.add(child);
        return child;
    }

    public MCTNode getBestSuccessor() {
        return bestSuccessor;
    }

    public MCTNode getParent() {
        return parent;
    }

    public int getAction() {
        return action;
    }
}
