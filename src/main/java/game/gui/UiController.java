package game.gui;

import game.database.Database;
import game.mecanique.MecaniqueCombat;

import game.model.Character;
import game.model.Spell;

import java.io.*;
import java.util.ArrayList;
import java.util.ListIterator;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.animation.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

//Interface Controller - Handles UI actions
public class UiController {

    //DEBUG ELEMENTS
    @FXML private Button aragorn;

    //GENERAL ELEMENTS
    @FXML private Pane gamePane;
    @FXML private GridPane gameGrid;
    @FXML private Pane mainPane;
    @FXML private TilePane attacksPane;
    @FXML private Button skipTurn;

    //PLAYER SETTINGS
    @FXML private Pane playerPane;
    @FXML private Label playerName;
    @FXML private Label playerWind;
    @FXML private Label playerWater;
    @FXML private Label playerEarth;
    @FXML private Label playerFire;
    @FXML private Label playerHitpoints;
    @FXML private Label playerPA;
    @FXML private Label playerPM;
    @FXML private Label playerWeapon;
    @FXML private Button playerWindUpgrade;
    @FXML private Button playerWaterUpgrade;
    @FXML private Button playerEarthUpgrade;
    @FXML private Button playerFireUpgrade;
    private ImageView playerSprite;

    //ENEMY SETTINGS
    @FXML private Pane enemyPane;
    @FXML private Label enemyName;
    @FXML private Label enemyWind;
    @FXML private Label enemyWater;
    @FXML private Label enemyEarth;
    @FXML private Label enemyFire;
    @FXML private Label enemyHitpoints;
    @FXML private Label enemyPA;
    @FXML private Label enemyPM;
    @FXML private Label enemyWeapon;
    private ImageView enemySprite;

    //MAIN MENU SETTINGS
    @FXML private Pane mainMenu_Pane;
    @FXML private Pane credits_Pane;

    @FXML private Pane login_Pane;
    @FXML private TextField login_Username;
    @FXML private PasswordField login_Password;
    @FXML private Button login_LoginButton;
    @FXML private Button login_RegisterButton;
    @FXML private Label login_Invalid;

    @FXML private Pane register_Pane;
    @FXML private TextField register_Username;
    @FXML private TextField register_Password;
    @FXML private Button register_RegisterButton;
    @FXML private Button register_CancelButton;
    @FXML private Label register_Invalid;

    @FXML private Pane newLoad_Pane;
    @FXML private Button newLoad_NewButton;
    @FXML private Button newLoad_LoadButton;

    @FXML private Pane load_Pane;
    @FXML private ListView load_GamesListView;
    @FXML private Button load_LoadButton;
    @FXML private Button load_CancelButton;

    @FXML private Pane create_Pane;
    @FXML private TextField create_Name;
    @FXML private ImageView create_Skin;
    @FXML private Button create_SkinBack;
    @FXML private Button create_SkinNext;
    @FXML private Label create_WindPts;
    @FXML private Button create_WindRemove;
    @FXML private Button create_WindAdd;
    @FXML private Label create_WaterPts;
    @FXML private Button create_WaterRemove;
    @FXML private Button create_WaterAdd;
    @FXML private Label create_EarthPts;
    @FXML private Button create_EarthRemove;
    @FXML private Button create_EarthAdd;
    @FXML private Label create_FirePts;
    @FXML private Button create_FireRemove;
    @FXML private Button create_FireAdd;
    @FXML private Label create_RemainingPts;
    @FXML private ComboBox create_Weapon;
    @FXML private Button create_StartGame;
    @FXML private Button create_Back;
    @FXML private Label create_Invalid;

    //COMBAT SETTINGS
    private int playerLocationX;
    private int playerLocationY;
    private int enemyLocationX;
    private int enemyLocationY;
    private int pmRemaining;
    private ArrayList<Spell> listSpells;
    private Button clickedAttackButton;
    private UiInitializer uiInitializer;
    int[][] csvData;

