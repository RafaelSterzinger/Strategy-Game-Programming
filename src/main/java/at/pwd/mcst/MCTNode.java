package at.pwd.mcst;

import at.pwd.game.State;

import java.util.*;

public class MCTNode implements Iterable<MCTEdge> {
    private State state;

    private List<MCTEdge> edges;

    private boolean expanded = false;

    private static final double C = 1.0 / Math.sqrt(2.0);

    public MCTNode(State state) {
        this.state = state;
        this.edges = new ArrayList<>();
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

    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public Iterator<MCTEdge> iterator() {
        return edges.iterator();
    }

    /**
     * Expands this node
     */
    public void expand(Map<String, MCTNode> nodes, float[] probs) {
        assert !expanded;
        boolean[] mask = state.getActionMask();
        for (int action = 0; action < probs.length; action++) {
            if (mask[action]) {
                State newState = this.state.step(action);
                MCTNode child = nodes.get(newState.getId());
                if (child == null) {
                    child = new MCTNode(newState);
                    // TODO optimize id
                    nodes.put(child.getState().getId(), child);
                }
                // TODO find out if player turn is right
                MCTEdge edge = new MCTEdge(this, child, state.getPlayerTurn(), action);
                edges.add(edge);
            }
        }
        expanded = true;
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
