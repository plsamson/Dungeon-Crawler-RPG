package game.model;

import java.util.Objects;

public class Coord {
    private int x, y;

    public Coord(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void move(Coord c){
        this.x = c.getX();
        this.y = c.getY();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coord coord = (Coord) obj;
        return x == coord.x && y == coord.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString(){
        return "("+this.x+","+this.y+")";
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
