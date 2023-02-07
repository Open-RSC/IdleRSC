package controller;

import java.util.ArrayList;


final class Node {
    static final int WORLD_W = 900;
    static final int WORLD_H = 4050;
    final short x;
    final short y;
    short costFromStartAlongBestKnownPath;
    short totalCostFromStartToGoalThroughY;
    short HEURISTIC_COST_TO_GOAL;
    byte walkable;
    ArrayList<Node> neighbors;
    boolean isEvaluated;
    boolean isTentativeNodeToBeEvaluated;

    Node(int x, int y) {
        this.x = (short) x;
        this.y = (short) y;
        HEURISTIC_COST_TO_GOAL = -1;
    }

    void reset() {
        HEURISTIC_COST_TO_GOAL = -1;
        costFromStartAlongBestKnownPath = 0;
        totalCostFromStartToGoalThroughY = 0;
        isEvaluated = false;
        isTentativeNodeToBeEvaluated = false;
    }

    int distFrom(Node n) {
        final int sx = this.x - n.x;
        if (sx == 0) return 1;
        final int sy = this.y - n.y;
        if (sy == 0) return 1;
        int dx = Math.abs(sx);
        int dy = Math.abs(sy);
        dx *= dx;
        dy *= dy;
        return (int) Math.sqrt(dx + dy);
    }

    int estHeuristicCost(Node n) {
        // manhattan
        if (HEURISTIC_COST_TO_GOAL == -1) {
            HEURISTIC_COST_TO_GOAL = (short) ((2 - walkable) *
                    (Math.abs(this.x - n.x) +
                            Math.abs(this.y - n.y)));
        }
        return HEURISTIC_COST_TO_GOAL;
    }

    public ArrayList<Node> getNeighbors(Node[][] nodes) {
        if (this.neighbors != null) {
            return this.neighbors;
        }

        final boolean allowDiagonal = true;
        final boolean dontCrossCorners = true;

        boolean s0 = false, d0 = false,
                s1 = false, d1 = false,
                s2 = false, d2 = false,
                s3 = false, d3 = false;

        final int x = this.x;
        final int y = this.y;
        Node n;
        final ArrayList<Node> neighbors = new ArrayList<>(0);

        n = getNode(nodes, x, y - 1);
        if (n != null) {
            neighbors.add(n);
            s0 = true;
        }
        n = getNode(nodes, x + 1, y);
        if (n != null) {
            neighbors.add(n);
            s1 = true;
        }
        n = getNode(nodes, x, y + 1);
        if (n != null) {
            neighbors.add(n);
            s2 = true;
        }
        n = getNode(nodes, x - 1, y);
        if (n != null) {
            neighbors.add(n);
            s3 = true;
        }

        if (!allowDiagonal) {
            return neighbors;
        }

        if (dontCrossCorners) {
            d0 = s3 && s0;
            d1 = s0 && s1;
            d2 = s1 && s2;
            d3 = s2 && s3;
        } else {
            d0 = s3 || s0;
            d1 = s0 || s1;
            d2 = s1 || s2;
            d3 = s2 || s3;
        }

        n = getNode(nodes, x - 1, y - 1);
        if (n != null && d0) {
            neighbors.add(n);
        }
        n = getNode(nodes, x + 1, y - 1);
        if (n != null && d1) {
            neighbors.add(n);
        }
        n = getNode(nodes, x + 1, y + 1);
        if (n != null && d2) {
            neighbors.add(n);
        }
        n = getNode(nodes, x - 1, y + 1);
        if (n != null && d3) {
            neighbors.add(n);
        }

        this.neighbors = neighbors;
        return neighbors;
    }


    public static Node getNode(Node[][] nodes, int x, int y) {
        if (x < 0 || x > (WORLD_W - 1)) {
            return null;
        }
        if (y < 0 || y > (WORLD_H - 1)) {
            return null;
        }
        return nodes[x][y];
    }

    public static void resetNodes(Node[][] nodes) {
        for (int x = 0; x < WORLD_W; ++x) {
            for (int y = 0; y < WORLD_H; ++y) {
                Node n = nodes[x][y];
                if (n == null) continue;
                n.reset();
            }
        }
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

}
