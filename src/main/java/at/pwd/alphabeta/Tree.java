package at.pwd.alphabeta;

import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.game.State;
import at.pwd.model.Model;

import java.util.*;

public class Tree {
    private Node root;

    public Tree(State state) {
        root = new Node(null, state, -1);
    }

    public int searchMove(int depth) {
        alphaBeta(root, depth, -Float.MAX_VALUE, Float.MAX_VALUE);
        float best = -Float.MAX_VALUE;
        int action = -1;
        for (Node child:root.getChildren()) {
            float value = child.getValue();
            if(best < value || action == -1) {
                best = value;
                action = child.getAction();
            }
        }
        return action;
    }

    private float alphaBeta(Node currentNode, int depth, float alpha, float beta) {
        State state = currentNode.getState();
        if (depth == 0 || state.isDone()) {
            float value = state.getValue();
            currentNode.setValue(value);
            return value;
        }
        List<Node> children = currentNode.expand();
        if(root.getState().getPlayerTurn() == state.getPlayerTurn()) {
            for (Node child : children) {
                alpha = Math.max(alpha, alphaBeta(child, depth - 1, alpha, beta));
                currentNode.setValue(alpha);
                if(beta <= alpha) {
                    break;
                }
            }
            children.sort((n1, n2) -> Float.compare(n1.getValue(), n2.getValue()) * -1);
            return alpha;
        } else {
            for (Node child : children) {
                beta = Math.min(beta, alphaBeta(child, depth - 1, alpha, beta));
                currentNode.setValue(beta);
                if(beta <= alpha) {
                    break;
                }
            }
            children.sort((n1, n2) -> Float.compare(n1.getValue(), n2.getValue()));
            return beta;
        }
    }

    public void changeRoot(State state) {
        root = root.getNode(state);
    }
}
