package at.pwd.mcst;

import at.pwd.game.State;
import at.pwd.model.Model;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MCT {
    private MCTNode root;
    private int playerId;
    private static final Random RANDOM = new Random();
    private Map<String, MCTNode> nodes;
    private Model model;

    public MCT(Model model, MCTNode root, Map<String, MCTNode> nodes) {
        this.root = root;
        this.nodes = nodes;
        this.model = model;
        playerId = root.getState().getPlayerTurn();
    }

    public MCT() {
        this(new Model(), new MCTNode(new State()), new HashMap<>());
    }

    public void changeRootTo(State state) {
        Stack<MCTNode> stack = new Stack<>();
        stack.add(root);
        MCTNode result = null;
        do {
            MCTNode currentNode = stack.pop();
            if (currentNode.getState().equals(state)) {
                result = currentNode;
                break;
            }
            if (currentNode.getState().getPlayerTurn() != root.getState().getPlayerTurn()) {
                stack.addAll(root.getEdges().stream().map(MCTEdge::getOut).collect(Collectors.toList()));
            }
        } while (!stack.isEmpty());
        root = result == null ? new MCTNode(state) : result;
    }

    public void simulate() {
        List<MCTEdge> path = new LinkedList<>();
        MCTNode leaf = getBestLeaf(root, path);
        float value = expandAndEvaluate(leaf);
        backup(path, value);
    }

    private MCTNode getBestLeaf(MCTNode node, List<MCTEdge> path) {
        while (node.isExpanded()) {
            int edgesVisitCountSum = node.getEdgesVisitCountSum();
            double maxUpperConfidenceBound = -1234;
            MCTEdge bestEdge = null;
            for (MCTEdge edge : node) {
                // TODO: add dirichlet noise if root node
                // Variant of the Upper Confidence bounds applied to Trees (PUCT) algorithm:
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
            model.fit(leaf.getState());
            leaf.expand(nodes, model.getQuality());
            value = model.getValue();
        } else {
            int winner = leaf.getResult();
            assert winner != State.UNDEFINED_ID;
            if (winner == State.NOBODY_ID) {
                value = 0.5f;
            } else if (winner == root.getState().getPlayerTurn()) {
                value = 1.0f;
            } else {
                value = 0.0f;
            }
        }
        return value;
    }

    private void backup(List<MCTEdge> path, float value) {
        for (MCTEdge edge : path) {
            int playerTurn = edge.getPlayerTurn();
            edge.update(playerTurn == playerId ? value : value * -1);
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
