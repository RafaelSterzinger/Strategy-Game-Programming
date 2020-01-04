package at.pwd.game;

import at.pwd.agent.DeepBeanAgent;
import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.services.GameFactory;
import at.pwd.model.Model;

public class SelfPlay {
    private void playMatches(DeepBeanAgent[] players, int episodes) {
        State currentState = new State();
        for (int i = 0; i < episodes; i++) {
            currentState.reset(true);
            while (!currentState.isDone()) {

            }
        }
    }

    public void run() {
        Model model = new Model();
        DeepBeanAgent bestActor = new DeepBeanAgent(model);
        DeepBeanAgent currentActor = new DeepBeanAgent();

        int iteration = 0;

        while (true) {
            iteration++;
            System.out.println("---------");
            System.out.println("Self play");

        }
    }
}
