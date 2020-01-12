package at.pwd.game;

import at.pwd.boardgame.game.mancala.MancalaGame;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class State {
    private int[] board;

    private int playerTurn;

    private int winner = UNDEFINED_ID;

    public static final int UNDEFINED_ID = -1;
    public static final int NOBODY_ID = 0;
    public static final int BLACK_ID = 1;
    public static final int WHITE_ID = 2;

    private static final int WHITE_KALAHA = 6;
    private static final int BLACK_KALAHA = 13;
    private static final int[] MAP_OPPOSITE_WHITE = new int[]{12, 11, 10, 9, 8, 7, -1, -1, -1, -1, -1, -1, -1, -1};
    private static final int[] MAP_OPPOSITE_BLACK = new int[]{-1, -1, -1, -1, -1, -1, -1, 5, 4, 3, 2, 1, 0, -1};

    public State() {
        board = new int[14];
        reset(true);
    }

    public State(State state) {
        this.board = Arrays.copyOf(state.board, state.board.length);
        playerTurn = state.playerTurn;
        winner = state.winner;
    }

    public void reset(boolean randomStart) {
        board[0] = 6;
        board[1] = 6;
        board[2] = 6;
        board[3] = 6;
        board[4] = 6;
        board[5] = 6;
        board[6] = 0;
        board[7] = 6;
        board[8] = 6;
        board[9] = 6;
        board[10] = 6;
        board[11] = 6;
        board[12] = 6;
        board[13] = 0;
        if (randomStart) {
            playerTurn = Math.random() < 0.5 ? WHITE_ID : BLACK_ID;
        } else {
            playerTurn = State.WHITE_ID;
        }
    }

    public State step(int action) {
        State state = new State(this);
        state.update(action);
        return state;
    }

    public void update(int action) {
        assert playerTurn == BLACK_ID || playerTurn == WHITE_ID;
        assert getActionList().contains(action);

        int ownKalaha;
        int oppKalaha;
        int opponentId;
        int[] mapOpposite;
        int start;
        int stop;
        if (playerTurn == State.WHITE_ID) {
            ownKalaha = WHITE_KALAHA;
            oppKalaha = BLACK_KALAHA;
            opponentId = BLACK_ID;
            mapOpposite = MAP_OPPOSITE_WHITE;
            start = 0;
            stop = oppKalaha;
        } else {
            ownKalaha = BLACK_KALAHA;
            oppKalaha = WHITE_KALAHA;
            opponentId = WHITE_ID;
            mapOpposite = MAP_OPPOSITE_BLACK;
            start = oppKalaha + 1;
            stop = ownKalaha;
        }
        int stonesInHand = board[action];
        assert stonesInHand != 0;

        board[action] = 0;
        int index = action;
        while (stonesInHand > 0) {
            index = (index + 1) % 14;
            if (index != oppKalaha) {
                board[index]++;
                stonesInHand--;
            }
        }
        if (index != ownKalaha) {
            int oppositeIndex = mapOpposite[index];
            if (oppositeIndex != -1 && board[index] == 1) {
                int oppositeValue = board[oppositeIndex];
                if (oppositeValue > 0) {
                    board[ownKalaha] += oppositeValue + 1;
                    board[index] = 0;
                    board[oppositeIndex] = 0;
                    int sum = 0;
                    for (int i = start; i < stop; i++) {
                        sum += board[mapOpposite[i]];
                    }
                    if (sum == 0) {
                        determineWinner(oppKalaha);
                    }
                }
            }
            playerTurn = opponentId;
        }
        int sum = 0;
        for (int i = start; i < stop; i++) {
            sum += board[i];
        }
        if (sum == 0) {
            determineWinner(oppKalaha);
        }
    }

    private void determineWinner(int oppKalaha) {
        for (int i = 0; i < board[i]; i++) {
            board[oppKalaha] += board[i];
            board[i] = 0;
        }
        if (board[WHITE_KALAHA] < board[BLACK_KALAHA]) {
            winner = WHITE_ID;
        } else if (board[WHITE_KALAHA] > board[BLACK_KALAHA]) {
            winner = BLACK_ID;
        } else {
            winner = NOBODY_ID;
        }
    }


    public int getPlayerTurn() {
        return playerTurn;
    }

    public List<Integer> getActionList() {
        List<Integer> actions = new LinkedList<>();
        if (playerTurn == WHITE_ID) {
            for (int i = 0; i < 6; i++) {
                if (board[i] > 0) {
                    actions.add(i);
                }
            }
        } else {
            for (int i = 7; i < 13; i++) {
                if (board[i] > 0) {
                    actions.add(i);
                }
            }
        }
        return actions;
    }

    public int[] getActions() {
        List<Integer> actions = getActionList();
        int[] actionArray = new int[actions.size()];
        for (int i = 0; i < actionArray.length; i++) {
            actionArray[i] = actions.remove(0);
        }
        return actionArray;
    }

    private boolean[] getBooleanMask() {
        boolean[] mask = new boolean[6];
        int start = 0;
        if (playerTurn == BLACK_ID) {
            start = 7;
        }
        for (int i = 0; i < mask.length; i++) {
            if (board[start + i] > 0) {
                mask[i] = true;
            }
        }
        return mask;
    }

    public INDArray getMask() {
        return Nd4j.create(getBooleanMask());
    }

    public INDArray getStateForModel() {
        int[][][] array = new int[1][2][14];
        array[0][0] = Arrays.copyOf(board, board.length);
        array[0][1] = Arrays.copyOf(board, board.length);
        for (int i = 0; i < 7; i++) {
            array[0][0][i] = board[i];
            array[0][1][i] = 0;
        }
        for (int i = 7; i < 14; i++) {
            array[0][0][i] = 0;
            array[0][1][i] = board[i];
        }
        return Nd4j.createFromArray(array);
    }

    public boolean isDone() {
        return winner != -1;
    }

    public boolean isNotDone() {
        return winner == -1;
    }

    public int getWinner() {
        return winner;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean[] mask = getBooleanMask();
        if (playerTurn == BLACK_ID) {
            sb.append("  ");
            for (int i = 5; i >= 0; i--) {
                if (mask[i]) {
                    sb.append(String.format(" %2d ", i + 7));
                } else {
                    sb.append("    ");
                }
            }
            sb.append("\n");
        }
        sb.append("----------------------------\n");
        sb.append("  ");
        for (int i = 12; i >= 7; i--) {
            sb.append(String.format(" %2d ", board[i]));
        }
        sb.append("\n");
        sb.append(String.format("%2d", board[13])).append("                        ").append(String.format("%2d", board[6]));
        sb.append("\n");
        sb.append("  ");
        for (int i = 0; i < 6; i++) {
            sb.append(String.format(" %2d ", board[i]));
        }
        sb.append("\n");
        sb.append("----------------------------\n");
        if (playerTurn == WHITE_ID) {
            sb.append("  ");
            for (int i = 0; i < 6; i++) {
                if (mask[i]) {
                    sb.append(String.format(" %2d ", i));
                } else {
                    sb.append("    ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Arrays.equals(board, state.board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }
}
