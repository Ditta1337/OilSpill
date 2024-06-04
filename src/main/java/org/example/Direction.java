package org.example;

import java.util.ArrayList;
import java.util.List;

public enum Direction {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW;

    public Vector2D toUnitVector() {
        double diag = 1 / Math.sqrt(2);
        return switch(this) {
            case N -> new Vector2D(0, -1);
            case NE -> new Vector2D(diag, -diag);
            case E -> new Vector2D(1, 0);
            case SE -> new Vector2D(diag, diag);
            case S -> new Vector2D(0, 1);
            case SW -> new Vector2D(-diag, diag);
            case W -> new Vector2D(-1, 0);
            case NW -> new Vector2D(-diag, -diag);
        };
    }

    public static Vector2D[] getNeighborsVectors() {
        List<Vector2D> neighbors = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            neighbors.add(dir.toUnitVector());
        }
        return neighbors.toArray(new Vector2D[0]);
    }

    public static Direction[] getEdges() {
        return new Direction[]{N, E, S, W};
    }

    public static Direction[] getCorners() {
        return new Direction[]{NE, SE, SW, NW};
    }
}
