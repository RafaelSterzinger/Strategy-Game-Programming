package at.pwd.mcst;

public class MCTEdge {
    private MCTNode in;
    private MCTNode out;

    private static final float CPUCT = 1;

    private int action;

    public int visitedCount; // number of visits
    public float totalValue; // total Value of next state
    public float meanValue; // mean value of the next state
    public float moveProbability; // the probability of selecting the action that lead to this state

    public MCTEdge(MCTNode in, MCTNode out, float moveProbability, int action) {
        this.in = in;
        this.out = out;
        this.moveProbability = moveProbability;
        this.action = action;
    }

    public MCTNode getIn() {
        return in;
    }

    public MCTNode getOut() {
        return out;
    }

    public double getExplorationRate(int edgeVisitCountSum) {
        return CPUCT * moveProbability * ((Math.sqrt(edgeVisitCountSum) / (1 + visitedCount)));
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

    public float getMoveProbability() {
        return moveProbability;
    }

    public int getAction() {
        return action;
    }

    public void update(float value) {
        visitedCount++;
        totalValue += value;
        meanValue = totalValue / visitedCount;
    }

    public int getPlayerTurn() {
        return in.getState().getPlayerTurn();
    }
}
