package at.pwd.alphabeta;

import at.pwd.game.State;

import java.util.List;

public class Tree {
    private final Node root;
    private long start, time;
    private int oldAlpha;
    private int bestAction;
    private int depth;

    public Tree(State state) {
        root = new Node(state, -1);
    }

    public int search(int depth, long start, long time) {
        this.start = start;
        this.time = time;
        this.oldAlpha = Integer.MIN_VALUE;
        this.depth = depth;
        this.bestAction = -1;
        int value = alphaBeta(root, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        alphaBeta(root, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.printf("finished search with value %d\n", value);
        return bestAction;
    }

    private int alphaBeta(Node currentNode, int depth, int alpha, int beta) {
        State state = currentNode.getState();
        if (depth == 0 || state.isDone() || System.currentTimeMillis() - start > time) {
            int value = state.getValue();
            currentNode.setValue(value);
            return value;
        }
        List<Node> children = currentNode.expand();
        if (root.getState().getPlayerTurn() == state.getPlayerTurn()) {
            for (Node child : children) {
                alpha = Math.max(alpha, alphaBeta(child, depth - 1, alpha, beta));
                if (depth == this.depth && (oldAlpha < alpha || bestAction == -1)) {
                    oldAlpha = alpha;
                    bestAction = child.getAction();
                }
                if (beta <= alpha) {
                    break;
                }
            }
            currentNode.setValue(alpha);

            //Sort in descending order, when iterative deepening, when can prune more
            children.sort((n1, n2) -> Integer.compare(n1.getValue(), n2.getValue()) * -1);
            return alpha;
        } else {
            for (Node child : children) {
                beta = Math.min(beta, alphaBeta(child, depth - 1, alpha, beta));
                if (beta <= alpha) {
                    break;
                }
            }
            currentNode.setValue(beta);
            //Sort in descending order, when iterative deepening, when can prune more
            children.sort((n1, n2) -> Integer.compare(n1.getValue(), n2.getValue() * -1));
            return beta;
        }
    }

    public int getOldAlpha() {
        return oldAlpha;
    }
}
