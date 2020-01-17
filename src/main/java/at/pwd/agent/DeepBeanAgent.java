package at.pwd.agent;

import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgent;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;
import at.pwd.game.State;
import at.pwd.mcst.MCT;
import at.pwd.model.Model;

import java.util.ArrayList;
import java.util.List;

public class DeepBeanAgent implements MancalaAgent {
    private MCT tree;

    private static String[] idMap;

    static {
        idMap = new String[]{"14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};
    }

    public DeepBeanAgent(Model model, MCT tree) {
        this.tree = tree;
    }

    public DeepBeanAgent(Model model) {
        this(model, new MCT());
    }

    public DeepBeanAgent(MCT tree) {
        this(new Model(), tree);
    }

    public DeepBeanAgent() {
        this(new Model(), new MCT());
    }

    @Override
    public MancalaAgentAction doTurn(int computationTime, MancalaGame mancalaGame) {
        long start = System.currentTimeMillis();
        long timeInMillis = computationTime * 950L;
        State state = mapToState(mancalaGame);
        int action = act(state, start, timeInMillis);
        return new MancalaAgentAction("" + idMap[action]);
    }


    private State mapToState(MancalaGame mancalaGame) {
        // createMap(mancalaGame);
        int[] board = new int[14];
        for (int i = 0; i < board.length; i++) {
            board[i] = mancalaGame.getState().stonesIn(idMap[i]);
        }
        return new State(board, mancalaGame.getState().getCurrentPlayer());
    }

    private void createMap(MancalaGame mancalaGame) {
        idMap = new String[14];
        int index = 0;
        index = addIdsToList(index, mancalaGame);
        mancalaGame.nextPlayer();
        addIdsToList(index, mancalaGame);
    }

    private int addIdsToList(int index, MancalaGame mancalaGame) {
        // TODO check for revert
        List<String> slotIds = mancalaGame.getSelectableSlots();
        String depositId = mancalaGame.getBoard().getDepotOfPlayer(mancalaGame.getState().getCurrentPlayer());
        for (String id : slotIds) {
            idMap[index++] = id;
        }
        idMap[index++] = depositId;
        return index;
    }


    public int act(State state, long start, long timeInMillis) {
        tree.changeRootTo(state);
        while (System.currentTimeMillis() - start < timeInMillis) {
            tree.simulate();
        }
        return tree.finishMove();
    }

    @Override
    public String toString() {
        return "DeepBeanAgent";
    }


}
