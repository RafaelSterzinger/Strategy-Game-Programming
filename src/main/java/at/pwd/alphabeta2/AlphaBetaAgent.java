package at.pwd.alphabeta2;

import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgent;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;
import at.pwd.game.State;

import java.util.List;

public class AlphaBetaAgent implements MancalaAgent {
    private String mapId;
    // private Node root = new Node(new State(), 0);
    private List<Node> leaves;

    private static String[] idMap;
    static {
        idMap = new String[]{"14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};
    }

    @Override
    public MancalaAgentAction doTurn(int computationTime, MancalaGame game) {
        // long start = System.currentTimeMillis();
        // long totalTime = computationTime * 990;
        // State state = mapToState(game);
        // root = root.getNode(state);
        // while (System.currentTimeMillis() - start < totalTime) {
        //     root.expand(14);
        //     // root.getBestAction();
        //     break;
        // }
        return null;
    }

    private State mapToState(MancalaGame mancalaGame) {
        int[] board = new int[14];
        for (int i = 0; i < board.length; i++) {
            board[i] = mancalaGame.getState().stonesIn(idMap[i]);
        }
        return new State(board, mancalaGame.getState().getCurrentPlayer());
    }
}
