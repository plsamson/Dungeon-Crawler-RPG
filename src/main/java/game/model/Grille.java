package game.model;

import java.util.ArrayList;
import java.util.List;

public class Grille {
    private int x, y;
    private Object[][] posEntity;

    public Grille(int largeur, int hauteur) {
        this.x = largeur;
        this.y = hauteur;
        posEntity = new Object[this.x][this.y];
    }

    public void updateCharPos(Character c, Coord pos) {
        Coord currentPos = c.getCurrentPos();
        this.posEntity[currentPos.getX()][currentPos.getY()] = null;
        this.posEntity[pos.getX()][pos.getY()] = c;
        c.getCurrentPos().move(pos);
    }

    public Boolean isEmpty(Coord pos) {
        if (pos.getX() < 0 || pos.getX() >= x || pos.getY() < 0 || pos.getY() >= y) {
            return false;
        }
        return posEntity[pos.getX()][pos.getY()] == null;
    }

    public Boolean isEmptyOpponant(Coord pos) {
        if (pos.getX() < 0 || pos.getX() >= x || pos.getY() < 0 || pos.getY() >= y) {
            return true;
        }
        return posEntity[pos.getX()][pos.getY()] == null;
    }

    public void addEntity(Object e, Coord pos) {
        this.posEntity[pos.getX()][pos.getY()] = e;
    }

    public Object getEntityAtPos(Coord pos) {
        return this.posEntity[pos.getX()][pos.getY()];
    }

    public int dist(Coord pos1, Coord pos2) {
        int dist = 0;
        if (pos1.equals(pos2)) {
            return dist;
        } else {
            dist += Math.abs(pos2.getX() - pos1.getX());
            dist += Math.abs(pos2.getY() - pos1.getY());
            return dist;
        }
    }

    public int pmCost(Coord pos1, Coord pos2) {
        return (dist(pos1, pos2) - 1);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean ligneDeVue(Coord p1, Coord p2) {
        List<Coord> points = calculerPointsLigne(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        for (int i = 0; i < points.size(); i++) {
            if (posEntity[points.get(i).getX()][points.get(i).getY()] instanceof Wall) {
                Wall wall = (Wall) posEntity[points.get(i).getX()][points.get(i).getY()];
                if (wall.blocksLdv) {
                    System.out.println(wall.blocksLdv + "");
                    System.out.println("(" + points.get(i).getX() + "," + points.get(i).getY() + ")");
                    return false; // il y a un obstacle sur la ligne de vue
                }
            } else if (posEntity[points.get(i).getX()][points.get(i).getY()] instanceof Character) {
                return false; // il y a un obstacle sur la ligne de vue
            }
        }
        return true; // pas d'obstacle sur la ligne de vue
    }

    private List<Coord> calculerPointsLigne(int x1, int y1, int x2, int y2) {
        List<Coord> points = new ArrayList<>();
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int err = dx - dy;
        int e2;

        while (true) {
            points.add(new Coord(x1, y1));
            if (x1 == x2 && y1 == y2) {
                break;
            }
            e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }

        return points;
    }

    public Grille loadGrille(int rowss, int colss) {
        String csvData = 
            "0,0,1,1,1,0,0,0,0,0,1,1,1,1,1,0,0,1,1,0,0,1,1,1\n" +
            "0,0,1,1,1,0,0,0,0,0,1,1,1,1,1,0,0,1,1,0,1,1,1,1\n" +
            "0,0,1,1,1,0,0,1,1,0,1,1,1,1,0,0,0,0,0,0,1,1,1,1\n" +
            "0,0,0,1,0,0,0,1,1,0,0,0,0,0,0,0,1,0,0,0,1,1,1,1\n" +
            "0,0,0,0,0,0,0,1,1,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0\n" +
            "0,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,1,1,0,0\n" +
            "0,0,1,0,0,1,0,0,0,0,1,1,1,1,0,0,0,0,1,0,1,1,0,0\n" +
            "1,0,1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0\n" +
            "1,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0";
        
        String[] rows = csvData.split("\n");
        int numRows = rows.length; // Should be 9
        int numCols = rows[0].split(",").length; // Should be 24
        
        if (numRows != colss || numCols != rowss) {
            throw new IllegalArgumentException("CSV data dimensions do not match the expected Grille dimensions.");
        }
    
        int[][] array = new int[numRows][numCols];
        
        for (int i = 0; i < numRows; i++) {
            String[] cols = rows[i].split(",");
            for (int j = 0; j < numCols; j++) {
                array[i][j] = Integer.parseInt(cols[j]);
            }
        }
    
        // Transpose the array to fit the Grille dimensions (24 rows, 9 cols)
        int[][] transposedArray = new int[rowss][colss];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                transposedArray[j][i] = array[i][j];
            }
        }
        Grille gride = new Grille(rowss, colss);
        for (int i = 0; i < transposedArray.length; i++) {
            for (int j = 0; j < transposedArray[i].length; j++) {
                if (transposedArray[i][j] == 1) {
                    gride.addEntity(new Wall(true), new Coord(i, j));
                }
            }
        }
    
        return gride;
    }
    
}