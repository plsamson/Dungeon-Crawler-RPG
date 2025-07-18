package game.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import game.model.Character;
import game.model.Spell;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/BaseDeDonnee/Base.db";
    protected static String DB_USER = null;
    protected static String DB_PASSWORD = null;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception ex) {
            System.err.println("Erreur de connexion à SQLite : " + ex.getMessage());
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static boolean checkUser(String username, String password) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception ex) {
            System.err.println("Erreur de connexion à SQLite : " + ex.getMessage());
        }
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn
                        .prepareStatement("SELECT * FROM User WHERE username = ? AND password = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createUser(String username, String password) {

        DB_USER = username;
        DB_PASSWORD = password;

        try (Connection conn = getConnection()) {
            // Vérifier si l'utilisateur existe déjà
            if (checkUser(username, password)) {
                // L'utilisateur existe déjà
                return false;
            }

            // Insérer le nouvel utilisateur
            String insertUserQuery = "INSERT INTO User (username, password) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertUserQuery)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtient le userId à partir du username et du password.
     * 
     * @param username Le nom d'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     * @return L'ID de l'utilisateur si les identifiants sont corrects, sinon -1.
     */
    public static int getUserId(String username, String password) {
        try (Connection conn = getConnection()) {
            String selectUserIdQuery = "SELECT userId FROM User WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectUserIdQuery)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("userId");
                } else {
                    // Aucun utilisateur trouvé avec ces identifiants
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean startGame(int characterId, int levelProgress) {
        try (Connection conn = getConnection()) {
            String insertGameQuery = "INSERT INTO Game (characterId, levelProgress) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertGameQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, characterId);
                stmt.setInt(2, levelProgress);
                stmt.executeUpdate();

                // Optionally, return true or the generated game ID
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getLastInsertedGameId() {
        try (Connection conn = getConnection()) {
            String query = "SELECT uniqueId FROM Game ORDER BY uniqueId DESC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("uniqueId");
                } else {
                    throw new SQLException("No games found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Obtient le characterId à partir du gameId.
     * 
     * @param gameId L'ID du jeu.
     * @return Le characterId associé au gameId, ou -1 si le jeu n'existe pas.
     */
    public static int getCharacterIdByGameId(int gameId) {
        try (Connection conn = getConnection()) {
            String query = "SELECT characterId FROM Game WHERE uniqueId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, gameId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("characterId");
                } else {
                    // Aucun jeu trouvé avec ce gameId
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean saveGame(int gameId, int userId) {
        try (Connection conn = getConnection()) {
            // Check if the user already has 3 game records
            String countGamesQuery = "SELECT COUNT(*) FROM UserGame WHERE userId = ?";
            try (PreparedStatement countStmt = conn.prepareStatement(countGamesQuery)) {
                countStmt.setInt(1, userId);
                ResultSet countRs = countStmt.executeQuery();
                int count = countRs.getInt(1);
                if (count >= 3) {
                    return false; // User has reached the maximum number of records
                }
            }

            // Insert the game record
            String insertUserGameQuery = "INSERT INTO UserGame (gameId, userId) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertUserGameQuery)) {
                insertStmt.setInt(1, gameId);
                insertStmt.setInt(2, userId);
                insertStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkUserGame(int gameId, int userId) {
        try (Connection conn = getConnection()) {
            String checkUserGameQuery = "SELECT COUNT(*) FROM UserGame WHERE gameId = ? AND userId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkUserGameQuery)) {
                stmt.setInt(1, gameId);
                stmt.setInt(2, userId);
                ResultSet rs = stmt.executeQuery();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static ArrayList<Spell> loadSpells(Connection conn, int spellId) throws SQLException {
        ArrayList<Spell> spells = new ArrayList<>();
        String selectSpellsQuery = "SELECT uniqueId, Nom, element, baseDmg, portee, pointsAction FROM Spell WHERE uniqueId = ?";
        PreparedStatement spellsStmt = conn.prepareStatement(selectSpellsQuery);
        spellsStmt.setInt(1, spellId);
        ResultSet spellsRs = spellsStmt.executeQuery();

        while (spellsRs.next()) {
            int uniqueId = spellsRs.getInt("uniqueId");
            String nom = spellsRs.getString("Nom");
            String element = spellsRs.getString("element");
            int baseDmg = spellsRs.getInt("baseDmg");
            int portee = spellsRs.getInt("portee");
            int pointsAction = spellsRs.getInt("pointsAction");

            Spell spell = new Spell(nom, element, pointsAction, baseDmg, portee);
            spells.add(spell);
        }

        return spells;
    }

    public static boolean createCharacter(String characterName) {
        try (Connection conn = getConnection()) {
            // Insertion du nom du personnage dans la table Character
            String insertCharacterQuery = "INSERT INTO Character (name, pointsAction, pointsMouvement) VALUES (?, 10, 5)";
            PreparedStatement stmt = conn.prepareStatement(insertCharacterQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, characterName);
            stmt.executeUpdate();

            // Récupération de l'ID généré pour le personnage
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int characterId = -1;
            if (generatedKeys.next()) {
                characterId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating character failed, no ID obtained.");
            }

            // Insertion des valeurs par défaut dans CharacterStats
            String insertDefaultStatsQuery = "INSERT INTO CharacterStats (uniqueId, fire, water, wind, earth, neutral, hitpoints) VALUES (?, 10, 10, 10, 10, 10, 100)";
            PreparedStatement insertDefaultStatsStmt = conn.prepareStatement(insertDefaultStatsQuery);
            insertDefaultStatsStmt.setInt(1, characterId);
            insertDefaultStatsStmt.executeUpdate();

            // Insertion des valeurs par défaut dans CharacterAttacks
            String insertDefaultAttacksQuery = "INSERT INTO CharacterAttacks (uniqueId, characterId, spellId) VALUES (?, ?, ?)";
            PreparedStatement insertDefaultAttacksStmt = conn.prepareStatement(insertDefaultAttacksQuery);
            int defaultSpellId = 1; // ID de l'arme par défaut
            insertDefaultAttacksStmt.setInt(1, characterId);
            insertDefaultAttacksStmt.setInt(2, characterId);
            insertDefaultAttacksStmt.setInt(3, defaultSpellId);
            insertDefaultAttacksStmt.executeUpdate();

            // Mise à jour du personnage avec les stats et attaques
            String updateCharacterQuery = "UPDATE Character SET stats = ?, attacks = ? WHERE uniqueId = ?";
            PreparedStatement updateCharacterStmt = conn.prepareStatement(updateCharacterQuery);
            updateCharacterStmt.setInt(1, characterId); // statsId est le même que characterId
            updateCharacterStmt.setInt(2, characterId); // attacksId est le même que characterId
            updateCharacterStmt.setInt(3, characterId);
            updateCharacterStmt.executeUpdate();

            // Création du personnage avec les données récupérées
            ArrayList<Spell> listSpells = loadSpells(conn, defaultSpellId); // Méthode pour charger les sorts
            Character character = new Character(characterName, 10, 100, 5, 10, 10, 10, 10, 10, listSpells);

            // À ce stade, vous avez créé avec succès un personnage avec les données de la
            // base de données
            // Vous pouvez faire d'autres manipulations ou simplement retourner true pour
            // indiquer que la création a réussi
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void displayCharacter(int characterId) {
        String characterInfo = getCharacterInfo(characterId);
        if (characterInfo != null) {
            System.out.println(characterInfo);
        } else {
            System.out.println("Character with ID " + characterId + " not found.");
        }
    }

    private static String getCharacterInfo(int characterId) {
        try (Connection conn = getConnection()) {
            String query = "SELECT c.name, c.pointsAction, c.pointsMouvement, " +
                    "cs.fire, cs.water, cs.wind, cs.earth, cs.neutral, cs.hitpoints " +
                    "FROM Character c " +
                    "JOIN CharacterStats cs ON c.stats = cs.uniqueId " +
                    "WHERE c.uniqueId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, characterId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("name");
                    int pointsAction = rs.getInt("pointsAction");
                    int pointsMouvement = rs.getInt("pointsMouvement");
                    int fire = rs.getInt("fire");
                    int water = rs.getInt("water");
                    int wind = rs.getInt("wind");
                    int earth = rs.getInt("earth");
                    int neutral = rs.getInt("neutral");
                    int hitpoints = rs.getInt("hitpoints");

                    return String.format(
                            "Character ID: %d\nName: %s\nPoints Action: %d\nPoints Mouvement: %d\nFire: %d\nWater: %d\nWind: %d\nEarth: %d\nNeutral: %d\nHitpoints: %d",
                            characterId, name, pointsAction, pointsMouvement, fire, water, wind, earth, neutral,
                            hitpoints);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Character getCharacter(int characterId) {
        try (Connection conn = getConnection()) {
            String selectCharacterQuery = "SELECT c.uniqueId, c.name, c.pointsAction, c.pointsMouvement, " +
                    "cs.fire, cs.water, cs.wind, cs.earth, cs.neutral, cs.hitpoints, " +
                    "ca.spellId " +
                    "FROM Character c " +
                    "JOIN CharacterStats cs ON c.stats = cs.uniqueId " +
                    "JOIN CharacterAttacks ca ON c.uniqueId = ca.characterId " +
                    "WHERE c.uniqueId = ?";
            PreparedStatement stmt = conn.prepareStatement(selectCharacterQuery);
            stmt.setInt(1, characterId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                int pointsAction = rs.getInt("pointsAction");
                int pointsMouvement = rs.getInt("pointsMouvement");
                int fire = rs.getInt("fire");
                int water = rs.getInt("water");
                int wind = rs.getInt("wind");
                int earth = rs.getInt("earth");
                int neutral = rs.getInt("neutral");
                int hitpoints = rs.getInt("hitpoints");
                int spellId = rs.getInt("spellId");

                // Charger les sorts
                ArrayList<Spell> spells = loadSpells(conn, spellId);

                // Créer le personnage
                Character character = new Character(name, pointsAction, hitpoints, pointsMouvement, fire, water, wind,
                        earth, neutral, spells);

                return character;
            } else {
                throw new SQLException("Character with ID " + characterId + " not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getLastInsertedCharacterId() {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT uniqueId FROM Character ORDER BY uniqueId DESC LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("uniqueId");
            } else {
                throw new SQLException("No characters found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Méthode pour augmenter les attributs du personnage
    public static void increaseAttributes(int characterId) {
        try (Connection conn = getConnection()) {
            String updateStatsQuery = "UPDATE CharacterStats SET fire = fire + 1, water = water + 1, wind = wind + 1, earth = earth + 1, neutral = neutral + 1 WHERE uniqueId = ?";
            PreparedStatement updateStatsStmt = conn.prepareStatement(updateStatsQuery);
            updateStatsStmt.setInt(1, characterId);
            updateStatsStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void startNewGame() {

    }

    public static void loadGame() {
        // Logique pour charger une partie existante
    }
}