    @FXML protected void initializeUI() throws IOException {
        playerLocationX = 0;
        playerLocationY = 0;
        pmRemaining = 0;

        Image playerSpriteIdle = new Image("player-idle.gif");
        playerSprite = new ImageView(playerSpriteIdle);
        playerSprite.setMouseTransparent(true);

        Image enemySpriteIdle = new Image("enemy-idle.gif");
        enemySprite = new ImageView(enemySpriteIdle);
        enemySprite.setMouseTransparent(true);

        int cols = getGridPaneColumns();
        int rows = getGridPaneRows();
        csvData = new int[rows][cols];

        //set background and decorations
        Tileset tileset = new Tileset("/tileset.png", 32);
        loadGridConfig("/map/decorationConfig.csv", tileset);
        loadGridConfig("/map/decoration2Config.csv", tileset);
        loadGridConfig("/map/backgroundConfig.csv", tileset);

        // initializers for obstacles
        try (InputStream inputStream = getClass().getResourceAsStream("/map/tileType.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null && row < rows) {
                String[] ids = line.split(",");
                for (int col = 0; col < ids.length && col < cols; col++) {
                    csvData[row][col] = Integer.parseInt(ids[col]);
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // grid events
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Button button = getButtonAt(col, row);
                if (button != null) {
                    final int currentCol = col;
                    final int currentRow = row;
                    //event handler initializer for PM and PO
                    button.setOnAction(event -> {
                        String colorStyle = button.getStyle();
                        boolean isPM = colorStyle.contains("rgba(0,200,0,0.5)");
                        boolean isPO = colorStyle.contains("rgba(0,0,200,0.5)");
                        boolean isEmpty = colorStyle.contains("border-color: DarkSlateGray");
                        if (isPM) {
                            clearMap();
                            Image gifImage = new Image("player-idle.gif");
                            ImageView gifImageView = new ImageView(gifImage);

                            if (currentCol > playerLocationX && currentRow > playerLocationY) { // South-East
                                gifImageView.setImage(new Image("player-right.gif"));
                            } else if (currentCol < playerLocationX && currentRow > playerLocationY) { // South-West
                                gifImageView.setImage(new Image("player-left.gif"));
                            } else if (currentCol < playerLocationX && currentRow < playerLocationY) { // North-West
                                gifImageView.setImage(new Image("player-left.gif"));
                            } else if (currentCol > playerLocationX && currentRow < playerLocationY) { // North-East
                                gifImageView.setImage(new Image("player-right.gif"));
                            } else if (currentCol > playerLocationX) { // East
                                gifImageView.setImage(new Image("player-right.gif"));
                            } else if (currentCol < playerLocationX) { // West
                                gifImageView.setImage(new Image("player-left.gif"));
                            } else if (currentRow > playerLocationY) { // South
                                gifImageView.setImage(new Image("player-bottom.gif"));
                            } else if (currentRow < playerLocationY) { // North
                                gifImageView.setImage(new Image("player-top.gif"));
                            }
                            gameGrid.getChildren().remove(playerSprite);
                            gameGrid.add(gifImageView, playerLocationX, playerLocationY);
                            TranslateTransition transition = new TranslateTransition(Duration.millis(800), gifImageView);
                            transition.setToX((currentCol - playerLocationX) * 50);
                            transition.setToY((currentRow - playerLocationY) * 50);

                            transition.setOnFinished(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    gameGrid.getChildren().remove(gifImageView);
                                    setPlayerLocation(currentCol, currentRow);
                                    setPM(currentCol, currentRow, pmRemaining);
                                    setEnemyLocation(enemyLocationX, enemyLocationY);
                                    System.out.println("Walking to Tile (" + currentCol + "," + currentRow + ")");
                                    MecaniqueCombat cb = MecaniqueCombat.getInstance();
                                    cb.setAction("movement "+currentCol + "," + currentRow);
                                }
                            });
                            transition.play();
                        }
                        if (isPO) {
                            MecaniqueCombat cb = MecaniqueCombat.getInstance();
                            if(clickedAttackButton!=null && button.getText().equals("Enemy")) {
                                Image gifImage = new Image(clickedAttackButton.getText()+".gif");
                                ImageView gifImageView = new ImageView(gifImage);
                                double angle = 0;
                                if (enemyLocationX > playerLocationX && enemyLocationY > playerLocationY) { // South-East
                                    angle = 45;
                                } else if (enemyLocationX < playerLocationX && enemyLocationY > playerLocationY) { // South-West
                                    angle = 135;
                                } else if (enemyLocationX < playerLocationX && enemyLocationY < playerLocationY) { // North-West
                                    angle = 225;
                                } else if (enemyLocationX > playerLocationX && enemyLocationY < playerLocationY) { // North-East
                                    angle = 315;
                                } else if (enemyLocationX > playerLocationX) { // East
                                    angle = 0;
                                } else if (enemyLocationX < playerLocationX) { // West
                                    angle = 180;
                                } else if (enemyLocationY > playerLocationY) { // South
                                    angle = 90;
                                } else if (enemyLocationY < playerLocationY) { // North
                                    angle = 270;
                                }
                                gifImageView.setRotate(angle);
                                gameGrid.add(gifImageView, playerLocationX, playerLocationY);
                                TranslateTransition transition = new TranslateTransition(Duration.seconds(1), gifImageView);
                                transition.setToX((enemyLocationX - playerLocationX) * 50);
                                transition.setToY((enemyLocationY - playerLocationY) * 50);
                                transition.setOnFinished(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        gameGrid.getChildren().remove(gifImageView);
                                    }
                                });
                                transition.play();
                                cb.setAction(clickedAttackButton.getText());
                            }
                            if(clickedAttackButton!=null && (button.getText().equals("Player") && clickedAttackButton.getText().equals("heal"))) {
                                Image gifImage = new Image(clickedAttackButton.getText()+".gif");
                                ImageView gifImageView = new ImageView(gifImage);
                                gameGrid.add(gifImageView, playerLocationX, playerLocationY);
                                TranslateTransition moveDown = new TranslateTransition(Duration.seconds(0.5), gifImageView);
                                moveDown.setByY(20);
                                TranslateTransition moveUp = new TranslateTransition(Duration.seconds(0.5), gifImageView);
                                moveUp.setByY(-40);
                                SequentialTransition sequentialTransition = new SequentialTransition(moveDown, moveUp);
                                sequentialTransition.play();
                                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                                pause.setOnFinished(e -> gameGrid.getChildren().remove(gifImageView));
                                pause.play();
                                cb.setAction(clickedAttackButton.getText());
                            }
                            clearMap();
                            setPlayerLocation(playerLocationX, playerLocationY);
                            setPM(playerLocationX, playerLocationY, pmRemaining);
                            setEnemyLocation(enemyLocationX, enemyLocationY);
                            System.out.println("Casting spell on Tile (" + currentCol + "," + currentRow + ")");
                        }
                        if (isEmpty) {
                            clearMap();
                            setPlayerLocation(playerLocationX, playerLocationY);
                            setPM(playerLocationX, playerLocationY, pmRemaining);
                            setEnemyLocation(enemyLocationX, enemyLocationY);
                        }
                    });
                }
            }
        }
    }

    //Sets the map
    private void loadGridConfig(String config, Tileset tileset) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(config);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] ids = line.split(",");
                for (int col = 0; col < ids.length; col++) {
                    int id = Integer.parseInt(ids[col].trim());
                    ImageView imageView = new ImageView(tileset.getTile(id));
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    imageView.setPreserveRatio(true);
                    gameGrid.getChildren().add(0, imageView); // at beginning of children list
                    GridPane.setRowIndex(imageView, row);
                    GridPane.setColumnIndex(imageView, col);
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Shows the player's spells
    public void updateAttacksPane(ArrayList<Spell> spells) {
        listSpells = spells;
        if (attacksPane != null && listSpells != null) {
            Platform.runLater(() -> {
                ListIterator<Spell> attacks = listSpells.listIterator();
                attacksPane.getChildren().forEach(node -> {
                    if (node instanceof Button && attacks.hasNext()) {
                        Button button = (Button) node;
                        button.setText(attacks.next().getName());
                        VBox tooltipContent = new VBox();
                        Label firstLine = new Label(attacks.previous().getName());
                        firstLine.setStyle("-fx-font-size: 18px;");
                        Label secondLine = new Label("action points: " + attacks.next().getCostPa());
                        secondLine.setStyle("-fx-text-fill: cyan;-fx-font-size: 12px;");
                        Label thirdLine = new Label(attacks.previous().getElement() + " spell");
                        thirdLine.setStyle("-fx-text-fill: yellow;-fx-font-size: 12px;");
                        tooltipContent.getChildren().addAll(firstLine, thirdLine, secondLine);
                        Tooltip tooltip = new Tooltip();
                        tooltip.setGraphic(tooltipContent);
                        tooltip.setShowDelay(Duration.seconds(0.1));
                        tooltip.setHideDelay(Duration.seconds(0.2));
                        button.setTooltip(tooltip);
                        attacks.next();

                        button.setOnAction(event -> {
                            Platform.runLater(() -> {
                                clickedAttackButton = (Button) event.getSource();
                                MecaniqueCombat cb = MecaniqueCombat.getInstance();
                                if (cb != null) {
                                    String spellChosen = clickedAttackButton.getText();
                                    for (Spell spell : listSpells) {
                                        if (spell.getName().equalsIgnoreCase(spellChosen)) {
                                            clearMap();
                                            setPlayerLocation(playerLocationX, playerLocationY);
                                            setEnemyLocation(enemyLocationX, enemyLocationY);
                                            setPO(playerLocationX, playerLocationY, spell.getPortee());
                                        }
                                    }

                                }
                            });
                        });
                    }
                });
                //skipturn
                int lastButtonIndex = attacksPane.getChildren().size() - 1;
                Button lastButton = (Button) attacksPane.getChildren().get(lastButtonIndex);
                if (lastButton != null) {
                    lastButton.setOnAction(event -> {
                        Button clickedAttackButton = (Button) event.getSource();
                        MecaniqueCombat cb = MecaniqueCombat.getInstance();
                        cb.setAction(clickedAttackButton.getText());
                    });
                }
            });
        }
    }

    @FXML public void updatePlayerPane(Character player){
        Platform.runLater(() -> {
            playerName.setText(player.getName());
            playerWind.setText(Integer.toString(player.getWindStat()));
            playerWater.setText(Integer.toString(player.getWaterStat()));
            playerEarth.setText(Integer.toString(player.getEarthStat()));
            playerFire.setText(Integer.toString(player.getFireStat()));
            playerHitpoints.setText(Integer.toString(player.getMaxHp()));
            playerPA.setText(Integer.toString(player.getMaxPa()));
            playerPM.setText(Integer.toString(player.getMaxPm()));
            playerWeapon.setText("Wooden Sword");
        });
    }

    @FXML public void updateEnemyPane(Character enemy){
        Platform.runLater(() -> {
            enemyName.setText(enemy.getName());
            enemyWind.setText(Integer.toString(enemy.getWindStat()));
            enemyWater.setText(Integer.toString(enemy.getWaterStat()));
            enemyEarth.setText(Integer.toString(enemy.getEarthStat()));
            enemyFire.setText(Integer.toString(enemy.getFireStat()));
            enemyHitpoints.setText(Integer.toString(enemy.getMaxHp()));
            enemyPA.setText(Integer.toString(enemy.getMaxPa()));
            enemyPM.setText(Integer.toString(enemy.getMaxPm()));
            enemyWeapon.setText("Iron Sword");
        });
    }

    //Sets location of player on the map
    @FXML public void setPlayerLocation(int x, int y) {
        Platform.runLater(() -> {
            gameGrid.getChildren().remove(playerSprite);
            playerLocationX = x;
            playerLocationY = y;
            gameGrid.add(playerSprite, playerLocationX, playerLocationY);

            for (javafx.scene.Node node : gameGrid.getChildren()) {
                Integer colIndex = GridPane.getColumnIndex(node);
                Integer rowIndex = GridPane.getRowIndex(node);
                // If the coordinates match, change the background color
                if (colIndex != null && rowIndex != null && colIndex == x && rowIndex == y && node instanceof Button) {
                    Button button = (Button) node;
                    //button.setStyle("-fx-background-color: rgba(200,0,0,0.5)");
                    button.setText("Player");
                    break;
                }
            }
        });
    }

    //walks the enemy to his next node on the map
    public void animateEnemyLocation(int x, int y) {
        while (gameGrid.getChildren().contains(enemySprite)) {
            gameGrid.getChildren().remove(enemySprite);
        }
        Image gifImageEnemy = new Image("enemy-idle.gif");
        if (enemyLocationX > playerLocationX && enemyLocationY > playerLocationY) { // South-East
            gifImageEnemy = new Image("enemy-left.gif");
        } else if (enemyLocationX < playerLocationX && enemyLocationY > playerLocationY) { // South-West
            gifImageEnemy = new Image("enemy-left.gif");
        } else if (enemyLocationX < playerLocationX && enemyLocationY < playerLocationY) { // North-West
            gifImageEnemy = new Image("enemy-left.gif");
        } else if (enemyLocationX > playerLocationX && enemyLocationY < playerLocationY) { // North-East
            gifImageEnemy = new Image("enemy-left.gif");
        } else if (enemyLocationX > playerLocationX) { // East
            gifImageEnemy = new Image("enemy-left.gif");
        } else if (enemyLocationX < playerLocationX) { // West
            gifImageEnemy = new Image("enemy-left.gif");
        } else if (enemyLocationY > playerLocationY) { // South
            gifImageEnemy = new Image("enemy-left.gif");
        } else if (enemyLocationY < playerLocationY) { // North
            gifImageEnemy = new Image("enemy-left.gif");
        }
        ImageView gifImageViewEnemy = new ImageView(gifImageEnemy);
        gameGrid.add(gifImageViewEnemy, enemyLocationX, enemyLocationY);
        TranslateTransition transitionEnemy = new TranslateTransition(Duration.millis(800), gifImageViewEnemy);
        transitionEnemy.setToX((x - enemyLocationX) * 50);
        transitionEnemy.setToY((y - enemyLocationY) * 50);
        transitionEnemy.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gameGrid.getChildren().remove(gifImageViewEnemy);
                setEnemyLocation(x, y);
            }
        });
        transitionEnemy.play();
    }

    //Sets location of enemy on the map
    public void setEnemyLocation(int x, int y) {
        Platform.runLater(() -> {
            while (gameGrid.getChildren().contains(enemySprite)) {
                gameGrid.getChildren().remove(enemySprite);
            }

            enemyLocationX = x;
            enemyLocationY = y;

            gameGrid.add(enemySprite, enemyLocationX, enemyLocationY);

            for (javafx.scene.Node node : gameGrid.getChildren()) {
                Integer colIndex = GridPane.getColumnIndex(node);
                Integer rowIndex = GridPane.getRowIndex(node);

                // If the coordinates match, change the background color
                if (colIndex != null && rowIndex != null && colIndex == x && rowIndex == y && node instanceof Button) {
                    Button button = (Button) node;
                    //button.setStyle("-fx-background-color: rgba(200,0,0,0.5)");
                    button.setStyle("-fx-border-color: DarkSlateGray; -fx-background-color: transparent");
                    button.setText("Enemy");
                    break;
                }
            }
        });
    }

    //Shows PM of player on the map
    public void setPM(int x, int y, int pmRemaining) {
        Platform.runLater(() -> {
            int cols = getGridPaneColumns();
            int rows = getGridPaneRows();
            for (int dx = -pmRemaining; dx <= pmRemaining; dx++) {
                for (int dy = -pmRemaining; dy <= pmRemaining; dy++) {
                    if ((dx != 0 || dy != 0) && Math.abs(dx) + Math.abs(dy) <= pmRemaining) {
                        int newX = x + dx;
                        int newY = y + dy;
                        // Check the coordinates are within the bounds of the grid
                        if (newX >= 0 && newX < cols && newY >= 0 && newY < rows) {
                            for (javafx.scene.Node node : gameGrid.getChildren()) {
                                Integer colIndex = GridPane.getColumnIndex(node);
                                Integer rowIndex = GridPane.getRowIndex(node);

                                // If the coordinates match
                                if (colIndex != null && rowIndex != null && colIndex == newX && rowIndex == newY && node instanceof Button) {
                                    Button button = (Button) node;
                                    if (csvData[rowIndex][colIndex] == 0) {
                                        button.setStyle("-fx-background-color: rgba(0,200,0,0.5); -fx-border-color: Green;");

                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    //Shows PO of a clicked spell on the map
    public void setPO(int x, int y, int spellPO) {
        Platform.runLater(() -> {
            int cols = getGridPaneColumns();
            int rows = getGridPaneRows();

            if (spellPO == 0) {
                // Handle spellPO is 0 (heal spell)
                Button button = findButtonAtPosition(x, y);
                if (button != null) {
                    button.setStyle("-fx-background-color: rgba(0,0,200,0.5); -fx-border-color: DarkBlue;");
                }
            } else {
                // Handle other cases of spellPO
                for (int dx = -spellPO; dx <= spellPO; dx++) {
                    for (int dy = -spellPO; dy <= spellPO; dy++) {
                        if ((dx != 0 || dy != 0) && Math.abs(dx) + Math.abs(dy) <= spellPO) {
                            int newX = x + dx;
                            int newY = y + dy;

                            // Check if the new coordinates are within bounds
                            if (newX >= 0 && newX < cols && newY >= 0 && newY < rows) {
                                Button button = findButtonAtPosition(newX, newY);
                                if (button != null && csvData[newY][newX] == 0) {
                                    button.setStyle("-fx-background-color: rgba(0,0,200,0.5); -fx-border-color: DarkBlue;");
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private Button findButtonAtPosition(int x, int y) {
        for (Node node : gameGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (colIndex != null && rowIndex != null && colIndex == x && rowIndex == y) {
                if (node instanceof Button) {
                    return (Button) node;
                } else if (node instanceof ImageView) {
                    // Check if there is a button underneath the ImageView
                    for (Node sibling : gameGrid.getChildren()) {
                        Integer siblingColIndex = GridPane.getColumnIndex(sibling);
                        Integer siblingRowIndex = GridPane.getRowIndex(sibling);

                        if (siblingColIndex != null && siblingRowIndex != null && siblingColIndex == x && siblingRowIndex == y && sibling instanceof Button) {
                            return (Button) sibling;
                        }
                    }
                }
            }
        }
        return null;
    }

    /** Mecanique Combqt **/
    public void endTurn(boolean end) {
//        Platform.runLater(() -> {
//        if(end) {
//            clearMap();
//            //setPlayerLocation(playerLocationX, playerLocationY);
//            //setEnemyLocation(enemyLocationX, enemyLocationY);
//        }
//        });
    }

    public void endCombat(boolean playerWon, int waveNb) {
        Platform.runLater(() -> {
            if (!playerWon) {
                clearMap();
                skipTurn.setText("YOU DIED");
            } else {
                clearMap();
                pmRemaining = 0;
                playerLocationX = 0;
                playerLocationY = 0;
                enemyLocationX = 23;
                enemyLocationY = 8;
                skipTurn.setText("LEVEL " + (waveNb + 2));
                skipTurn.setStyle("-fx-text-fill: LightGreen;");
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.seconds(3),
                        ae -> {skipTurn.setText("end turn");
                               skipTurn.setStyle("-fx-text-fill: white;");
                        }
                ));
                timeline.play();
            }
        });
    }


    public void setHitpointsPlayer(int hp) {
        Platform.runLater(() -> {
            if (hp < 0) {
                playerHitpoints.setText(Integer.toString(0));
            } else {
                playerHitpoints.setText(Integer.toString(hp));
            }
        });
    }

    public void setPAPlayer(int pa) {
        Platform.runLater(() -> {
            playerPA.setText(Integer.toString(pa));
        });
    }

    public void setPMPlayer(int pm) {
        Platform.runLater(() -> {
            pmRemaining = pm;
            playerPM.setText(Integer.toString(pm));
            clearMap();
            setPlayerLocation(playerLocationX,playerLocationY);
            setPM(playerLocationX,playerLocationY,pm);
            setEnemyLocation(enemyLocationX, enemyLocationY);
        });
    }

    public void setPosPlayer(int x, int y, int pm) {
        Platform.runLater(() -> {
            pmRemaining = pm;
            playerLocationX = x;
            playerLocationY = y;
            clearMap();
            setPlayerLocation(x,y);
            setPM(x,y,pm);
            setEnemyLocation(enemyLocationX, enemyLocationY);
        });
    }

    public void setHitpointsMob(int hp) {
        Platform.runLater(() -> {
            if (hp < 0) {
                enemyHitpoints.setText(Integer.toString(0));
            } else {
                enemyHitpoints.setText(Integer.toString(hp));
            }
        });
    }

    public void setPAMob(int pa) {
        Platform.runLater(() -> {
            enemyPA.setText(Integer.toString(pa));
        });
    }

    public void setPMMob(int pm) {
        Platform.runLater(() -> {
            enemyPM.setText(Integer.toString(pm));
        });
    }

    public void setPosMob(int x, int y, int playerPM) {
        Platform.runLater(() -> {
            PauseTransition pause = new PauseTransition(Duration.millis(850));
            pause.setOnFinished(event -> {
                while (gameGrid.getChildren().contains(enemySprite)) {
                    gameGrid.getChildren().remove(enemySprite);
                }
                clearMap();
                setPlayerLocation(playerLocationX, playerLocationY);
                setPM(playerLocationX, playerLocationY, playerPM);
                if ((x==0 && y==0) || (x==23 && y==8)) {
                    enemyLocationX=23;
                    enemyLocationY=8;
                    setEnemyLocation(x, y);
                } else {
                    animateEnemyLocation(x, y);
                }
            });
            pause.play();

        });
    }

    public void throwSpell(String spell) {
        Platform.runLater(() -> {
                    PauseTransition pause = new PauseTransition(Duration.millis(1));
                    pause.setOnFinished(event -> {
                        Image gifImage = new Image(spell + ".gif");
                        ImageView gifImageView = new ImageView(gifImage);
                        double angle = 0;

                        // Calculate the angle based on the relative positions
                        if (playerLocationX > enemyLocationX && playerLocationY > enemyLocationY) { // South-East
                            angle = 45;
                        } else if (playerLocationX < enemyLocationX && playerLocationY > enemyLocationY) { // South-West
                            angle = 135;
                        } else if (playerLocationX < enemyLocationX && playerLocationY < enemyLocationY) { // North-West
                            angle = 225;
                        } else if (playerLocationX > enemyLocationX && playerLocationY < enemyLocationY) { // North-East
                            angle = 315;
                        } else if (playerLocationX > enemyLocationX) { // East
                            angle = 0;
                        } else if (playerLocationX < enemyLocationX) { // West
                            angle = 180;
                        } else if (playerLocationY > enemyLocationY) { // South
                            angle = 90;
                        } else if (playerLocationY < enemyLocationY) { // North
                            angle = 270;
                        }

                        gifImageView.setRotate(angle);
                        gameGrid.add(gifImageView, enemyLocationX, enemyLocationY);

                        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), gifImageView);
                        transition.setToX((playerLocationX - enemyLocationX) * 50);
                        transition.setToY((playerLocationY - enemyLocationY) * 50);
                        transition.setOnFinished(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                gameGrid.getChildren().remove(gifImageView);
                            }
                        });
                        transition.play();
                    });
            pause.play();
        });
    }


    /**Utility methods**/
    private void clearMap() {

        for (javafx.scene.Node node : gameGrid.getChildren()) {
            if (node instanceof Button) {

                Integer row = GridPane.getRowIndex(node);
                Integer col = GridPane.getColumnIndex(node);

                ((Button) node).setStyle("-fx-background-color: transparent;");
                ((Button) node).setText("");

                StringBuilder borderStyle = new StringBuilder();
                borderStyle.append("-fx-border-style: solid;");
                // Initialize border width for all sides
                double topBorderWidth = 0;
                double rightBorderWidth = 0;
                double bottomBorderWidth = 0;
                double leftBorderWidth = 0;

                //set obstacles
                if (csvData[row][col] == 1) {
                    // Check north (row - 1)
                    if (row > 0 && csvData[row - 1][col] == 0) {
                        topBorderWidth = 1;
                    }
                    // Check south (row + 1)
                    if (row < csvData.length - 1 && csvData[row + 1][col] == 0) {
                        bottomBorderWidth = 1;
                    }
                    // Check west (col - 1)
                    if (col > 0 && csvData[row][col - 1] == 0) {
                        leftBorderWidth = 1;
                    }
                    // Check east (col + 1)
                    if (col < csvData[0].length - 1 && csvData[row][col + 1] == 0) {
                        rightBorderWidth = 1;
                    }
                    borderStyle.append(String.format(" -fx-border-width: %fpx %fpx %fpx %fpx;", topBorderWidth, rightBorderWidth, bottomBorderWidth, leftBorderWidth));
                    borderStyle.append(" -fx-border-color: DarkSlateGray;");
                    ((Button) node).setStyle(borderStyle.toString());
                } else {
                    ((Button) node).setStyle("-fx-border-color: DarkSlateGray;");
                }
            }
        }
    }

    private int getGridPaneColumns() {
        int maxCol = 0;
        for (javafx.scene.Node node : gameGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            if (colIndex != null && colIndex > maxCol) {
                maxCol = colIndex;
            }
        }
        return maxCol + 1;
    }

    private int getGridPaneRows() {
        int maxRow = 0;
        for (javafx.scene.Node node : gameGrid.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            if (rowIndex != null && rowIndex > maxRow) {
                maxRow = rowIndex;
            }
        }
        return maxRow + 1;
    }

    private Button getButtonAt(int col, int row) {
        for (javafx.scene.Node node : gameGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            if (colIndex != null && rowIndex != null && colIndex == col && rowIndex == row && node instanceof Button) {
                return (Button) node;
            }
        }
        return null;
    }

    public void setUiInitializer(UiInitializer uiInitializer) {
        this.uiInitializer = uiInitializer;
    }

    /** Main Menu **/
    @FXML private void aragorn() throws Exception {
        UiInitializer.getInstance().loadGame();
    }

    @FXML private void login() throws Exception {
        if (Database.checkUser(login_Username.getText(), login_Password.getText())) {
            //showCreateLoadPanel();
            UiInitializer.getInstance().loadGame();
        } else {
            showMessage(login_Invalid, "incorrect username or password");
        }
    }

    @FXML private void register() {
        if (Database.createUser(register_Username.getText(), register_Password.getText())) {
            showMessage(login_Invalid, "new user created");
        } else {
            showMessage(register_Invalid, "user already exists");
        }
    }

    @FXML private void create() {
        //créer partie
        showMessage(create_Invalid, "fields invalid");
    }

    @FXML private void load() {
        //loader partie selectionnee dans listview
    }

    @FXML private void showCreatePanel() {
        showPanel(create_Pane);
    }

    @FXML private void showLoadPanel() {
        showPanel(load_Pane);
    }

    @FXML private void showRegisterPanel() {
        login_Username.setText("");
        login_Password.setText("");
        showPanel(register_Pane);
    }

    @FXML private void showLoginPanel() {
        register_Username.setText("");
        register_Password.setText("");
        showPanel(login_Pane);
    }

    @FXML private void showCreateLoadPanel() {
        hideAllPanels();
        showPanel(newLoad_Pane);
    }

    @FXML private void showCreditsPanel() {
        hideAllPanels();
        showPanel(credits_Pane);
    }

    @FXML private void showPanel(Pane pane) {
        hideAllPanels();
        pane.setOpacity(0);
        pane.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), pane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);
        fadeIn.play();
    }

    @FXML private void hideAllPanels() {
        login_Pane.setVisible(false);
        register_Pane.setVisible(false);
        newLoad_Pane.setVisible(false);
        load_Pane.setVisible(false);
        create_Pane.setVisible(false);
        credits_Pane.setVisible(false);
    }

    @FXML private void showMessage(Label label, String message) {
        if (message.equals("new user created")) {
                label.setStyle("-fx-alignment: center;-fx-text-fill: rgba(0,200,0)");
                showLoginPanel();
        } else {
            label.setStyle("-fx-alignment: center;-fx-text-fill: rgba(200,0,0)");
        }
        Glow glow = new Glow();
        glow.setLevel(0.6);
        label.setEffect(glow);
        label.setText(message);
        label.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), label);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), label);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        SequentialTransition sequence = new SequentialTransition(label, fadeIn, pause, fadeOut);
        label.setVisible(true);
        //sequence.setOnFinished(event -> {});
        sequence.play();
    }
}