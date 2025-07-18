package game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import game.model.Character;
import game.model.Spell;

public class TestSpell {

    @Test
    public void calculateDamageFire() {
        Spell spell = new Spell("spell", "fire", 0, 10,0);
        Character test = new Character("test", 0, 0, 0, 10, 0, 0, 0, 0, null);
        assertEquals(15, spell.calculateDamage(test));
    }

    @Test
    public void calculateDamageWind() {
        Spell spell = new Spell("spell", "wind", 0, 10,0);
        Character test = new Character("test", 0, 0, 0, 0, 0, 0, 10, 0, null);
        assertEquals(15, spell.calculateDamage(test));
    }

    @Test
    public void calculateDamageEarth() {
        Spell spell = new Spell("spell", "earth", 0, 10,0);
        Character test = new Character("test", 0, 0, 0, 0, 0, 10, 0, 0, null);
        assertEquals(15, spell.calculateDamage(test));
    }

    @Test
    public void calculateDamageWater() {
        Spell spell = new Spell("spell", "water", 0, 10,0);
        Character test = new Character("test", 0, 0, 0, 0, 10, 0, 0, 0, null);
        assertEquals(15, spell.calculateDamage(test));
    }

    @Test
    public void calculateDamageNeurtal() {
        Spell spell = new Spell("spell", "neutral", 0, 10,0);
        Character test = new Character("test", 0, 0, 0, 10, 0, 0, 0, 10, null);
        assertEquals(15, spell.calculateDamage(test));
    }

    @Test
    public void calculateDamageWaterHeal() {
        Spell spell = new Spell("spell", "water", 0, -10,0);
        Character test = new Character("test", 0, 0, 0, 0, 10, 0, 0, 0, null);
        assertEquals(-15, spell.calculateDamage(test));
    }
}
