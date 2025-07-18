package game;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import game.model.Coord;
import game.model.Grille;

public class GrilleTest {
    
    @Test
    public void distancePos(){
        Grille g = new Grille(10,10);
        Coord start = new Coord(2,2);
        Coord end  = new Coord(4, 4);
        assertEquals(4, g.dist(start, end));
    }

    @Test
    public void distanceNeg(){
        Grille g = new Grille(10,10);
        Coord start = new Coord(4,4);
        Coord end  = new Coord(2, 1);
        assertEquals(5, g.dist(start, end));
    }
}
