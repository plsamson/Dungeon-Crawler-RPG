package game.mecanique;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import game.model.Character;
import game.model.Coord;
import game.model.Grille;
import game.model.Spell;

public class MobAI {
    private final String SELF = "self";
    private final String ENEMY = "enemy";
    private final String ENDTURN = "end";
    private final String MOVE = "movement ";
    private boolean range;
    private ArrayList<Character> enemy;

    public MobAI(boolean range) {
        this.range = range;
    }

    public String spellChosen(Character mob, Grille grille, ArrayList<Character> enemy) {
        this.enemy = enemy;
        if (this.range) {
            return rangeSpell(mob, grille);
        } else {
            return meleeSpell(mob, grille);
        }
    }

    private String rangeSpell(Character mob, Grille grille) {
        ArrayList<Spell> spellOptions = new ArrayList<Spell>();
        for (int i = 0; i < mob.getListSpells().size(); i++) {
            if (mob.getListSpells().get(i).getCostPa() <= mob.getCurrentPa()) {
                if (mob.getCurrentHp() <= (mob.getMaxHp() / 2)) {
                    spellOptions.add(mob.getListSpells().get(i));
                } else if (mob.getListSpells().get(i).getDamage() > 0) {
                    spellOptions.add(mob.getListSpells().get(i));
                }
            }
        }

        Grille grilleAtest = new Grille(grille.getX(), grille.getY());
        for (int i = 0; i < grille.getX(); i++) {
            for (int j = 0; j < grille.getY(); j++) {
                Coord tmp = new Coord(i, j);
                if (i == mob.getCurrentPos().getX() && j == mob.getCurrentPos().getY()) {
                    grilleAtest.addEntity(null, tmp);
                } else if (i == enemy.getFirst().getCurrentPos().getX()
                        && j == enemy.getFirst().getCurrentPos().getY()) {
                    grilleAtest.addEntity(null, tmp);
                } else {
                    grilleAtest.addEntity(grille.getEntityAtPos(tmp), tmp);
                }
            }
        }

        Character player = enemy.getFirst();
        if (grilleAtest.ligneDeVue(mob.getCurrentPos(), player.getCurrentPos())) {
            int dist = grille.dist(mob.getCurrentPos(), player.getCurrentPos());
            int count = 0;
            while (count < spellOptions.size()) {
                if (spellOptions.get(count).getPortee() < dist) {
                    spellOptions.remove(count);
                } else {
                    count++;
                }
            }
            if (spellOptions.size() == 0 && mob.getCurrentPm() == 0) {
                return ENDTURN;
            } else if (spellOptions.size() == 0) {
                return moveRandomDirection(mob, grille);
            } else if (spellOptions.size() == 1) {
                return spellOptions.get(0).getName();
            } else {
                Random random = new Random();
                int rndNum = random.nextInt(spellOptions.size() - 1);
                return spellOptions.get(rndNum).getName();
            }
        } else {
            if (mob.getCurrentPm() > 0) {
                return moveRandomDirection(mob, grille);
            } else {
                return ENDTURN;
            }
        }
    }

    private String moveRandomDirection(Character mob, Grille grille) {
        int moveX = grille.getX();
        int moveY = grille.getY();
        int depX = 0;
        int depY = 0;
        Random rand = new Random();
        boolean posFree = false;
        Grille grilleAtest = new Grille(grille.getX(), grille.getY());
        for (int i = 0; i < grille.getX(); i++) {
            for (int j = 0; j < grille.getY(); j++) {
                Coord tmp = new Coord(i, j);
                if (i == mob.getCurrentPos().getX() && j == mob.getCurrentPos().getY()) {
                    grilleAtest.addEntity(null, tmp);
                } else {
                    grilleAtest.addEntity(grille.getEntityAtPos(tmp), tmp);
                }
            }
        }
        AStar path = new AStar(grilleAtest);
        List<Coord> found = new ArrayList<>();
        if (grille.dist(this.enemy.getFirst().getCurrentPos(), mob.getCurrentPos()) > 9) {
            return moveToTarget(grille, mob);
        } else {
            while (!posFree) {
                depX = rand.nextInt(moveX);
                depY = rand.nextInt(moveY);
                Coord temp = new Coord(depX, depY);
                if (grille.dist(this.enemy.getFirst().getCurrentPos(), temp) <= 9) {
                    found = path.findPath(mob.getCurrentPos(), temp);
                    posFree = grille.isEmpty(temp);
                }
            }
            int pm = mob.getCurrentPm();
            if (found.size() - 1 > pm) {
                return (MOVE + found.get(pm).getX() + "," + found.get(pm).getY());
            } else {
                return (MOVE + found.get(found.size() - 1).getX() + "," + found.get(found.size() - 1).getY());
            }
        }
    }

