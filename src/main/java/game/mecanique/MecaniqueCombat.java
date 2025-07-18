package game.mecanique;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

import game.gui.UiController;
import game.gui.UiInitializer;
import game.model.Character;
import game.model.Coord;
import game.model.Grille;
import game.model.Spell;

public class MecaniqueCombat {
    private int tour;
    private ArrayList<Character> player;
    private ArrayList<Character> mob;
    private MobAI mobAI;
    private UiController controller;
    private String action;
    private static MecaniqueCombat instance;
    private Grille grilleCbt;
    int waveNb;

    private List<Clip> activeClips = new ArrayList<>();

    public MecaniqueCombat(int largeurGrille, int hauteurGrille, MobAI mob) {
        this.tour = 1;
        controller = UiInitializer.controller;
        instance = this;
        this.grilleCbt = new Grille(largeurGrille, hauteurGrille);
        this.mobAI = mob;

        playSound("src/main/resources/Game_Sounds/game-music-loop-1.wav", true); // Play background music

    }

    public MecaniqueCombat(Grille gride, MobAI mob, int waveNb) {
        this.tour = 1;
        controller = UiInitializer.controller;
        instance = this;
        this.grilleCbt = gride;
        this.mobAI = mob;
        this.waveNb = waveNb;

        playSound("src/main/resources/Game_Sounds/game-music-loop-1.wav", true); // Play background music
    }

    public boolean fight(ArrayList<Character> player, ArrayList<Character> mob) {
        boolean playerWon = false;
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < player.size(); i++) {
            grilleCbt.addEntity(player.get(i), player.get(i).getCurrentPos());
        }
        for (int i = 0; i < mob.size(); i++) {
            grilleCbt.addEntity(mob.get(i), mob.get(i).getCurrentPos());
        }

        this.mob = mob;
        this.player = player;
        controller.updatePlayerPane(player.getFirst());
        controller.updateEnemyPane(mob.getFirst());
        controller.updateAttacksPane(player.getFirst().getListSpells());

        this.controller.setPosMob(this.mob.getFirst().getCurrentPos().getX(),
                this.mob.getFirst().getCurrentPos().getY(), this.player.get(0).getMaxPm());
        this.controller.setPosPlayer(this.player.getFirst().getCurrentPos().getX(),
                this.player.getFirst().getCurrentPos().getY(), this.player.get(0).getMaxPm());

