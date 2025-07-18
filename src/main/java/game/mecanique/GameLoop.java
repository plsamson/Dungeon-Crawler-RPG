package game.mecanique;

import java.util.ArrayList;

import game.model.Character;
import game.model.Coord;
import game.model.Grille;
import game.model.Spell;

public class GameLoop {
    private static boolean running = true;
    private static boolean playerWon = false;
    private static int waveNb = 0;

    public static void start() {
        Thread gameThread = new Thread(() -> gameLoop());
        gameThread.setDaemon(true);
        gameThread.start();
    }

    private static void gameLoop() {
        while (running) {
            runCombat();
            if (playerWon == false) {
                running = false;
            }
            // Set un wait pour start next round?
        }
    }

    //Initialisation des paramètres de combat
    private static void runCombat() {
        playerWon = false;
        Spell fireball = new Spell("fireball", "fire", 3, 9, 3);
        Spell waterball = new Spell("waterball", "water", 2, 6, 2);
        Spell heal = new Spell("heal", "fire", 4, -10, 0);
        Spell windstrike = new Spell("windstrike", "wind", 3, 9, 4);
        Spell earthspear = new Spell("earthspear", "earth", 6, 18, 4);
        Spell weapon = new Spell("weapon", "neutral", 4, 12, 1);
        ArrayList<Spell> spellJ1 = new ArrayList<Spell>();
        spellJ1.add(fireball);
        spellJ1.add(waterball);
        spellJ1.add(earthspear);
        spellJ1.add(windstrike);
        spellJ1.add(heal);
        spellJ1.add(weapon);
        ArrayList<Spell> spellJ2 = new ArrayList<Spell>();
        spellJ2.add(heal);
        spellJ2.add(waterball);
        spellJ2.add(windstrike);
        spellJ2.add(weapon);
        Coord posJ1 = new Coord(0, 0);
        Coord posJ2 = new Coord(23, 8);
        int baseHpJoueur = (int) Math.round(30.00 * (1.00 + (double) waveNb * 20.00 / 100.00));
        int statJoueur = 5 + 5 * waveNb;
        int pa = 6;
        int pm = 4;
        Character j1 = new Character("Aragorn", pa, baseHpJoueur, pm, statJoueur, statJoueur, statJoueur, 0, statJoueur,
                spellJ1);
        Character j2 = new Character("Evil Wizard", pa, baseHpJoueur, pm, 0, statJoueur, 0, statJoueur, statJoueur,
                spellJ2);
        j1.setCurrentPos(posJ1);
        j2.setCurrentPos(posJ2);
        ArrayList<Character> player = new ArrayList<Character>();
        ArrayList<Character> mob = new ArrayList<Character>();
        player.add(j1);
        mob.add(j2);
        MobAI mobAi = new MobAI(true);
        Grille gride = new Grille(24, 9);
        gride = gride.loadGrille(24,9);
        MecaniqueCombat combat = new MecaniqueCombat(gride, mobAi, waveNb);
        playerWon = combat.fight(player, mob);
        waveNb++;
    }
}
