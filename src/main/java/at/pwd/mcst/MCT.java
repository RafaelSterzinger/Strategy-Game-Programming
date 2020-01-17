package at.pwd.mcst;

import at.pwd.game.State;
import at.pwd.model.Model;

import java.util.*;

public class MCT {
    private MCTNode root;
    private Map<String, MCTNode> nodes;
    private Model model;

    public MCT(Model model, Map<String, MCTNode> nodes) {
        this.nodes = nodes;
        this.model = model;
    }

    public MCT() {
        this(new Model(), new HashMap<>());
    }

    public void changeRootTo(State state) {
        String id = state.getId();
        root = nodes.get(id);
        if (root == null) {
            root = new MCTNode(state);
            nodes.put(id, root);
        }
    }

    public void simulate() {
        List<MCTEdge> path = new LinkedList<>();
        MCTNode leaf = getBestLeaf(root, path);
        float value = expandAndEvaluate(leaf);
        backup(path, value);
    }

    private MCTNode getBestLeaf(MCTNode node, List<MCTEdge> path) {
        while (!node.isLeaf() && !node.isTerminal()) {
            int edgesVisitCountSum = node.getEdgesVisitCountSum();
            Iterator<MCTEdge> it = node.iterator();
// TODO Check calculation
            MCTEdge bestEdge = it.next();
            double maxUpperConfidenceBound = bestEdge.getMeanValue() + bestEdge.getExplorationRate(edgesVisitCountSum);

            while (it.hasNext()) {
                // TODO: add dirichlet noise if root node
                // Variant of the Upper Confidence bounds applied to Trees (PUCT) algorithm:
                MCTEdge edge = it.next();
                double upperConfidenceBound = edge.getMeanValue() + edge.getExplorationRate(edgesVisitCountSum);
                if (upperConfidenceBound > maxUpperConfidenceBound) {
                    maxUpperConfidenceBound = upperConfidenceBound;
                    bestEdge = edge;
                }
            }
            path.add(bestEdge);
            node = bestEdge.getOut();
        }
        return node;
    }

    private float expandAndEvaluate(MCTNode leaf) {
        // TODO check if new edge should be added to path
        float value;
        if (!leaf.isTerminal()) {
            model.predict(leaf.getState());
            leaf.expand(nodes, model.getPolicy());
            value = model.getValue();
        } else {
            // TODO check if that is nice
            int winner = leaf.getResult();
            assert winner != State.UNDEFINED_ID;
            if (winner == State.NOBODY_ID) {
                value = 0.0f;
            } else {
                value = 1.0f;
            }
        }
        return value;
    }

    private void backup(List<MCTEdge> path, float value) {
        for (MCTEdge edge : path) {
            int playerTurn = edge.getPlayerTurn();
            edge.update(playerTurn == root.getState().getPlayerTurn() ? value : value * -1);
        }
    }


    public int finishMove() {
        float best = -9999;
        int action = 0;
        for (MCTEdge edge : root) {
            if (best < edge.getMeanValue()) {
                best = edge.getMeanValue();
                action = edge.getAction();
            }
        }
        return action;
    }
}
