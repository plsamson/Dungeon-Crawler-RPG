package game.mecanique;

import game.model.Coord;
import game.model.Grille;
import game.model.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class AStar {

    private Grille grille;

    public AStar(Grille grille) {
        this.grille = grille;
    }

    public List<Coord> findPath(Coord start, Coord goal) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<Coord, Node> allNodes = new HashMap<>();

        Node startNode = new Node(start, 0, heuristic(start, goal), null);
        openSet.add(startNode);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            if (currentNode.getCoord().equals(goal)) {
                return reconstructPath(currentNode);
            }

            for (Coord neighbor : getNeighbors(currentNode.getCoord())) {
                int tentativeGCost = currentNode.getGCost() + 1; // Assuming uniform cost for moving to a neighbor
                Node neighborNode = allNodes.getOrDefault(neighbor, new Node(neighbor, Integer.MAX_VALUE, heuristic(neighbor, goal), null));

                if (tentativeGCost < neighborNode.getGCost()) {
                    neighborNode.setGCost(tentativeGCost);
                    neighborNode.parent = currentNode;
                    allNodes.put(neighbor, neighborNode);

                    if (!openSet.contains(neighborNode)) {
                        openSet.add(neighborNode);
                    }
                }
            }
        }

        return null; // No path found
    }

    private List<Coord> reconstructPath(Node currentNode) {
        List<Coord> path = new ArrayList<>();
        while (currentNode != null) {
            path.add(currentNode.getCoord());
            currentNode = currentNode.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    private int heuristic(Coord a, Coord b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    private List<Coord> getNeighbors(Coord coord) {
        List<Coord> neighbors = new ArrayList<>();
        int x = coord.getX();
        int y = coord.getY();

        if (x - 1 >= 0 && grille.isEmpty(new Coord(x - 1, y))) neighbors.add(new Coord(x - 1, y));
        if (x + 1 < grille.getX() && grille.isEmpty(new Coord(x + 1, y))) neighbors.add(new Coord(x + 1, y));
        if (y - 1 >= 0 && grille.isEmpty(new Coord(x, y - 1))) neighbors.add(new Coord(x, y - 1));
        if (y + 1 < grille.getY() && grille.isEmpty(new Coord(x, y + 1))) neighbors.add(new Coord(x, y + 1));

        return neighbors;
    }
}