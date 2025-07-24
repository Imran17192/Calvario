package de.uniks.stp24.utils;

import java.util.ArrayList;

/**
 * Simple data structure to save travel paths between islands for future computation
 * @apiNote I tried to reduce the size of this class, for now it takes 150 B on average.
 * My computations resulted in the following observation: 10 thousand instances are going to weight 1 MB on average,
 * which is not that much considering the fact that the player will only traverse between his claimed islands
 * and some of the enemy islands. <p>
 * The most critical number of instances ranges from 125 thousand in 50-islands match to 8 million in the 200-islands match.
 * With that in mind, path storage will take a space in a range from 14 MB to almost 1 GB (which is unlikely).
 */
public class PathEntry {
    private final ArrayList<String> path;
    private final short distance;

    public PathEntry(ArrayList<String> path, int distance) {
        this.path = path;
        this.distance = (short) distance;
    }

    public ArrayList<String> getPathFromLocation(String islandID) {
        if (islandID.equals(this.path.getFirst())) return path;
        if (islandID.equals(this.path.getLast())) return new ArrayList<>(path.reversed());
        return null;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PathEntry other)
            return  (this.path.getFirst().equals(other.path.getFirst()) && (this.path.getLast().equals(other.path.getLast()))
                    || (this.path.getFirst().equals(other.path.getLast()) && (this.path.getLast().equals(other.path.getFirst()))));
        return false;
    }

    public boolean equals(String islandID1, String islandID2) {
        return  (this.path.getFirst().equals(islandID1) && (this.path.getLast().equals(islandID2))
                || (this.path.getFirst().equals(islandID2) && (this.path.getLast().equals(islandID1))));

    }

    @Override
    public String toString() {
        return String.format("[PATH ENTRY]: (%s -> %s)\tPath: %s\tDistance: %d",
                this.path.getFirst(), this.path.getLast(), path, distance);
    }
}
