package at.pwd.alphabeta;

import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.game.State;
import at.pwd.model.Model;

import java.util.*;

public class Tree {
    private State root;
    private long start;
    private long time;
    private int DEPTH;
    private Integer currentBest;

    public Tree(State state, long start, long time) {
        root = state;
        this.start = start;
        this.time = time;
    }

    public int searchMove(int depth) {
        currentBest = null;
        Thread thread = new Thread(()->alphaBeta(root, DEPTH = depth, -Float.MAX_VALUE, Float.MAX_VALUE, true));
        return currentBest;
    }

    private float alphaBeta(State state, int depth, float alpha, float beta, boolean maximizingPlayer) {
        if (depth == 0 || state.isDone()) {
            return state.getValue();
        }

        List<Integer> actions = state.getActionList();
        for (Integer action : actions) {
            State nextState = state.step(action);
            boolean moveAgain = state.getPlayerTurn() == nextState.getPlayerTurn();
            if (maximizingPlayer) {
                float oldAlpha = alpha;
                alpha = Math.max(alpha, alphaBeta(nextState, depth - 1, alpha, beta, moveAgain));
                if (depth == DEPTH && (oldAlpha < alpha || currentBest == null)) {
                    currentBest = action;
                }
            } else {
                beta = Math.min(beta, alphaBeta(nextState, depth - 1, alpha, beta, !moveAgain));
            }

            if (beta <= alpha) {
                break;
            }
        }
        return maximizingPlayer ? alpha : beta;
    }
}
