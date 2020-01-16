package at.pwd.mcst;

public class MCTEdge {
    private MCTNode in;
    private MCTNode out;
    private int playerTurn;

    private static final float CPUCT = 1;

    private int action;

    public int visitedCount; // number of visits
    public float totalValue; // total Value of next state
    public float meanValue; // mean value of the next state
    public float prior; // the probability of selecting the action that lead to this state

    public MCTEdge(MCTNode in, MCTNode out, int playerTurn, int action) {
        this.in = in;
        this.out = out;
        this.playerTurn = playerTurn;
        this.action = action;
    }

    public MCTNode getIn() {
        return in;
    }

    public MCTNode getOut() {
        return out;
    }

    public double getExplorationRate(int edgeVisitCountSum) {
        return meanValue + CPUCT * prior *
                (Math.sqrt(edgeVisitCountSum) /
                        (1 + visitedCount));
    }

    public int getVisitedCount() {
        return visitedCount;
    }

    public float getTotalValue() {
        return totalValue;
    }

    public float getMeanValue() {
        return meanValue;
    }

    public float getPrior() {
        return prior;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public int getAction() {
        return action;
    }

    public void update(float value) {
        visitedCount++;
        totalValue += value;
        meanValue = totalValue / visitedCount;
    }
}
