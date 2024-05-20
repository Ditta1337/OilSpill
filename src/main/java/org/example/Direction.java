package org.example;

public enum Direction {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW;

    public double[] toUnitVector() {
        double diag = 1 / Math.sqrt(2);
        return switch(this) {
            case N -> new double[]{1, 0};
            case NE -> new double[]{diag, diag};
            case E -> new double[]{0, 1};
            case SE -> new double[]{-diag, diag};
            case S -> new double[]{-1, 0};
            case SW -> new double[]{-diag, -diag};
            case W -> new double[]{0, -1};
            case NW -> new double[]{diag, -diag};
        };
    }

    public Direction[] getOverlappingDirections() {
        return switch (this) {
            case N -> new Direction[]{N};
            case NE -> new Direction[]{NE, N, E};
            case E -> new Direction[]{E};
            case SE -> new Direction[]{SE, E, S};
            case S -> new Direction[]{S};
            case SW -> new Direction[]{SW, S, W};
            case W -> new Direction[]{W};
            case NW -> new Direction[]{NW, W, N};
        };
    }

    public static Direction[] getEdges() {
        return new Direction[]{N, E, S, W};
    }

    public static Direction[] getCorners() {
        return new Direction[]{NE, SE, SW, NW};
    }
}
