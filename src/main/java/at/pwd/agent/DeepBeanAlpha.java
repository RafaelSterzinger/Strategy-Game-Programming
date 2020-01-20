package at.pwd.agent;

import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgent;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;
import at.pwd.mcst.MCT;

public class DeepBeanAlpha implements MancalaAgent {
    private MCT tree;

    private static String[] idMap;

    static {
        idMap = new String[]{"14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"};
    }

    @Override
    public MancalaAgentAction doTurn(int i, MancalaGame mancalaGame) {
        return null;
    }




}
