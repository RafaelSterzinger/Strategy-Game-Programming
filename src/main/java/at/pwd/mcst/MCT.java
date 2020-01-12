package at.pwd.mcst;

import at.pwd.game.State;
import at.pwd.model.Model;

import java.util.*;

public class MCT {
    private MCTNode root;
    private static final Random RANDOM = new Random();
    private Map<String, MCTNode> nodes;
    private Model model;

    public MCT(Model model, MCTNode root, Map<String, MCTNode> nodes) {
        this.root = root;
        this.nodes = nodes;
        this.model = model;
    }

    public MCT() {
        this(new Model(), new MCTNode(new State()), new HashMap<>());
    }

    public void simulate() {
        List<MCTEdge> path = new LinkedList<>();
        MCTNode leaf = getBestLeaf(root, path);
        MCTNode node = expand(leaf);
        backup(leaf, winner);
    }

    private MCTNode getBestLeaf(MCTNode node, List<MCTEdge> path) {
        while (!node.isFullyExpanded()) {
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

    private MCTNode expand(MCTNode node) {
        // TODO check if new edge should be added to path

    }

    private void backup() {

    }


    public MCTNode treePolicy(MCTNode node) {
        while (!node.isTerminal()) {
            if (node.isFullyExpanded()) {
                node = node.getBestSuccessor();
            } else {

                return node.expand();
            }
        }
        return node;
    }


    public int defaultPolicy(State state) {
        // copy original game
        state = new State(state);

        // play until done and return winner id
        while (state.isNotDone()) {
            List<Integer> legalMoves = state.getActionList();
            int play = legalMoves.get(RANDOM.nextInt(legalMoves.size()));
            state.update(play);
        }
        return state.getWinner();
    }

    private void backup(MCTNode current, int winnerId) {
        assert current != null;
        boolean won = winnerId == this.root.getState().getPlayerTurn();
        do {
            // always increase visit count
            current.update(won);
            current = current.getParent();
        } while (current != null);
    }

    public void changeRootTo(State state) {
        Stack<MCTNode> stack = new Stack<>();
        stack.addAll(root.getChildren());
        MCTNode result = null;
        do {
            MCTNode currentNode = stack.pop();
            if (currentNode.getState().equals(state)) {
                result = currentNode;
                break;
            }
            if (currentNode.getState().getPlayerTurn() != root.getState().getPlayerTurn()) {
                stack.addAll(currentNode.getChildren());
            }
        } while (!stack.isEmpty());
        root = result == null ? new MCTNode(state) : result;
    }

    public int finishMove() {
        root = root.getBestSuccessor();
        return root.getAction();
    }
}
