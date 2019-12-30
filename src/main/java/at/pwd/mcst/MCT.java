package at.pwd.mcst;

import at.pwd.boardgame.game.base.WinState;
import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;

import java.util.List;
import java.util.Random;

public class MCT {
    private MCTNode root;

    private static final Random RANDOM = new Random();

    public MancalaAgentAction mctSearch(MancalaGame game, int timeInMillis) {
        root = new MCTNode(game);
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeInMillis) {
            MCTNode expansion = treePolicy(root);
            WinState delta = defaultPolicy(expansion.getState());
            backup(expansion, delta);
        }
        root = root.getBestSuccessor();
        return new MancalaAgentAction(root.getAction());
    }

    public MCTNode expand(MCTNode node) {
        return node.randomMove();
    }

    public MCTNode treePolicy(MCTNode node) {
        while (!node.isTerminal()) {
            if (node.isFullyExpanded()) {
                node = node.getBestSuccessor();
            } else {
                return expand(node);
            }
        }
        return node;
    }

    public WinState defaultPolicy(MancalaGame state) {
        state = new MancalaGame(state); // copy original game
        WinState result = state.checkIfPlayerWins();

        while (result.getState() == WinState.States.NOBODY) {
            String play;
            do {
                List<String> legalMoves = state.getSelectableSlots();
                play = legalMoves.get(RANDOM.nextInt(legalMoves.size()));
            } while (state.selectSlot(play));
            state.nextPlayer();
            result = state.checkIfPlayerWins();
        }
        return result;
    }

    private void backup(MCTNode current, WinState winState) {
        boolean won = winState.getState() == WinState.States.SOMEONE &&
                winState.getPlayerId() == this.root.getState().getState().getCurrentPlayer();
        while (current != null) {
            // always increase visit count
            current.update(won);
            current = current.getParent();
        }
    }
}
