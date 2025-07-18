package game;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import game.model.Coord;

public class TestCoord {
    
    @Test
    public void createNewCoord(){
        Coord test = new Coord(5, 10);
        assertEquals(5, test.getX());
        assertEquals(10, test.getY());
    }

    @Test
    public void moveUpDates(){
        Coord start = new Coord(0, 0);
        Coord end = new Coord(10, 10);
        start.move(end);
        assertEquals(end, start);
    }

    @Test
    public void equalsTrue(){
        Coord c1 = new Coord(0, 0);
        Coord c2 = new Coord(0, 0);
        assertEquals(true, c1.equals(c2));
    }

    @Test
    public void equalsFasle(){
        Coord c1 = new Coord(0, 0);
        Coord c2 = new Coord(1, 0);
        assertEquals(false, c1.equals(c2));
    }
}
