package game.model;

import java.util.ArrayList;

public class Character {
    private String name;
    private int maxPa, maxHp, maxPm;
    private int currentHp, currentPa, currentPm;
    private int fireStat, waterStat, earthStat, windStat, neutralStat;
    private Boolean dead, done;
    private ArrayList<Spell> listSpells;
    private Coord currentPos;

    public Character(String name, int maxPa, int maxHp, int maxPm, int fireStat, int waterStat, int earthStat, int windStat, int neutralStat, ArrayList<Spell> listSpells){
        this.name = name;
        this.maxPa = maxPa;
        this.currentPa = maxPa;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.maxPm = maxPm;
        this.currentPm = maxPm;
        this.fireStat = fireStat;
        this.waterStat = waterStat;
        this.earthStat = earthStat;
        this.windStat = windStat;
        this.neutralStat = neutralStat;
        this.done = false;
        this.dead = false;
        this.listSpells = listSpells;
    }

    public boolean isDone(){
        return this.done;
    }

    public void setEndTurn(boolean end){
        this.done = end;
    }

    public void dead(){
        this.currentHp = maxHp;
    }

    public void endTurn(){
        this.currentPa = maxPa;
        this.currentPm = maxPm;
        this.done = true;
    }

    public int getMaxPa() {
        return maxPa;
    }

    public void setMaxPa(int maxPa) {
        this.maxPa = maxPa;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getMaxPm() {
        return maxPm;
    }

    public void setMaxPm(int maxPm) {
        this.maxPm = maxPm;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public int getCurrentPa() {
        return currentPa;
    }

    public void setCurrentPa(int currentPa) {
        this.currentPa = currentPa;
    }

    public int getCurrentPm() {
        return currentPm;
    }

    public void setCurrentPm(int currentPm) {
        this.currentPm = currentPm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFireStat() {
        return fireStat;
    }

    public void setFireStat(int fireStat) {
        this.fireStat = fireStat;
    }

    public int getWaterStat() {
        return waterStat;
    }

    public void setWaterStat(int waterStat) {
        this.waterStat = waterStat;
    }

    public int getEarthStat() {
        return earthStat;
    }

    public void setEarthStat(int earthStat) {
        this.earthStat = earthStat;
    }

    public int getWindStat() {
        return windStat;
    }

    public void setWindStat(int windStat) {
        this.windStat = windStat;
    }

    public int getNeutralStat() {
        return neutralStat;
    }

    public void setNeutralStat(int neutralStat) {
        this.neutralStat = neutralStat;
    }

    public Boolean getDead() {
        return dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public void addSpell(Spell spell){
        this.listSpells.add(spell);
    }

    public Spell getSpell(String spell){
        for(int i = 0; i< listSpells.size(); i++){
            String tmp = listSpells.get(i).getName();
            if(tmp.equals(spell)){
                return listSpells.get(i);
            }
        }
        return null;
    }

    public ArrayList<Spell> getListSpells(){
        return this.listSpells;
    }

    public Coord getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(Coord currentPos) {
        this.currentPos = currentPos;
    }

    
    public String toString(){
        String tmp = "Name: "+this.name+" HP: "+this.currentHp+"/"+this.maxHp+"\n"+
                     "PA: "+this.currentPa+" PM: "+this.currentPm+"\n"+
                     "Spell: ";
        for(int i=0; i<this.listSpells.size();i++){
            tmp+= this.listSpells.get(i).getName()+", ";
        }
        tmp = tmp.substring(0,tmp.length()-2);
        return tmp;
    }
}
