package de.uniks.stp24.service;

import java.util.ArrayList;
import java.util.List;

/*
Generic class made for tree to handle variables
 */

public class VariablesTree<T> {
    public final Node<T> root;

    public VariablesTree(String key) {
        this.root = new Node<>(key);
    }

    public Node<T> getRoot() {
        return root;
    }

    /*
    Inner class for Nodes
     */
    public static class Node<T> {
        private final String key;
        private T value;
        private final List<Node<T>> children;

        /*
        Constructor for children
         */
        public Node(String key) {
            this.key = key;
            this.children = new ArrayList<>();
        }

        /*
        Constructor for leaf nodes
         */
        public Node(String key, T value) {
            this.key = key;
            this.value = value;
            this.children = new ArrayList<>();
        }

        public String getKey() {
            return key;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public List<Node<T>> getChildren() {
            return children;
        }

        public void addChild(Node<T> child) {
            children.add(child);
        }
    }

    /*
    Method that search a certain node and returns their children
     */

    public Node<T> getNode(String startNodeKey, String endNodeKey){
        Node<T> startNode = recSearch(startNodeKey, root);
        if (startNode != null) {
            return recSearch(endNodeKey, startNode);
        }
        return null;
    }

    private Node<T> recSearch(String nodeKey, Node<T> currentNodePos){
        if(currentNodePos == null) return null;
        if(currentNodePos.getKey().equals(nodeKey)) return currentNodePos;

        if(checkChildrenFirst(currentNodePos.getChildren(), nodeKey) != null){
            return checkChildrenFirst(currentNodePos.getChildren(), nodeKey);
        }
        for(Node<T> child: currentNodePos.getChildren()){
            Node<T> result = recSearch(nodeKey, child);
            if (result != null) return result;
        }
        return null;
    }

    private Node<T> checkChildrenFirst(List<VariablesTree. Node<T>> children, String nodeKey){
        for(Node<T> child: children) {
            if(child.getKey().equals(nodeKey)) return child;
        }
        return null;
    }

}
