package at.pwd.alphabeta;

import at.pwd.game.State;
import at.pwd.model.Model;

import java.util.*;

public class Tree {
    private State root;

    public Tree(State state) {
        root = state;
    }

    public int searchMove(int depth) {
        return alphaBeta(root, depth, -Float.MAX_VALUE, Float.MAX_VALUE, true).getMove();
    }

    private Node alphaBeta(State state, int depth, float alpha, float beta, boolean maximizingPlayer) {
        Node node = null;
        if (depth == 0 || state.isDone()) {
            node = new Node();
            node.setValue(state.getValue());
            return node;
        }

        List<Integer> actions = state.getActionList();
        if (maximizingPlayer) {
            for (Integer action : actions) {
                State nextState = state.step(action);
                node = alphaBeta(nextState, depth - 1, alpha, beta, state.getPlayerTurn() == nextState.getPlayerTurn());
                node.setMove(action);
                alpha = Math.max(alpha, node.getValue());
                if (alpha > beta) {
                    break;
                }
            }
            return node;
        } else {
            for (Integer action : actions) {
                State nextState = state.step(action);
                node = alphaBeta(nextState, depth - 1, alpha, beta, !(state.getPlayerTurn() == nextState.getPlayerTurn()));
                node.setMove(action);
                beta = Math.min(beta, node.getValue());
                if (alpha > beta) {
                    break;
                }
            }
            return node;
        }
    }
}
