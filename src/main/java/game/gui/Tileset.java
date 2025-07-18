package game.gui;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import java.util.HashMap;
import java.util.Map;

//Manipulates tileset image, peuple tuiles de cartes
public class Tileset {
    private Image tilesetImage;
    private int tileSize;
    private Map<Integer, Image> tiles;

    public Tileset(String imagePath, int tileSize) {
        this.tileSize = tileSize;
        this.tilesetImage = new Image(imagePath);
        this.tiles = new HashMap<>();
        loadTiles();
    }

    private void loadTiles() {
        //convertir à base64
        PixelReader pixelReader = tilesetImage.getPixelReader();

        //split le base64
        int columns = (int) tilesetImage.getWidth() / tileSize;
        int rows = (int) tilesetImage.getHeight() / tileSize;

        //emmagasiner
        int id = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int x = col * tileSize;
                int y = row * tileSize;
                WritableImage tile = new WritableImage(pixelReader, x, y, tileSize, tileSize);
                tiles.put(id++, tile);
            }
        }
    }

    //retourne une tuile pour la placer sur la carte
    public Image getTile(int id) {
        return tiles.get(id);
    }
}