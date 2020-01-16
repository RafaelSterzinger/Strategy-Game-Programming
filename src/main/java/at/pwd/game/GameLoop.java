package at.pwd.game;

import at.pwd.agent.DeepBeanAgent;
import at.pwd.model.Model;
import at.pwd.agent.lowerBound.MancalaAlphaBetaAgent;
import at.pwd.boardgame.game.agent.AgentAction;
import at.pwd.boardgame.game.base.WinState;
import at.pwd.boardgame.game.mancala.MancalaGame;
import at.pwd.boardgame.game.mancala.agent.MancalaAgentAction;

import java.io.IOException;


public class GameLoop {

    public static void main(String[] args) throws IOException {
        State state = new State();

        Model model = new Model();

        MancalaGame mancalaGame = new MancalaGame();

        DeepBeanAgent deepBeanAgent = new DeepBeanAgent();
        int deepBeanScore = 0;

        MancalaAlphaBetaAgent alphaBetaAgent = new MancalaAlphaBetaAgent();
        int alphaBetaScore = 0;

        boolean deepBeanStarted = true;
        boolean deepBeanTurn = true;
        int startingPlayer = mancalaGame.getState().getCurrentPlayer();

        while (true) {
            MancalaAgentAction action;
            if (deepBeanTurn) {
                action = deepBeanAgent.doTurn(100, mancalaGame);
            } else {
                action = alphaBetaAgent.doTurn(100, mancalaGame);
            }
            if (action.applyAction(mancalaGame) == AgentAction.NextAction.NEXT_PLAYER) {
                deepBeanTurn = !deepBeanTurn;
            }
            WinState winState = mancalaGame.checkIfPlayerWins();
            if (winState.getState() == WinState.States.SOMEONE) {
                if (mancalaGame.getState().getCurrentPlayer() == startingPlayer) {
                    deepBeanScore++;
                } else {
                    alphaBetaScore++;
                }
                deepBeanStarted = !deepBeanStarted;
                deepBeanTurn = deepBeanStarted;
                System.out.printf("Deep Bean Score: %d \t Alpha Beta Score: %d\n", deepBeanScore, alphaBetaScore);
            }
        }
    }


}
