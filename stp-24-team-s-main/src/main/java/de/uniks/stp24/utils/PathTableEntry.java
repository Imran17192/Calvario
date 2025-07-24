package de.uniks.stp24.utils;

import java.util.Map;

public class PathTableEntry {
    private final Map<String, Integer> connections;
    private final String id;
    private int shortestPath;
    private String previousNode;

    public PathTableEntry(String id, Map<String, Integer> connections) {
        this.id = id;
        this.connections = connections;
        this.shortestPath = -1;
        this.previousNode = "";
    }

    public String getID() {
        return id;
    }

    public Map<String, Integer> getConnections() {
        return connections;
    }

    public int getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(int shortestPath) {
        this.shortestPath = shortestPath;
    }

    public String getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(String previousNode) {
        this.previousNode = previousNode;
    }

    @Override
    public String toString() {
        return String.format("[X] Entry %s:\t Path: %d\t Previous: %s", id, shortestPath, previousNode);
    }
}

