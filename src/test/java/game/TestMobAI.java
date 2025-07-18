package game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import game.mecanique.MobAI;
import game.model.Character;
import game.model.Coord;
import game.model.Grille;
import game.model.Spell;

public class TestMobAI {

    @Test
    public void spellChosenFullHp() {
        MobAI mob = new MobAI(false);
        Spell heal = new Spell("heal", "water", 4, -10,0);
        Spell waterball = new Spell("waterball", "water", 2, 6,1);
        ArrayList<Spell> spellTest = new ArrayList<Spell>();
        spellTest.add(waterball);
        spellTest.add(heal);
        Character test = new Character("Test", 6, 50, 0, 0, 0, 0, 0, 0, spellTest);
        Character testTarget = new Character("Target", 0, 50, 0, 0, 0, 0, 0, 0, spellTest);
        Coord posTest = new Coord(0, 0);
        Coord posTarget = new Coord(1, 0);
        Grille grilleTest = new Grille(2,2);
        ArrayList<Character> targetList = new ArrayList<Character>();
        targetList.add(testTarget);
        test.setCurrentPos(posTest);
        testTarget.setCurrentPos(posTarget);
        grilleTest.addEntity(testTarget, posTarget);
        grilleTest.addEntity(test, posTest);
        assertEquals("waterball", mob.spellChosen(test,grilleTest,targetList));
    }

    @Test
    public void spellChosenHeal() {
        MobAI mob = new MobAI(false);
        Spell heal = new Spell("heal", "water", 4, -10,0);
        ArrayList<Spell> spellTest = new ArrayList<Spell>();
        spellTest.add(heal);
        Character test = new Character("Test", 6, 50, 0, 0, 0, 0, 0, 0, spellTest);
        Character testTarget = new Character("Target", 0, 50, 0, 0, 0, 0, 0, 0, spellTest);
        Coord posTest = new Coord(0, 0);
        Coord posTarget = new Coord(1, 0);
        Grille grilleTest = new Grille(2,2);
        ArrayList<Character> targetList = new ArrayList<Character>();
        targetList.add(testTarget);
        test.setCurrentPos(posTest);
        testTarget.setCurrentPos(posTarget);
        grilleTest.addEntity(testTarget, posTarget);
        grilleTest.addEntity(test, posTest);
        test.setCurrentHp(25);
        assertEquals("heal", mob.spellChosen(test,grilleTest,targetList));
    }

    @Test
    public void choseTargetSelf() {
        Spell heal = new Spell("heal", "water", 0, -10,0);
        MobAI mob = new MobAI(false);
        assertEquals("self", mob.choseTarget(heal));
    }

    @Test
    public void choseTargetEnemy() {
        Spell enemy = new Spell("enemy", null, 0, 10,0);
        MobAI mob = new MobAI(false);
        assertEquals("enemy", mob.choseTarget(enemy));
    }

    @Test
    public void moveToTargetMelee() {
        MobAI mob = new MobAI(false);
        Spell heal = new Spell("heal", "water", 4, -10,0);
        Spell waterball = new Spell("waterball", "water", 2, 6,1);
        ArrayList<Spell> spellTest = new ArrayList<Spell>();
        spellTest.add(waterball);
        spellTest.add(heal);
        Character test = new Character("Test", 6, 50, 2, 0, 0, 0, 0, 0, spellTest);
        Character testTarget = new Character("Target", 0, 50, 0, 0, 0, 0, 0, 0, spellTest);
        Coord posTest = new Coord(0, 0);
        Coord posTarget = new Coord(0, 2);
        Grille grilleTest = new Grille(1,3);
        ArrayList<Character> targetList = new ArrayList<Character>();
        targetList.add(testTarget);
        test.setCurrentPos(posTest);
        testTarget.setCurrentPos(posTarget);
        grilleTest.addEntity(testTarget, posTarget);
        grilleTest.addEntity(test, posTest);
        assertEquals("movement 0,1", mob.spellChosen(test,grilleTest,targetList));
    }

    @Test
    public void hitEnemyRange(){
        MobAI mob = new MobAI(true);
        Spell heal = new Spell("heal", "water", 4, -10,0);
        Spell waterball = new Spell("waterball", "water", 2, 6,3);
        ArrayList<Spell> spellTest = new ArrayList<Spell>();
        spellTest.add(waterball);
        spellTest.add(heal);
        Character test = new Character("Test", 6, 50, 0, 0, 0, 0, 0, 0, spellTest);
        Character testTarget = new Character("Target", 0, 50, 0, 0, 0, 0, 0, 0, spellTest);
        Coord posTest = new Coord(0, 0);
        Coord posTarget = new Coord(0, 3);
        Grille grilleTest = new Grille(1,4);
        ArrayList<Character> targetList = new ArrayList<Character>();
        targetList.add(testTarget);
        test.setCurrentPos(posTest);
        testTarget.setCurrentPos(posTarget);
        grilleTest.addEntity(testTarget, posTarget);
        grilleTest.addEntity(test, posTest);
        assertEquals("waterball", mob.spellChosen(test,grilleTest,targetList));
    }

    @Test
    public void rangeMoveToRandomePos(){
        MobAI mob = new MobAI(true);
        Spell heal = new Spell("heal", "water", 4, -10,0);
        Spell waterball = new Spell("waterball", "water", 2, 6,3);
        ArrayList<Spell> spellTest = new ArrayList<Spell>();
        spellTest.add(waterball);
        spellTest.add(heal);
        Character test = new Character("Test", 0, 50, 2, 0, 0, 0, 0, 0, spellTest);
        Character testTarget = new Character("Target", 0, 50, 0, 0, 0, 0, 0, 0, spellTest);
        Coord posTest = new Coord(3, 3);
        Coord posTarget = new Coord(5, 5);
        Grille grilleTest = new Grille(6,6);
        ArrayList<Character> targetList = new ArrayList<Character>();
        targetList.add(testTarget);
        test.setCurrentPos(posTest);
        testTarget.setCurrentPos(posTarget);
        grilleTest.addEntity(testTarget, posTarget);
        grilleTest.addEntity(test, posTest);
        assertNotEquals("waterBall",mob.spellChosen(test, grilleTest, targetList));
        assertNotEquals("heal",mob.spellChosen(test, grilleTest, targetList));
        assertNotEquals("end",mob.spellChosen(test, grilleTest, targetList));
    }
}
