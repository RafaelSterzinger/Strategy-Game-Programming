package at.pwd.agent;

import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgent;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;
import at.pwd.mcst.MCT;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;

public class DeepBeanAgent implements MancalaAgent {
    private MCT tree = new MCT();

    @Override
    public MancalaAgentAction doTurn(int computationTime, MancalaGame mancalaGame) {
        return tree.mctSearch(mancalaGame, computationTime*1000-100);
    }

    @Override
    public String toString() {
        return "DeepBeanAgent";
    }
}
