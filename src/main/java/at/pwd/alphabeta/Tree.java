package at.pwd.alphabeta;

import at.pwd.game.State;
import at.pwd.model.Model;

import java.util.*;

public class Tree {
    private Node root;
    float alpha;
    float beta;

    public Tree(State state) {
        root = new Node(state,0);
    }

    public Node alphaBeta(){

    }

//    public void expand(int depth) {
//
//        //
//        root = root.getChild(action);
//    }
//
//    public void changeRootTo(State state) {
//        Node newRoot = null;
//        for (Node node : root.getChildren()) {
//            if (node.getState().equals(state)) {
//                newRoot = node;
//            }
//        }
//        if (node == null) {
//            root = new Node(state, 0);
//        }
//    }
}
