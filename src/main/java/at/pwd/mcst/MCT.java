package at.pwd.mcst;

import at.pwd.game.State;

import java.util.List;
import java.util.Random;

public class MCT {
    private MCTNode root;

    private static final Random RANDOM = new Random();

    public int mctSearch(State state, int timeInMillis) {
        root = new MCTNode(state);
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeInMillis) {
            MCTNode expansion = treePolicy(root);
            int delta = defaultPolicy(expansion.getState());
            backup(expansion, delta);
        }
        root = root.getBestSuccessor();

        // Should not modify state
        assert state.equals(root.getState());

        return root.getAction();
    }

    public MCTNode treePolicy(MCTNode node) {
        while (!node.isTerminal()) {
            if (node.isFullyExpanded()) {
                node = node.getBestSuccessor();
            } else {
                return node.expand();
            }
        }
        return node;
    }

    public int defaultPolicy(State state) {
        // copy original game
        state = new State(state);

        // play until done and return winner id
        while (state.isNotDone()) {
            List<Integer> legalMoves = state.getActionList();
            int play = legalMoves.get(RANDOM.nextInt(legalMoves.size()));
            state.update(play);
        }
        return state.getWinner();
    }

    private void backup(MCTNode current, int winnerId) {
        boolean won = winnerId == this.root.getState().getPlayerTurn();
        while (current != null) {
            // always increase visit count
            current.update(won);
            current = current.getParent();
        }
    }
}
