package at.pwd.agent;

import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgent;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;
import at.pwd.game.State;
import at.pwd.mcst.MCT;

public class DeepBeanAgent implements MancalaAgent {
    private MCT tree;

    private static String[] idMap;

    static {
        idMap = new String[]{"14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};
    }

    public DeepBeanAgent() {
        tree = new MCT();
    }

    @Override
    public MancalaAgentAction doTurn(int computationTime, MancalaGame mancalaGame) {
        long start = System.currentTimeMillis();
        long timeInMillis = computationTime * 950L;
        State state = mapToState(mancalaGame);
        int action = act(state, start, timeInMillis);
        System.out.println("DeepBeanAgent returning action " + action);
        return new MancalaAgentAction(idMap[action]);
    }

    private State mapToState(MancalaGame mancalaGame) {
        int[] board = new int[14];
        for (int i = 0; i < board.length; i++) {
            board[i] = mancalaGame.getState().stonesIn(idMap[i]);
        }
        return new State(board, mancalaGame.getState().getCurrentPlayer());
    }

    public int act(State state, long start, long timeInMillis) {
        tree.changeRootTo(state);
        int simulate = 0;
        while (System.currentTimeMillis() - start < timeInMillis) {
            tree.simulate();
            simulate++;
        }
        System.out.println("simulated "+simulate);
        return tree.finishMove();
    }

    @Override
    public String toString() {
        return "DeepBeanAgent";
    }


}
