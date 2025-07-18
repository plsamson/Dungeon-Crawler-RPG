package game.mecanique;

import java.util.List;

import game.model.Coord;
import game.model.Grille;
import game.model.Wall;

public class TestAStar {
    public static void main(String[] args) {
        Grille grille = new Grille(10, 10);
        AStar aStar = new AStar(grille);

        grille.addEntity(new Wall(true), new Coord(5, 4));
        grille.addEntity(new Wall(true), new Coord(5, 5));
        grille.addEntity(new Wall(true), new Coord(5, 3));
        grille.addEntity(new Wall(true), new Coord(3, 4));
        grille.addEntity(new Wall(true), new Coord(4, 3));
        grille.addEntity(new Wall(true), new Coord(4, 6));

        Coord start = new Coord(-1, 0);
        Coord goal = new Coord(7, 7);

        List<Coord> path = aStar.findPath(start, goal);

        printGrid(grille, path, start, goal);
        if (path != null) {
            for (Coord coord : path) {
                System.out.println("(" + coord.getX() + ", " + coord.getY() + ")");
            }
        } else {
            System.out.println("No path found.");
        }
    }

    private static void printGrid(Grille grille, List<Coord> path, Coord start, Coord goal) {
        for (int y = 0; y < grille.getY(); y++) {
            for (int x = 0; x < grille.getX(); x++) {
                Coord currentCoord = new Coord(x, y);

                if (currentCoord.equals(start)) {
                    System.out.print("S "); // Start
                } else if (currentCoord.equals(goal)) {
                    System.out.print("G "); // Goal
                } else if (grille.getEntityAtPos(currentCoord) != null) {
                    System.out.print("X "); // Wall
                } else if (path != null && path.contains(currentCoord)) {
                    System.out.print("* "); // Path
                } else {
                    System.out.print(". "); // Empty space
                }
            }
            System.out.println();
        }
    }
}
