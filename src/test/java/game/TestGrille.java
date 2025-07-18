package game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import game.model.Character;
import game.model.Coord;
import game.model.Grille;
import game.model.Wall;

public class TestGrille {

    @Test
    public void createGrilleProperly() {
        Grille test = new Grille(10, 20);
        assertEquals(10, test.getX());
        assertEquals(20, test.getY());
    }

    @Test
    public void addEntityGetEntity() {
        Grille test = new Grille(10, 10);
        Wall entity = new Wall(true);
        test.addEntity(entity, new Coord(5, 5));
        assertEquals(entity, test.getEntityAtPos(new Coord(5, 5)));
    }

    @Test
    public void distance() {
        Grille test = new Grille(11, 11);
        Coord start = new Coord(0, 0);
        Coord end = new Coord(10, 10);
        assertEquals(20, test.dist(start, end));
    }

    @Test
    public void obstructedLigneOfSight() {
        Grille test = new Grille(10, 10);
        Wall blocks = new Wall(true);
        test.addEntity(blocks, new Coord(5, 5));
        Coord start = new Coord(5, 4);
        Coord end = new Coord(5, 6);
        assertEquals(false, test.ligneDeVue(start, end));
    }

    @Test
    public void notObstructedLigneOfSightWithWall() {
        Grille test = new Grille(10, 10);
        Wall blocks = new Wall(false);
        test.addEntity(blocks, new Coord(5, 5));
        Coord start = new Coord(5, 4);
        Coord end = new Coord(5, 6);
        assertEquals(true, test.ligneDeVue(start, end));
    }

    @Test
    public void notObstructedLigneOfSightWithoutWall() {
        Grille test = new Grille(10, 10);
        Coord start = new Coord(5, 4);
        Coord end = new Coord(5, 6);
        assertEquals(true, test.ligneDeVue(start, end));
    }

    @Test
    public void emptyAtPos(){
        Grille test = new Grille(10,10);
        assertEquals(true, test.isEmpty(new Coord(5, 5)));
    }

    @Test
    public void notEmptyAtPos(){
        Grille test = new Grille(10,10);
        test.addEntity(new Wall(true), new Coord(5, 5));
        assertEquals(false, test.isEmpty(new Coord(5, 5)));
    }

    @Test
    public void notEmptyCharacterAtPos(){
        Grille test = new Grille(10,10);
        test.addEntity(new Character(null, 0, 0, 0, 0, 0, 0, 0, 0, null), new Coord(5, 5));
        assertEquals(false, test.isEmptyOpponant(new Coord(5, 5)));
    }
}
