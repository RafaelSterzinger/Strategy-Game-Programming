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
    private Model model;
    private boolean initialized;

    private String[] idMap;

    public DeepBeanAgent(Model model, MCT tree) {
        this.tree = tree;
        this.model = model;
        initialized = false;
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
        return new MancalaAgentAction("" + action);
    }

    private State mapToState(MancalaGame mancalaGame) {
        if (idMap == null) {
            List<String> ids = new ArrayList<>();
            addIdsToList(ids, mancalaGame);
            mancalaGame.nextPlayer();
            addIdsToList(ids, mancalaGame);
            idMap = (String[]) ids.toArray();
            assert idMap.length == 14;
        }
        int[] board = new int[14];
        for (int i = 0; i < board.length; i++) {
            board[i] = mancalaGame.getState().stonesIn(idMap[i]);
        }
        return new State(board);
    }

    private void addIdsToList(List<String> ids, MancalaGame mancalaGame) {
        List<String> slotIds = mancalaGame.getSelectableSlots();
        String depositId = mancalaGame.getBoard().getDepotOfPlayer(mancalaGame.getState().getCurrentPlayer());
        slotIds.add(depositId);
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
