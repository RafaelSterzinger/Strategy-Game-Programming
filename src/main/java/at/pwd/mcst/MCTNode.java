package at.pwd.mcst;

import at.pwd.boardgame.game.base.WinState;
import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MCTNode {
    private static final Random RANDOM = new Random();

    private MancalaGame state;
    private MCTNode parent;
    private String action;

    private List<MCTNode> children;
    private MCTNode bestSuccessor;

    private int winCount;
    private int visitCount;

    private final boolean terminal;
    private WinState result;

    private List<String> possibleActions;

    private double currentValue;

    private static final double C = 1.0 / Math.sqrt(2.0);

    public MCTNode(MancalaGame state) {
        this.state = state;
        children = new ArrayList<>();
        WinState result = state.checkIfPlayerWins();
        this.terminal = result.getState() != WinState.States.NOBODY;
        this.result = state.checkIfPlayerWins();
        this.possibleActions = state.getSelectableSlots();
    }

    public MancalaGame getState() {
        return state;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public WinState getResult() {
        return result;
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
        String action = possibleActions.remove(RANDOM.nextInt(possibleActions.size()));
        MancalaGame newState = new MancalaGame(this.state);
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
