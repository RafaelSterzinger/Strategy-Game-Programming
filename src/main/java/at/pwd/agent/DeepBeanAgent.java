package at.pwd.agent;

import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgent;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;
import at.pwd.game.State;
import at.pwd.mcst.MCT;
import at.pwd.model.Model;

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
        return null;
    }

    @Override
    public String toString() {
        return "DeepBeanAgent";
    }

    public int act(State state, int turns) {
        tree.changeRootTo(state);

        while (System.currentTimeMillis() - start < timeInMillis) {
            tree.simulate();
        }

        return tree.finishMove();
    }


}
