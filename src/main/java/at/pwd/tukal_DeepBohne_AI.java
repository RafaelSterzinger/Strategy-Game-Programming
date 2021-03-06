package at.pwd;

import at.pwd.alphabeta.Tree;
import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgent;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;
import at.pwd.game.State;

import java.util.Arrays;

/**
 * DeepBeanAlpha Agent plays always from player 0 (white) perspective
 */
public class tukal_DeepBohne_AI implements MancalaAgent {
    private static final String[] idMapWhite;
    private static final String[] idMapBlack;
    private String[] idMap;

    /**
     * Depending on current player id, we map the MancalaGame accordingly
     */
    static {
        idMapWhite = new String[]{"14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};
        idMapBlack = new String[]{"7", "6", "5", "4", "3", "2", "1", "14", "13", "12", "11", "10", "9", "8"};
    }

    @Override
    public MancalaAgentAction doTurn(int computationTime, MancalaGame mancalaGame) {
        long tik = System.currentTimeMillis();
        long timeInMillis = computationTime * 900L;

        idMap = mancalaGame.getState().getCurrentPlayer() == 0 ? idMapWhite : idMapBlack;

        State state = mapToState(mancalaGame);
        Tree tree = new Tree(state);

        int depth = 3;
        long currentTime = System.currentTimeMillis() - tik;
        int action = -1;
        int lastAction = action;
        while (currentTime < timeInMillis) {
            System.out.printf("Depth %d time %d of %d\n", depth, currentTime, timeInMillis);
            lastAction = action;
            try {
                action = tree.search(++depth, tik, timeInMillis);
            } catch (OutOfMemoryError ex) {
                tree = null;
                System.out.println("Out of memory");
                return new MancalaAgentAction(idMap[action]);
            }
            if (tree.getOldAlpha() == Integer.MAX_VALUE) {
                lastAction = action;
                break;
            }
            currentTime = System.currentTimeMillis() - tik;
        }
        action = lastAction;
        long tok = System.currentTimeMillis();
        System.out.println("Algorithm took " + (tok - tik) + " milliseconds, Action " + action + " was chosen");

        return new MancalaAgentAction(idMap[action]);
    }

    private State mapToState(MancalaGame mancalaGame) {
        int[] board = new int[14];
        for (int i = 0; i < board.length; i++) {
            board[i] = mancalaGame.getState().stonesIn(idMap[i]);
        }
        return new State(board, 0);
    }

    @Override
    public String toString() {
        return "tukal_DeepBohne_AI";
    }
}
