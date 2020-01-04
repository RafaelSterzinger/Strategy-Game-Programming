package at.pwd.game;

import java.util.Arrays;
import java.util.Scanner;

public class PlayerVsPlayerTest {
    public static void main(String[] args) {
        State state = new State();
        Scanner scanni = new Scanner(System.in);
        while (!state.isDone()) {
            System.out.println(state);
            System.out.printf("Type in one of the actions player %d: %s\n", state.getPlayerTurn(), Arrays.toString(state.getActions()));
            System.out.flush();
            int action = scanni.nextInt();
            System.out.println();
            state.step(action);
        }
        System.out.println(state);
        System.out.printf("Player %d won!\n", state.getPlayerTurn());
    }
}
