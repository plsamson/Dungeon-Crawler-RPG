-- Create table for User
CREATE TABLE User (
                      userId INTEGER PRIMARY KEY AUTOINCREMENT,
                      username VARCHAR(50) NOT NULL UNIQUE,
                      password VARCHAR(100) NOT NULL
);

-- Create table for Game
CREATE TABLE Game (
                      uniqueId INTEGER PRIMARY KEY AUTOINCREMENT,
                      characterId INT,
                      levelProgress INT,
                      FOREIGN KEY (characterId) REFERENCES Character(uniqueId)
);

-- Create table for UserGame
CREATE TABLE UserGame (
                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                          gameId INT,
                          userId INT,
                          FOREIGN KEY (gameId) REFERENCES Game(uniqueId),
                          FOREIGN KEY (userId) REFERENCES User(userId)
);

-- Create table for Spell
CREATE TABLE Spell (
                       uniqueId INTEGER PRIMARY KEY AUTOINCREMENT,
                       Nom VARCHAR(100),
                       element VARCHAR(50),
                       baseDmg INT,
                       portee INT,
                       pointsAction INT
);

-- Create table for Weapon
CREATE TABLE Weapon (
                        uniqueId INTEGER PRIMARY KEY AUTOINCREMENT,
                        type VARCHAR(50),
                        baseDmg INT,
                        portee INT,
                        pointsAction INT
);

-- Create table for CharacterStats
CREATE TABLE CharacterStats (
                                uniqueId INTEGER PRIMARY KEY AUTOINCREMENT,
                                fire INT,
                                water INT,
                                wind INT,
                                earth INT,
                                neutral INT,
                                hitpoints INT
);

-- Create table for Character
CREATE TABLE Character (
                           uniqueId INTEGER PRIMARY KEY AUTOINCREMENT,
                           name VARCHAR(255),
                           weapon INT,
                           stats INT,
                           pointsAction INT,
                           pointsMouvement INT,
                           userId INT,
                           attacks INT,
                           FOREIGN KEY (attacks) REFERENCES CharacterAttacks(uniqueId),
                           FOREIGN KEY (weapon) REFERENCES Weapon(uniqueId),
                           FOREIGN KEY (stats) REFERENCES CharacterStats(uniqueId),
                           FOREIGN KEY (userId) REFERENCES User(userId)
);

-- Create table for CharacterAttacks
CREATE TABLE CharacterAttacks (
                                  uniqueId INTEGER PRIMARY KEY AUTOINCREMENT,
                                  characterId INT,
                                  spellId INT,
                                  FOREIGN KEY (characterId) REFERENCES Character(uniqueId),
                                  FOREIGN KEY (spellId) REFERENCES Spell(uniqueId)
);

-- Insert default spells
INSERT INTO Spell (Nom, element, baseDmg, portee, pointsAction) VALUES ('fireball', 'fire', 9, 3, 3);
INSERT INTO Spell (Nom, element, baseDmg, portee, pointsAction) VALUES ('waterball', 'water', 6, 2, 2);
INSERT INTO Spell (Nom, element, baseDmg, portee, pointsAction) VALUES ('heal', 'water', -10, 4, 4);
INSERT INTO Spell (Nom, element, baseDmg, portee, pointsAction) VALUES ('windstrike', 'wind', 9, 3, 3);
INSERT INTO Spell (Nom, element, baseDmg, portee, pointsAction) VALUES ('earthspear', 'earth', 18, 6, 6);
INSERT INTO Spell (Nom, element, baseDmg, portee, pointsAction) VALUES ('weapon', 'neutral', 12, 4, 4);

-- Insert default weapon
INSERT INTO Weapon (type, baseDmg, portee, pointsAction) VALUES ('defaultWeapon', 10, 1, 3);
