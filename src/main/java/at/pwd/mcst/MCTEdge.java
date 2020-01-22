package at.pwd.mcst;

public class MCTEdge {
    private final MCTNode in;
    private final MCTNode out;

    private static final float CPUCT = 2;

    private final int action;

    public int visitCount; // number of visits
    public float totalValue; // total Value of next state
    public float meanValue; // mean value of the next state
    public float priorProbability; // the probability of selecting the action that lead to this state

    public MCTEdge(MCTNode in, MCTNode out, float moveProbability, int action) {
        this.in = in;
        this.out = out;
        this.priorProbability = moveProbability;
        this.action = action;
    }

    public MCTNode getIn() {
        return in;
    }

    public MCTNode getOut() {
        return out;
    }

    public double getU(int sumVisitCount) {
        return CPUCT * priorProbability * ((Math.sqrt(sumVisitCount) / (1 + visitCount)));
    }

    public int getN() {
        return visitCount;
    }

    public float getTotalValue() {
        return totalValue;
    }

    public float getQ() {
        return meanValue;
    }

    public float getPriorProbability() {
        return priorProbability;
    }

    public int getAction() {
        return action;
    }

    public void update(float value) {
        // TODO: Add virtual loss
        visitCount++;
        totalValue += value;
        meanValue = totalValue / visitCount;
    }

    public int getPlayerTurn() {
        return in.getState().getPlayerTurn();
    }
}
