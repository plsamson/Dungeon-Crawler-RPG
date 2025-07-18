package game;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import game.model.Character;
import game.model.Spell;

public class TestCharacter {

    @Test
    public void getSpell() {
        Spell fireball = new Spell("fireball", "fire", 3, 9,2);
        Spell waterball = new Spell("waterball", "water", 2, 6,2);
        Spell heal = new Spell("heal", "water", 4, -10,0);
        Spell windstrike = new Spell("windstrike", "wind", 3, 9,1);
        Spell earthspear = new Spell("earthspear", "earth", 6, 18,5);
        Spell weapon = new Spell("weapon", "neutral", 4, 12,1);
        ArrayList<Spell> test = new ArrayList<Spell>();
        test.add(weapon);
        test.add(heal);
        test.add(fireball);
        test.add(waterball);
        test.add(windstrike);
        test.add(earthspear);
        Character testC = new Character("testC", 0, 0, 0, 0, 0, 0, 0, 0, test);
        assertEquals("waterball", testC.getSpell("waterball").getName());
        assertEquals("fireball", testC.getSpell("fireball").getName());
        assertEquals("heal", testC.getSpell("heal").getName());
        assertEquals("weapon", testC.getSpell("weapon").getName());
        assertEquals("earthspear", testC.getSpell("earthspear").getName());
        assertEquals("windstrike", testC.getSpell("windstrike").getName());
    }
}
