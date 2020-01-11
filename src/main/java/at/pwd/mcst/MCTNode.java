package at.pwd.mcst;

import at.pwd.boardgame.game.base.WinState;
import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;
import at.pwd.game.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MCTNode {
    private static final Random RANDOM = new Random();

    private State state;
    private MCTNode parent;
    private String action;

    private List<MCTNode> children;
    private MCTNode bestSuccessor;

    private int winCount;
    private int visitCount;

    private List<String> possibleActions;

    private double currentValue;

    private static final double C = 1.0 / Math.sqrt(2.0);

    public MCTNode(State state) {
        this.state = state;
        children = new ArrayList<>();
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

    public MCTNode randomMove() {
        List<Integer> moves = state.getActionList();
        State
        if (!newState.selectSlot(action)) {
            newState.nextPlayer();
        }
        MCTNode node = new MCTNode(newState);
        node.action = action;
        node.parent = this;
        this.children.add(node);
        return node;
    }

    public MCTNode getBestSuccessor() {
        return bestSuccessor;
    }

    public MCTNode getParent() {
        return parent;
    }

    public String getAction() {
        return action;
    }
}
