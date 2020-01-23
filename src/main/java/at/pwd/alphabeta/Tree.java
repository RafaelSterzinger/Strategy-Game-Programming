package at.pwd.alphabeta;

import at.pwd.game.State;

import java.util.List;

public class Tree {
    private final Node root;
    private long start, time;
    private float oldAlpha;
    private int bestAction;
    private int depth;

    public Tree(State state) {
        root = new Node( state, -1);
    }

    public int search(int depth, long start, long time) {
        this.start = start;
        this.time = time;
        this.oldAlpha = -Float.MAX_VALUE;
        this.depth = depth;
        this.bestAction = -1;
        float value = alphaBeta(root, depth, -Float.MAX_VALUE, Float.MAX_VALUE);
        alphaBeta(root, depth, -Float.MAX_VALUE, Float.MAX_VALUE);
        System.out.printf("finished search with value %f\n",value);
        return bestAction;
    }

    private float alphaBeta(Node currentNode, int depth, float alpha, float beta) {
        State state = currentNode.getState();
        if (depth == 0 || state.isDone() || System.currentTimeMillis() - start > time) {
            float value = state.getValue();
            currentNode.setValue(value);
            return value;
        }
        List<Node> children = currentNode.expand();
        if (root.getState().getPlayerTurn() == state.getPlayerTurn()) {
            for (Node child : children) {
                alpha = Math.max(alpha, alphaBeta(child, depth - 1, alpha, beta));
                if(depth == this.depth && (oldAlpha < alpha || bestAction ==-1)) {
                    oldAlpha = alpha;
                    bestAction = child.getAction();
                }
                if (beta <= alpha) {
                    break;
                }
            }
            currentNode.setValue(alpha);
            children.sort((n1, n2) -> Float.compare(n1.getValue(), n2.getValue())*-1);
            return alpha;
        } else {
            for (Node child : children) {
                beta = Math.min(beta, alphaBeta(child, depth - 1, alpha, beta));
                if (beta <= alpha) {
                    break;
                }
            }
            currentNode.setValue(beta);
            children.sort((n1, n2) -> Float.compare(n1.getValue(), n2.getValue()*-1));
            return beta;
        }
    }

    public float getOldAlpha() {
        return oldAlpha;
    }
}
