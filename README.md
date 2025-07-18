<p align="center">
  <img src="src/main/resources/sprites/logo.png" alt="Dungeon Crawler RPG Logo" width="300" />
</p>

# Dungeon Crawler RPG

**Dungeon Crawler RPG** is a Java-based 2D dungeon crawler inspired by classic **Dungeon Crawl** roguelikes and the tactical, turn-based combat of **Dofus**.

---

## 🎮 Features

- **Tile-based 2D dungeon** exploration on a grid
- **Turn-based combat** with enemy AI using A* for efficient tile-based pathfinding
- **Multiple spells and attack types** with elemental affinities (fire, water, earth, air, etc.)
- **Procedurally scaling levels**: enemy difficulty increases indefinitely after each level
- **Account creation & login** system for saving and loading player profiles
- **Modular game logic**: Controller uses package `mecanique` (services) to manage models.
- **JavaFX-based GUI** initialized via `UIInitializer` and user input handled by `UIController`
- **JUnit tests** for core mechanics

---

## 🛠 Requirements

- **Java JDK 21** or newer
- **Maven 3.6+**
- **JavaFX 22**
- **SQLite JDBC 3.46**
- **JUnit 4.11** / **JUnit Jupiter 5.8.1** for testing

---

## 🚀 Installation & Running

1. **Clone the repository**

   `git clone https://github.com/plsamson/dungeon_crawler_rpg.git`

2. **Build the project**

   `mvn clean package`

3. **Run via Maven**

   `mvn javafx:run`
