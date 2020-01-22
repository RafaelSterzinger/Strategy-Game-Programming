package at.pwd.mcst;
import at.pwd.game.State;
import at.pwd.model.Model;

import java.util.*;

public class MCT {
    private MCTNode root;
    private final Map<String, MCTNode> nodes;
    private final Model model;

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
            Iterator<MCTEdge> it = node.iterator();
            MCTEdge bestEdge = it.next();

            int sumVisitCount = node.getSumVisitCount();
            double maxUpperConfidenceBound = bestEdge.getQ() + bestEdge.getU(sumVisitCount);

            while (it.hasNext()) {
                // TODO: Add Dirichlet noise if root node
                // Variant of the Upper Confidence bounds applied to Trees (PUCT) algorithm:
                MCTEdge edge = it.next();
                double upperConfidenceBound = edge.getQ() + edge.getU(sumVisitCount);
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
        float value;
        if (!leaf.isTerminal()) {
            model.predict(leaf.getState());
            leaf.expand(nodes, model.getPolicy());
            value = model.getValue();
        } else {
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
            if (best < edge.getN()) {
                best = edge.getN();
                action = edge.getAction();
            }
        }
        return action;
    }
}
