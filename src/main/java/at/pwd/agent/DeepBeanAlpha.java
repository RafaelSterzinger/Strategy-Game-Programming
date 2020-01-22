package at.pwd.agent;

import at.pwd.alphabeta.Tree;
import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgent;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;
import at.pwd.game.State;

public class DeepBeanAlpha implements MancalaAgent {
    private static String[] idMapWhite;
    private static String[] idMapBlack;
    private String[] idMap;

    static {
        idMapWhite = new String[]{"14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};
        idMapBlack = new String[]{"7", "6", "5", "4", "3", "2", "1", "14", "13", "12", "11", "10", "9", "8"};
    }

    @Override
    public MancalaAgentAction doTurn(int computationTime, MancalaGame mancalaGame) {
        long tik = System.currentTimeMillis();
        long timeInMillis = computationTime * 600L;

        idMap = mancalaGame.getState().getCurrentPlayer() == 0 ? idMapWhite : idMapBlack;

        State state = mapToState(mancalaGame);
        Tree tree = new Tree(state);
        // if (tree == null) {
        //     tree = new Tree(state);
        // } else {
        //     tree.changeRoot(state);
        // }
        int depth = 3;
        long currentTime = System.currentTimeMillis() - tik;
        int action = -1;
        int lastAction = action;
        while (currentTime < timeInMillis) {
            System.out.printf("Depth %d time %d of %d\n", depth, currentTime, timeInMillis);
            lastAction = action;
            action = tree.search(++depth, tik, timeInMillis);
            currentTime = System.currentTimeMillis() - tik;
        }
        action = lastAction;
        long tok = System.currentTimeMillis();
        MancalaAgentAction mancalaAction = new MancalaAgentAction(idMap[action]);
        System.out.println("Algorithm took " + (tok - tik) + " milliseconds, Action " + action + " was chosen");
        return mancalaAction;
    }

    private State mapToState(MancalaGame mancalaGame) {
        int[] board = new int[14];
        for (int i = 0; i < board.length; i++) {
            board[i] = mancalaGame.getState().stonesIn(idMap[i]);
        }
        return new State(board, 0);
    }
}
