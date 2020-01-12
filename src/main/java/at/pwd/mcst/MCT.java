package at.pwd.mcst;

import at.pwd.game.State;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MCT {
    private MCTNode root;
    private static final Random RANDOM = new Random();

    public int simulate() {
        MCTNode expansion = treePolicy(root);
        int winner = defaultPolicy(expansion.getState());
        backup(expansion, winner);

        root = root.getBestSuccessor();

        // Should not modify state
        //assert state.equals(root.getState());

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
        assert current != null;
        boolean won = winnerId == this.root.getState().getPlayerTurn();
        do {
            // always increase visit count
            current.update(won);
            current = current.getParent();
        } while (current != null);
    }

    public void changeRootTo(State state) {
        Stack<MCTNode> stack = new Stack<>();
        stack.addAll(root.getChildren());
        MCTNode result = null;
        do {
            MCTNode currentNode = stack.pop();
            if (currentNode.getState().equals(state)) {
                result = currentNode;
                break;
            }
            if (currentNode.getState().getPlayerTurn() != root.getState().getPlayerTurn()) {
                stack.addAll(currentNode.getChildren());
            }
        } while (!stack.isEmpty());
        root = result == null ? new MCTNode(state) : result;
    }

    public int finishMove() {
        root = root.getBestSuccessor();
        return root.getAction();
    }
}
