package game.database;
import game.model.Character;
import java.util.Scanner;


public class TestSaveLoad {

    protected static Character characterTest;

    protected static String username;
    protected static String password;
    protected static int characterTestID;
    protected static int gameId;
    protected static int userId;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // On utilise un compte existant puisque la création de compte n'est pas l'objectif principal ici.
        System.out.print("Entrez votre nom d'utilisateur: ");
        username = scanner.next();
        System.out.print("Entrez votre mot de passe: ");
        password = scanner.next();

        if (Database.checkUser(username, password)) {

            // Création du caractère
            Database.createCharacter("Test");
            characterTestID = Database.getLastInsertedCharacterId();
            characterTest = Database.getCharacter(characterTestID);



        System.out.println("Choisissez une option:");
        System.out.println("1. Nouvelle partie");
        System.out.println("2. Charger une partie");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                NewGame(scanner);
                break;
            case 2:
                LoadGame(scanner);
                break;
            default:
                System.out.println("Choix invalide.");
                break;
        }

    }else {
        System.out.println("Invalid username or password");
    }
    }

    public static void NewGame(Scanner scanner) {

        // Faire une insertion dans la table game pour indiquer la nouvelle partie.
        Database.startGame(characterTestID,1);
        userId = Database.getUserId(username, password);
        gameId = Database.getLastInsertedGameId();
        Database.displayCharacter(characterTestID);

        System.out.println("Résultat de la partie? (1 pour gagner, 2 pour perdre)");
        int resultat = scanner.nextInt();

        if (resultat == 1) {
            System.out.println("Le joueur a gagné.");
            System.out.println("Augmentation des attributs...");

            Database.increaseAttributes(characterTestID);
            Database.displayCharacter(characterTestID);

            System.out.println("Les attributs ont été augmentés. Voulez-vous sauvegarder ou terminer?");
            System.out.println("1. Sauvegarder");
            System.out.println("2. Terminer");
            int saveChoice = scanner.nextInt();

            if (saveChoice == 1) {

                Database.saveGame(gameId, userId);
                System.out.println("Jeu sauvegardé.");
            } else {
                System.out.println("Jeu terminé.");
            }
        } else if (resultat == 2) {
            System.out.println("Le joueur a perdu.");
            System.out.println("Voulez-vous arrêter le jeu ou recommencer?");
            System.out.println("1. Terminer");
            System.out.println("2. Recommencer");
            int endChoice = scanner.nextInt();

            if (endChoice == 1) {
                System.out.println("Jeu terminé.");
            } else {
                System.out.println("Recommencer...");
                NewGame(scanner);
            }
        } else {
            System.out.println("Choix invalide.");
        }
    }

    private static void LoadGame(Scanner scanner) {
        userId = Database.getUserId(username, password);
        gameId = Database.getLastInsertedGameId();

        // Sauvegarde existe ou non.
        if (Database.checkUserGame(gameId, userId)) {

            System.out.println("Chargement en cours:");
            characterTestID = Database.getCharacterIdByGameId(gameId);

            Database.displayCharacter(characterTestID);

            System.out.println("Merci de votre participation");

        } else {
            System.out.println("No game found");
        }
    }




}
