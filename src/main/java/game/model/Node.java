package game.model;

import java.util.Objects;

public class Node implements Comparable<Node> {
    private Coord coord;
    private int gCost;
    private int hCost;
    public Node parent;

    public Node(Coord coord, int gCost, int hCost, Node parent) {
        this.coord = coord;
        this.gCost = gCost;
        this.hCost = hCost;
        this.parent = parent;
    }

    public Coord getCoord() {
        return coord;
    }

    public int getFCost() {
        return gCost + hCost;
    }

    public int getGCost() {
        return gCost;
    }

    public void setGCost(int gCost) {
        this.gCost = gCost;
    }

    public int getHCost() {
        return hCost;
    }

    public Node getParent() {
        return parent;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.getFCost(), other.getFCost());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return coord.equals(node.coord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coord);
    }
}
