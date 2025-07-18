package game.mecanique;

import game.model.Coord;
import game.model.Grille;
import game.model.Wall;

public class TestLDV {
    public static void main(String[] args) {
        Grille g = new Grille(10, 10);

        // Ajouter quelques obstacles
        g.addEntity(new Wall(true), new Coord(4, 3));
        g.addEntity(new Wall(true), new Coord(5, 4));
        g.addEntity(new Wall(true), new Coord(5, 5));
        g.addEntity(new Wall(true), new Coord(5, 6));
        Coord pos1 = new Coord(3, 5);
        Coord pos2 = new Coord(6, 2);
        Coord pos3 = new Coord(6, 3);
        Coord pos4 = new Coord(3, 4);
        // Vérifier la ligne de vue
        System.out.println("Ligne de vue entre (0, 0) et (9, 9) : " + g.ligneDeVue(pos1,pos2));
        System.out.println("Ligne de vue entre (6, 5) et (2, 5) : " + g.ligneDeVue(pos3,pos4));
    }
}
