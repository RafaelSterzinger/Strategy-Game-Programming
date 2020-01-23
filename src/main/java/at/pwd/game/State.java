package at.pwd.game;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class State {
    private final int[] board;

    private int playerTurn;

    private int winner = UNDEFINED_ID;

    public static final int UNDEFINED_ID = -2;
    public static final int NOBODY_ID = -1;
    public static final int WHITE_ID = 0;
    public static final int BLACK_ID = 1;

    public static final int WHITE_KALAHA = 6;
    public static final int BLACK_KALAHA = 13;

    private static final int[] MAP_OPPOSITE_WHITE = new int[]{12, 11, 10, 9, 8, 7, -1, -1, -1, -1, -1, -1, -1, -1};
    private static final int[] MAP_OPPOSITE_BLACK = new int[]{-1, -1, -1, -1, -1, -1, -1, 5, 4, 3, 2, 1, 0, -1};

    public State(int[] board, int playerTurn) {
        assert board.length == 14;
        this.board = board;
        this.playerTurn = playerTurn;
    }

    public State(State state) {
        this.board = Arrays.copyOf(state.board, state.board.length);
        playerTurn = state.playerTurn;
        winner = state.winner;
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
        // To check side
        int start;
        int stop;
        if (playerTurn == State.WHITE_ID) {
            ownKalaha = WHITE_KALAHA;
            oppKalaha = BLACK_KALAHA;
            opponentId = BLACK_ID;
            mapOpposite = MAP_OPPOSITE_WHITE;
            start = 0;
            stop = ownKalaha;
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
                board[ownKalaha] += oppositeValue + 1;
                board[index] = 0;
                board[oppositeIndex] = 0;
                int sum = 0;
                for (int i = start; i < stop; i++) {
                    sum += board[mapOpposite[i]];
                }
                if (sum == 0) {
                    determineWinner(ownKalaha);
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

    private void determineWinner(int kalaha) {
        for (int i = 0; i < WHITE_KALAHA; i++) {
            board[kalaha] += board[i];
            board[i] = 0;
        }
        for (int i = WHITE_KALAHA + 1; i < BLACK_KALAHA; i++) {
            board[kalaha] += board[i];
            board[i] = 0;
        }
        if (board[WHITE_KALAHA] < board[BLACK_KALAHA]) {
            winner = BLACK_ID;
        } else if (board[WHITE_KALAHA] > board[BLACK_KALAHA]) {
            winner = WHITE_ID;
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

    public boolean isDone() {
        return winner != UNDEFINED_ID;
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

    public String getId() {
        return String.format("%d: %s", playerTurn, Arrays.toString(board));
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

    public float getValue() {
        if (board[WHITE_KALAHA] > 36) {
            return Float.MAX_VALUE;
        } else if (board[BLACK_KALAHA] > 36) {
            return -Float.MAX_VALUE;
        }

        // float score;

        // int ownStones = getStones(0);
        // int oppStones = getStones(WHITE_KALAHA + 1);

        // score = ((board[WHITE_KALAHA] - board[BLACK_KALAHA]) * 1.5f + (ownStones - oppStones));

        return board[WHITE_KALAHA] - board[BLACK_KALAHA];
    }

    private int getStones(int fromBowl) {
        int sum = 0;
        for (int i = fromBowl; i < fromBowl + 6; i++) {
            sum += board[i];
        }
        return sum;
    }
}
