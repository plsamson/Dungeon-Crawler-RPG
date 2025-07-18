package game.mecanique;

import java.util.ArrayList;

import game.model.Character;
import game.model.Coord;
import game.model.Grille;
import game.model.Spell;

public class TestCombat {
    public static void main(String[] args) {
        Spell fireball = new Spell("fireball", "fire", 3, 9,2);
        Spell waterball = new Spell("waterball", "water", 2, 6,2);
        Spell heal = new Spell("heal", "water", 4, -10,0);
        Spell windstrike = new Spell("windstrike", "wind", 3, 9,4);
        Spell earthspear = new Spell("earthspear", "earth", 6, 18,4);
        Spell weapon = new Spell("weapon", "neutral", 4, 12,1);
        ArrayList<Spell> spellJ1 = new ArrayList<Spell>();
        spellJ1.add(fireball);
        spellJ1.add(heal);
        spellJ1.add(weapon);
        spellJ1.add(earthspear);
        ArrayList<Spell> spellJ2 = new ArrayList<Spell>();
        spellJ2.add(heal);
        spellJ2.add(waterball);
        spellJ2.add(windstrike);
        spellJ2.add(weapon);
        Character j1 = new Character("Joueur1", 6, 30, 3, 5, 5, 5, 0, 5, spellJ1);
        Character j2 = new Character("Joueur2", 6, 30, 6, 0, 5, 0, 5, 5, spellJ2);
        ArrayList<Character> player = new ArrayList<Character>();
        ArrayList<Character> mob = new ArrayList<Character>();
        player.add(j1);
        mob.add(j2);
        
        Grille testG = new Grille(10, 10);
        //Object murTest = new Object();
        //Coord posMurTest = new Coord(5, 3);
        Coord posJ1 = new Coord(4, 4);
        Coord posJ2 = new Coord(4, 0);

        j1.setCurrentPos(posJ1);
        j2.setCurrentPos(posJ2);

        MobAI mobai = new MobAI(true);
        String spellChosen = mobai.spellChosen(j2, testG, player);

        System.out.println(spellChosen);
        System.out.println(spellChosen.charAt(9)-'0');
        System.out.println(spellChosen.charAt(11)-'0');

        /*System.out.println(testG.isEmpty(posMurTest));
        testG.addEntity(murTest, posMurTest);
        System.out.println(testG.getEntityAtPos(posMurTest).toString());
        System.out.println(testG.isEmpty(posMurTest));
        testG.addEntity(j1, j1.getCurrentPos());
        System.out.println(testG.getEntityAtPos(posJ1).toString());*/
    }
}
