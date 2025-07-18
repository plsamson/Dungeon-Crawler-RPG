package game.model;

public class Spell {
    private final String FIRE = "fire";
    private final String WATER = "water";
    private final String EARTH = "earth";
    private final String WIND = "wind";
    private final String NEUTRAL = "neutral";
    private String name, element;
    private int costPa;
    private int damage;
    private int portee;

    public Spell (String name, String element, int costPa, int damage, int po){
        this.costPa = costPa;
        this.damage = damage;
        this.name = name;
        this.element = element;
        this.portee = po;
    }

    public int calculateDamage(Character player){
        double calculate = this.damage;
        int total;
        switch(this.element){
            case FIRE:
                calculate = calculate * (1 + (double)player.getFireStat()*5/100);
                break;
            case EARTH:
                calculate = calculate * (1 + (double)player.getEarthStat()*5/100);
                break;
            case WIND: 
                calculate = calculate * (1 + (double)player.getWindStat()*5/100);
                break;
            case WATER:
                calculate = calculate * (1 + (double)player.getWaterStat()*5/100);
                break;
            case NEUTRAL:
                calculate = calculate * (1 + (double)player.getNeutralStat()*5/100);
                break;
        }
        total = (int) Math.round(calculate);
        return total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public int getCostPa() {
        return costPa;
    }

    public void setCostPa(int costPa) {
        this.costPa = costPa;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setPortee(int po) {this.portee = po;}

    public int getPortee() {
        return portee;
    }
}
