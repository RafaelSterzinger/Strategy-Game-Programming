package at.pwd.agent;

import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgent;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;
import at.pwd.mcst.MCT;
import at.pwd.model.Model;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;

public class DeepBeanAgent implements MancalaAgent {
    private MCT tree;
    private Model model;

    public DeepBeanAgent(Model model, MCT tree) {
        this.tree = tree;
        this.model = model;
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
        return tree.mctSearch(mancalaGame, computationTime*1000-100);
    }

    @Override
    public String toString() {
        return "DeepBeanAgent";
    }
}