        int nbPlayer;
        int nbMob;
        int currentPlayer = 0;
        int currentMob = 0;
        while (!this.player.isEmpty() && !this.mob.isEmpty()) {
            System.out.println("Turn number " + this.tour);
            nbPlayer = player.size();
            nbMob = mob.size();
            while (currentPlayer < nbPlayer && currentMob < nbMob) {
                if (currentPlayer < nbPlayer) {
                    if (player.get(currentPlayer).getDead() == false) {
                        System.out.println("Turn of "
                                + player.get(currentPlayer).getName());
                        doTurn(player.get(currentPlayer), scanner);
                        currentPlayer++;
                    }
                }
                if (currentMob < nbMob) {
                    if (mob.get(currentMob).getDead() == false) {
                        System.out.println("Turn of "
                                + mob.get(currentMob).getName());
                        doTurn(mob.get(currentMob), scanner);
                        currentMob++;
                    }
                }
            }
            currentMob = 0;
            currentPlayer = 0;
            clearDeadPlayer(this.player);
            clearDeadPlayer(this.mob);
            this.tour++;
        }
        if(this.mob.isEmpty()){
            playerWon = true;
        }
        controller.endCombat(playerWon, waveNb);
        scanner.close();
        endCombat();
        stopSound();
        playSound("src/main/resources/Game_Sounds/game-music-loop-3.wav", true);
        return playerWon;
    }

    private void clearDeadPlayer(ArrayList<Character> player) {
        for (int i = 0; i < player.size(); i++) {
            if (player.get(i).getDead()) {
                player.get(i).dead();
                player.remove(i);
            }
        }
    }

    private void doTurn(Character player, Scanner scanner) {
        boolean joueur = false;
        for (int i = 0; i < this.player.size(); i++) {
            if (player.getName().equals(this.player.get(i).getName())) {
                joueur = true;
                break;
            }
        }
        if (player.getDead() == false && joueur == true) {
            while (player.isDone() == false) {
                controller.setPMPlayer(this.player.get(0).getCurrentPm());
                controller.setPAPlayer(this.player.get(0).getCurrentPa());
                System.out.println("Hp: " + player.getCurrentHp() + "\nPa: " + player.getCurrentPa() + "\nPm: "
                        + player.getCurrentPm());
                playSpell(player, scanner);
                playSound("src/main/resources/Game_Sounds/game-notification.wav", false); // Play notification sound
            }
            player.setEndTurn(false);
        } else if (player.getDead() == false) {
            while (player.isDone() == false) {
                System.out.println("Hp: " + player.getCurrentHp() + "\nPa: " + player.getCurrentPa() + "\nPm: "
                        + player.getCurrentPm());
                playSpellMob(player);
            }
            controller.setPMMob(this.mob.get(0).getMaxPm());
            controller.setPAMob(this.mob.get(0).getMaxPa());
            player.setEndTurn(false);
            playSound("src/main/resources/Game_Sounds/game-notification.wav", false); // Play notification sound
        }
    }

    /**
     * Methode qui fait jouer les sort de l'ai
     * A modifier pour ajouter les choix de l'AI
     * 
     * @param player
     */
    private void playSpellMob(Character player) {
        String chosenSpellToString = mobAI.spellChosen(player, this.grilleCbt, this.player);
        int damageDone = 0;
        AStar distAStar = new AStar(this.grilleCbt);

        if (chosenSpellToString.equals("end turn") || chosenSpellToString.equals("end")) {
            updateEndTurn(player);
            System.out.println(player.getName() + " has ended their turn.");
        } else if (chosenSpellToString.contains("movement")) {
            String coordinatesPart = chosenSpellToString.substring("movement".length()).trim();
            String[] coordinates = coordinatesPart.split(",");
            int x = Integer.parseInt(coordinates[0].trim());
            int y = Integer.parseInt(coordinates[1].trim());

            Coord newPos = new Coord(x, y);
            List<Coord> movement = distAStar.findPath(player.getCurrentPos(), newPos);
            int dist = movement.size() - 1;
            System.out.println(newPos.toString());
            updatePm(player, newPos, dist, true);
        } else {
            Spell chosenSpell = player.getSpell(chosenSpellToString);
            if (chosenSpell != null && chosenSpell.getCostPa() <= player.getCurrentPa()) {
                damageDone = chosenSpell.calculateDamage(player);
                String target = mobAI.choseTarget(chosenSpell);
                if (target.equalsIgnoreCase("self")) {
                    updateHp(damageDone, player, true);
                    updatePa(chosenSpell.getCostPa(), player, true);
                    System.out.println(player.getName() + " has cast " + chosenSpellToString + " on self");
                    if (chosenSpellToString == "heal") {
                        playSound("src/main/resources/Game_Sounds/heal.wav", false);
                    }
                    System.out.println(player.getName() + " take " + damageDone);
                } else if (target.equalsIgnoreCase("enemy")) {
                    if (chosenSpellToString == "fireball" || chosenSpellToString == "waterball"){
                        playSound("src/main/resources/Game_Sounds/arcade-game-explosion.wav", false);
                    } else {
                        playSound("src/main/resources/Game_Sounds/sword-slash-with-metallic-impact.wav", false);
                    }
                    updateHp(damageDone, this.player.get(0), false);
                    updatePa(chosenSpell.getCostPa(), player, true);
                    controller.throwSpell(chosenSpellToString);
                    if (this.player.get(0).getCurrentHp() < 0) {
                        this.player.get(0).setDead(true);
                    }
                    System.out.println(player.getName() + " has cast " + chosenSpellToString + " on "
                            + this.player.get(0).getName() + ".");
                    System.out.println(this.player.get(0).getName() + " takes " + damageDone);
                }
            }
        }
    }

    /**
     * Methode pour donner le choix de sort au joeur
     * Changer pour match avec le API
     * 
     * @param player
     */
    private void playSpell(Character player, Scanner scanner) {
        action = "";
        AStar distAStar = new AStar(this.grilleCbt);
        ArrayList<Spell> listSpells = player.getListSpells();
        String chosenSpellToString = "";
        int damageDone = 0;
        String tell = "Choose an action from the following list or end turn :";

        for (int i = 0; i < listSpells.size(); i++) {
            if (player.getCurrentPa() >= listSpells.get(i).getCostPa()) {
                tell += " " + listSpells.get(i).getName() + ",";
            }
        }
        tell = tell.substring(0, tell.length() - 1);
        if (player.getCurrentPm() != 0) {
            tell += "Pm available: " + player.getCurrentPm();
        }
        if (tell.equals("Choose an action from the following list or end turn ")) {
            tell = "Out of action points and movement points, end turn.";
        }
        System.out.println(tell);

        waitForAction();

        if (action.equals("end turn")) {
            updateEndTurn(player); // Update pour la fin de tour
            System.out.println(player.getName() + " has ended their turn.");
        } else if (action.contains("movement")) {
            String coordinatesPart = action.substring("movement".length()).trim();
            String[] coordinates = coordinatesPart.split(",");
            System.out.println(coordinates[0]+","+coordinates[1]);
            int x = Integer.parseInt(coordinates[0].trim());
            int y = Integer.parseInt(coordinates[1].trim());

            Coord newPos = new Coord(x, y);
            System.out.println(newPos.toString());
            List<Coord> movement = distAStar.findPath(player.getCurrentPos(), newPos);
            int dist = movement.size() - 1;
            updatePm(player, newPos, dist, false);
        } else {
            Spell chosenSpell = player.getSpell(action);
            if (chosenSpell != null && chosenSpell.getCostPa() <= player.getCurrentPa()) {
                chosenSpellToString = chosenSpell.getName();
                damageDone = chosenSpell.calculateDamage(player);

                System.out.println("Please chose a target for the spell \"Self\" or \"Enemy\"");
                String target = "enemy";
                if (action.equals("heal")) {
                    target = "self";
                }
                if (target.equalsIgnoreCase("self")) {
                    updateHp(damageDone, player, false); // Update info HP
                    updatePa(chosenSpell.getCostPa(), player, false); // Update info Pa
                    System.out.println(player.getName() + " has cast " + chosenSpellToString + " on self");
                    if (chosenSpellToString == "heal") {
                        playSound("src/main/resources/Game_Sounds/heal.wav", false);
                    }
                    System.out.println(player.getName() + " take " + damageDone + " damage");
                } else if (target.equalsIgnoreCase("enemy")) {
                    if (chosenSpellToString == "fireball" || chosenSpellToString == "waterball"){
                        playSound("src/main/resources/Game_Sounds/arcade-game-explosion.wav", false);
                    } else {
                        playSound("src/main/resources/Game_Sounds/sword-slash-with-metallic-impact.wav", false);
                    }
                    updateHp(damageDone, this.mob.get(0), true); // Update info HP
                    updatePa(chosenSpell.getCostPa(), player, false); // Update info Pa
                    if (this.mob.get(0).getCurrentHp() < 0) {
                        this.mob.get(0).setDead(true);
                    }
                    System.out.println(player.getName() + " has cast " + chosenSpellToString + " on "
                            + this.mob.get(0).getName() + ".");
                    System.out.println(this.mob.get(0).getName() + " take " + damageDone);
                }
            }
        }
    }

    private String waitForAction() {
        boolean paused = true;
        action = "";

        while (paused) {
            if (action != "") {
                paused = false;
            }

            // Petit délais pour éviter usage excessif du CPU
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return action;
    }

    private void updateEndTurn(Character player) {
        player.endTurn();
        controller.endTurn(true);
    }

    private void updatePa(int costPa, Character player, boolean mob) {
        int newPa = player.getCurrentPa() - costPa;
        player.setCurrentPa(newPa);
        if (mob) {
            controller.setPAMob(newPa);
        } else {
            controller.setPAPlayer(newPa);
        }
    }

    private void updateHp(int damage, Character player, boolean mob) {
        int newHp = player.getCurrentHp() - damage;
        player.setCurrentHp(newHp);
        if (mob) {
            controller.setHitpointsMob(newHp);
        } else {
            controller.setHitpointsPlayer(newHp);
        }
    }

    private void updatePm(Character player, Coord newPos, int dist, boolean mob) {
        int newPm = player.getCurrentPm() - dist;
        player.setCurrentPm(newPm);
        grilleCbt.updateCharPos(player, newPos);
        player.setCurrentPos(newPos);
        if (mob) {
            controller.setPosMob(newPos.getX(), newPos.getY(), this.player.get(0).getMaxPm());
            controller.setPMMob(newPm);
        } else {
            controller.setPosPlayer(newPos.getX(), newPos.getY(), newPm);
            controller.setPMPlayer(newPm);
        }
    }

    private void playSound(String soundFile, boolean loop) {
        try {
            File audioFile = new File(soundFile);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            }
            activeClips.add(clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void stopSound() {
        for (Clip clip : activeClips) {
            if (clip.isRunning()) {
                clip.stop();
                clip.close();
            }
        }
        activeClips.clear();
    }

    private void endCombat() {
        this.tour = 0;
        stopSound();
    }

    public int getTour() {
        return tour;
    }

    public ArrayList<Character> getPlayer() {
        return player;
    }

    public void setPlayer(ArrayList<Character> player) {
        this.player = player;
    }

    public ArrayList<Character> getMob() {
        return mob;
    }

    public void setMob(ArrayList<Character> mob) {
        this.mob = mob;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public static MecaniqueCombat getInstance() {
        return instance;
    }

    public Grille getGrilleCbt() {
        return grilleCbt;
    }

    public void setGrilleCbt(Grille grilleCbt) {
        this.grilleCbt = grilleCbt;
    }
}