    private String meleeSpell(Character mob, Grille grille) {
        ArrayList<Spell> spellOptions = new ArrayList<Spell>();
        if (inRange(mob, grille)) {
            for (int i = 0; i < mob.getListSpells().size(); i++) {
                if (mob.getListSpells().get(i).getCostPa() <= mob.getCurrentPa()) {
                    if (mob.getCurrentHp() <= (mob.getMaxHp() / 2)) {
                        spellOptions.add(mob.getListSpells().get(i));
                    } else if (mob.getListSpells().get(i).getDamage() > 0) {
                        spellOptions.add(mob.getListSpells().get(i));
                    }
                }
            }
            if (spellOptions.size() == 0) {
                return ENDTURN;
            } else {
                if (spellOptions.size() == 1) {
                    return spellOptions.get(0).getName();
                }
                Random random = new Random();
                int rndNum = random.nextInt(spellOptions.size() - 1);
                return spellOptions.get(rndNum).getName();
            }
        } else {
            if (mob.getCurrentPm() > 0) {
                return moveToTarget(grille, mob);
            } else {
                return ENDTURN;
            }

        }
    }

    public String moveToTarget(Grille grille, Character mob) {
        List<Coord> path = bestPath(grille, mob, this.enemy.getFirst());
        for (int i = 1; i < enemy.size(); i++) {
            List<Coord> tmp = bestPath(grille, mob, this.enemy.get(i));
            if (path == null) {
                path = tmp;
            }
            if (tmp != null) {
                if (tmp.size() < path.size()) {
                    path = tmp;
                }
            }
        }
        int pm = mob.getCurrentPm();
        if (path.size() - 1 > pm) {
            return (MOVE + path.get(pm).getX() + "," + path.get(pm).getY());
        } else {
            return (MOVE + path.get(path.size() - 1).getX() + "," + path.get(path.size() - 1).getY());
        }
    }

    private List<Coord> bestPath(Grille grille, Character mob, Character enemy) {
        Grille grilleAtest = new Grille(grille.getX(), grille.getY());
        for (int i = 0; i < grille.getX(); i++) {
            for (int j = 0; j < grille.getY(); j++) {
                Coord tmp = new Coord(i, j);
                if (i == mob.getCurrentPos().getX() && j == mob.getCurrentPos().getY()) {
                    grilleAtest.addEntity(null, tmp);
                } else {
                    grilleAtest.addEntity(grille.getEntityAtPos(tmp), tmp);
                }
            }
        }
        AStar algo = new AStar(grilleAtest);
        List<Coord> north = algo.findPath(mob.getCurrentPos(),
                new Coord(enemy.getCurrentPos().getX(), enemy.getCurrentPos().getY() - 1));
        List<Coord> south = algo.findPath(mob.getCurrentPos(),
                new Coord(enemy.getCurrentPos().getX(), enemy.getCurrentPos().getY() + 1));
        List<Coord> east = algo.findPath(mob.getCurrentPos(),
                new Coord(enemy.getCurrentPos().getX() + 1, enemy.getCurrentPos().getY()));
        List<Coord> west = algo.findPath(mob.getCurrentPos(),
                new Coord(enemy.getCurrentPos().getX() - 1, enemy.getCurrentPos().getY()));
        List<List<Coord>> paths = new ArrayList<>();
        if (north != null)
            paths.add(north);
        if (south != null)
            paths.add(south);
        if (east != null)
            paths.add(east);
        if (west != null)
            paths.add(west);
        return paths.isEmpty() ? null : Collections.min(paths, (p1, p2) -> Integer.compare(p1.size(), p2.size()));
    }

    public String choseTarget(Spell chosenSpell) {
        if (chosenSpell.getDamage() < 0) {
            return this.SELF;
        } else {
            return this.ENEMY;
        }
    }

    private boolean inRange(Character c, Grille grille) {
        Coord self = c.getCurrentPos();
        for (int i = 0; i < enemy.size(); i++) {
            if (grille.dist(self, enemy.get(i).getCurrentPos()) == 1) {
                return true;
            }
        }
        return false;
    }

    public void setEnemy(ArrayList<Character> enemy) {
        this.enemy = enemy;
    }

}
