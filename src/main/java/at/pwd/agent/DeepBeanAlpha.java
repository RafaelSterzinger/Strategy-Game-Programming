package at.pwd.agent;

import at.pwd.alphabeta.Tree;
import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgent;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;
import at.pwd.game.State;

public class DeepBeanAlpha implements MancalaAgent {
    private static final String[] idMapWhite;
    private static final String[] idMapBlack;
    private String[] idMap;
    private Tree tree;

    static {
        idMapWhite = new String[]{"14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};
        idMapBlack = new String[]{"7", "6", "5", "4", "3", "2", "1", "14", "13", "12", "11", "10", "9", "8"};
    }

    @Override
    public MancalaAgentAction doTurn(int computationTime, MancalaGame mancalaGame) {
        long tik = System.currentTimeMillis();
        computationTime *= 700L;

        idMap = mancalaGame.getState().getCurrentPlayer() == 0 ? idMapWhite : idMapBlack;
        State state = mapToState(mancalaGame);

        if(tree == null) {
            tree = new Tree(state);
        } else {
            tree.changeRoot(state);
        }

        int depth = 3;
        tree.search(depth, tik, computationTime);

        long timeSpent = System.currentTimeMillis() - tik;
        while (timeSpent < computationTime) {
            System.out.printf("Depth %d time %d of %d\n",depth, timeSpent, computationTime);
            tree.search(++depth, tik, computationTime);
            timeSpent = System.currentTimeMillis() - tik;
        }

        int action = tree.getActionAndChangeRoot();
        long tok = System.currentTimeMillis();
        System.out.println("Algorithm took " + (tok - tik) + " milliseconds");
        return new MancalaAgentAction(idMap[action]);
    }

    private State mapToState(MancalaGame mancalaGame) {
        int[] board = new int[14];
        for (int i = 0; i < board.length; i++) {
            board[i] = mancalaGame.getState().stonesIn(idMap[i]);
        }
        return new State(board, 0);
    }
}
