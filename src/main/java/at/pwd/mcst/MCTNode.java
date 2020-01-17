package at.pwd.mcst;

import at.pwd.game.State;

import java.util.*;

public class MCTNode implements Iterable<MCTEdge> {
    private State state;
    private List<MCTEdge> edges;
    private boolean leaf;
    private static final double C = 1.0 / Math.sqrt(2.0);

    public MCTNode(State state) {
        this.state = state;
        this.edges = new ArrayList<>();
        leaf = true;
    }

    public State getState() {
        return state;
    }

    public boolean isTerminal() {
        return state.isDone();
    }

    public int getResult() {
        return state.getWinner();
    }

    public boolean isLeaf() {
        return leaf;
    }

    @Override
    public Iterator<MCTEdge> iterator() {
        return edges.iterator();
    }

    /**
     * Expands this node
     */
    public void expand(Map<String, MCTNode> nodes, float[] probs) {
        assert leaf;
        boolean[] mask = state.getActionMask();
        for (int i = 0; i < probs.length; i++) {
            if (mask[i]) {
                int action =state.getPlayerTurn() == State.WHITE_ID ? i : i + 7;
                State newState = this.state.step(action);
                // TODO optimize id
                String id = newState.getId();
                MCTNode child = nodes.get(id);
                if (child == null) {
                    child = new MCTNode(newState);
                    nodes.put(id, child);
                }
                MCTEdge edge = new MCTEdge(this, child, probs[i], action);
                edges.add(edge);
            }
        }
        leaf = false;
    }

    public int getEdgesVisitCountSum() {
        return edges.stream().mapToInt(MCTEdge::getVisitedCount).sum();
    }

    public List<MCTEdge> getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        return state.getId();
    }
}
