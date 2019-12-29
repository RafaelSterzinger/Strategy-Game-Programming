package at.pwd.agent;


import at.pwd.boardgame.game.base.WinState;
import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.MancalaState;
import at.pwd.boardgame.game.mancala.agent.MancalaAgent;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rfischer on 18/04/2017.
 */
public class TestAgent implements MancalaAgent {
    private Random r = new Random();
    private MancalaState originalState;
    private static final double C = 1.0f / Math.sqrt(2.0f);

    private int counter = 10;

    private class MCTSTree {
        private int visitCount;
        private int winCount;

        private MancalaGame game;
        private WinState winState;
        private MCTSTree parent;
        private List<MCTSTree> children;
        String action;

        public MCTSTree(MancalaGame game) {
            this.game = game;
            this.children = new ArrayList<>();
            this.winState = game.checkIfPlayerWins();
        }

        public boolean isNonTerminal() {
            return winState.getState() == WinState.States.NOBODY;
        }

        public MCTSTree getBestNode() {
            MCTSTree best = null;
            double value = 0;
            for (MCTSTree m : children) {
                double wC = (double) m.winCount;
                double vC = (double) m.visitCount;
                double currentValue = wC / vC + C * Math.sqrt(2 * Math.log(visitCount) / vC);

                if (best == null || currentValue > value) {
                    value = currentValue;
                    best = m;
                }
            }

            return best;
        }

        public boolean isFullyExpanded() {
            return children.size() == game.getSelectableSlots().size();
        }

        public MCTSTree move(String action) {
            MancalaGame newGame = new MancalaGame(this.game);
            if (!newGame.selectSlot(action)) {
                newGame.nextPlayer();
            }

            MCTSTree tree = new MCTSTree(newGame);
            tree.action = action;
            tree.parent = this;

            this.children.add(tree);

            return tree;
        }
    }

    private int i = 0;

    @Override
    public MancalaAgentAction doTurn(int computationTime, MancalaGame game) {
        long start = System.currentTimeMillis();
        this.originalState = game.getState();

        MCTSTree root = new MCTSTree(game);

        System.out.println("computation time " + computationTime);

        while ((System.currentTimeMillis() - start) < (computationTime * 1000 - 100)) {
            MCTSTree best = treePolicy(root);
            WinState winning = defaultPolicy(best.game);
            backup(best, winning);
        }

        MCTSTree selected = root.getBestNode();
        System.out.println("Selected action " + selected.winCount + " / " + selected.visitCount);
        System.out.println("called doTurn " + ++i);
        return new MancalaAgentAction(selected.action);
    }

    private void backup(MCTSTree current, WinState winState) {
        boolean hasWon = winState.getState() == WinState.States.SOMEONE && winState.getPlayerId() == originalState.getCurrentPlayer();

        while (current != null) {
            // always increase visit count
            current.visitCount++;

            // if it ended in a win => increase the win count
            current.winCount += hasWon ? 1 : 0;

            current = current.parent;
        }
    }

    private MCTSTree treePolicy(MCTSTree current) {
        while (current.isNonTerminal()) {
            if (!current.isFullyExpanded()) {
                return expand(current);
            } else {
                current = current.getBestNode();
            }
        }
        return current;
    }

    private MCTSTree expand(MCTSTree best) {
        List<String> legalMoves = best.game.getSelectableSlots();

        //remove already expanded moves
        for (MCTSTree move : best.children) {
            legalMoves.remove(move.action);
        }

        return best.move(legalMoves.get(r.nextInt(legalMoves.size())));
    }

    private WinState defaultPolicy(MancalaGame game) {
        game = new MancalaGame(game); // copy original game
        WinState state = game.checkIfPlayerWins();

        while (state.getState() == WinState.States.NOBODY) {
            String play;
            do {
                List<String> legalMoves = game.getSelectableSlots();
                play = legalMoves.get(r.nextInt(legalMoves.size()));
            } while (game.selectSlot(play));
            game.nextPlayer();

            state = game.checkIfPlayerWins();
        }

        return state;
    }

    @Override
    public String toString() {
        return "Deep Bean";
    }
}
