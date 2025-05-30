/** Main Game Engine
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GameEngine {

    // --- Constants ---
    private static final int MAP_SIZE = 10;
    private static final int MAX_STEPS_ALLOWED = 100;
    private static final String SAVE_FILENAME = "minidungeon.save";
    private static final String TOP_SCORES_FILENAME = "topscores.dat";
    private static final int MAX_TOP_SCORES = 5;

    // --- Instance Fields ---
    private GameState state;
    private final int initialDifficulty;
    private int playerStartX = MAP_SIZE - 1; // Default for Level 1 start
    private int playerStartY = 0;            // Default for Level 1 start
    private List<ScoreEntry> topScores;

    // --- Constructor ---
    public GameEngine(int difficulty) {
        this.initialDifficulty = difficulty;
        this.topScores = new ArrayList<>();
        loadTopScores(); // Load existing scores when the engine is created
    }

    // --- Game Lifecycle Methods ---
    public void startNewGame() {
        this.playerStartX = MAP_SIZE - 1; // Reset to Level 1 start position
        this.playerStartY = 0;
        this.state = new GameState(MAP_SIZE, this.initialDifficulty);
        this.state.setLevel(1);
        Player player = new Player(playerStartX, playerStartY);
        this.state.setPlayer(player);
        generateLevel(playerStartX, playerStartY);
        System.out.println("Game started. Level 1. Difficulty: " + this.state.getDifficulty());
    }

    public boolean advanceToNextLevel() {
        if (state.getLevel() == 1) {
            int ladderX = state.getPlayerX();
            int ladderY = state.getPlayerY();
            int currentScore = state.getPlayer().getScore();
            int currentHp = state.getPlayer().getHp();
            int currentDifficulty = state.getDifficulty();
            int nextLevelDifficulty = Math.min(currentDifficulty + 2, 10); // Increase difficulty, capped at 10

            this.playerStartX = ladderX; // Next level starts where ladder was
            this.playerStartY = ladderY;

            this.state = new GameState(MAP_SIZE, nextLevelDifficulty); // Create new state for Level 2
            this.state.setLevel(2);
            Player player = new Player(playerStartX, playerStartY); // New player instance for new state
            player.setScore(currentScore); // Carry over score
            player.setHp(currentHp);       // Carry over HP
            this.state.setPlayer(player);

            generateLevel(playerStartX, playerStartY); // Generate Level 2 map
            state.getAndClearTurnMessages(); // Clear any residual messages
            String advanceMessage = "Advanced to Level 2! New Difficulty: " + this.state.getDifficulty();
            state.addTurnMessage(advanceMessage);
            System.out.println(advanceMessage); // Also print to console for text mode
            return true;
        }
        return false;
    }

    private void generateLevel(int pStartX, int pStartY) {
        Entity[][] map = state.getMap();
        Player player = state.getPlayer();
        state.setSteps(0); // Reset steps for the new level

        // Clear map
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                map[i][j] = null;
            }
        }
        state.setPlayerPosition(pStartX, pStartY); // Set player's logical position in GameState
        if (player != null) {
            player.setPosition(pStartX, pStartY); // Also update Player object's internal position
        }

        if (state.getLevel() == 1) {
            // pStartX and pStartY are the entry coordinates for Level 1 (e.g., bottom-left)
            map[pStartX][pStartY] = new Entry();
            System.out.println("DEBUG: Entry object placed at (" + pStartX + "," + pStartY + ") for Level 1."); // Optional debug
        }

        // Place items (assignment doesn't require 'E' symbol if player just starts there)
        placeRandomItems(map, new Gold(), 5, pStartX, pStartY);
        placeRandomItems(map, new Trap(), 5, pStartX, pStartY);
        placeRandomItems(map, new MeleeMutant(), 3, pStartX, pStartY);
        int rangedMutantCount = state.getDifficulty();
        placeRandomItems(map, new RangedMutant(), rangedMutantCount, pStartX, pStartY);
        placeRandomItems(map, new HealthPotion(), 2, pStartX, pStartY);
        placeRandomItems(map, new Ladder(), 1, pStartX, pStartY);
    }

    private void placeRandomItems(Entity[][] map, Entity itemType, int count, int playerAvoidX, int playerAvoidY) {
        Random rand = new Random();
        int itemsPlaced = 0;
        int attempts = 0; // To prevent infinite loop on very full maps or impossible conditions
        while (itemsPlaced < count && attempts < MAP_SIZE * MAP_SIZE * 2) {
            int x = rand.nextInt(MAP_SIZE);
            int y = rand.nextInt(MAP_SIZE);
            boolean isPlayerStartCell = (x == playerAvoidX && y == playerAvoidY);
            // Prevent placing ladder on fixed L1 entry if itemType is Ladder and current level is 1
            boolean isL1EntryAndLadder = (state.getLevel() == 1 && itemType instanceof Ladder && x == MAP_SIZE -1 && y == 0);

            if (map[x][y] == null && !isPlayerStartCell && !isL1EntryAndLadder) {
                // Create new instances for each item
                if (itemType instanceof Gold) map[x][y] = new Gold();
                else if (itemType instanceof Trap) map[x][y] = new Trap();
                else if (itemType instanceof MeleeMutant) map[x][y] = new MeleeMutant();
                else if (itemType instanceof RangedMutant) map[x][y] = new RangedMutant();
                else if (itemType instanceof HealthPotion) map[x][y] = new HealthPotion();
                else if (itemType instanceof Ladder) map[x][y] = new Ladder();
                // Add other entity types here if necessary
                itemsPlaced++;
            }
            attempts++;
        }
        if (itemsPlaced < count) {
            System.err.println("Warning: Could not place all " + count + " instances of " + itemType.getClass().getSimpleName());
        }
    }

    // --- Game State Accessors & Mutators ---
    public Entity[][] getMapEntities() { return state.getMap(); }
    public int getPlayerX() { return state.getPlayerX(); }
    public int getPlayerY() { return state.getPlayerY(); }
    public Player getPlayer() { return state.getPlayer(); }
    public GameState getState() { return state; }
    public int getSteps() { return state.getSteps(); }
    public int getMaxSteps() { return MAX_STEPS_ALLOWED; }

    // --- Game Logic Methods ---
    public List<String> handlePlayerMove(Direction direction) {
        if (isGameOver() || hasWonGame()) {
            // Return a modifiable list if messages might be added later, or ensure this is handled
            List<String> endMessages = new ArrayList<>();
            endMessages.add("Game is over, cannot move.");
            return endMessages;
        }
        state.movePlayer(direction); // GameState updates its internal messages
        return state.getAndClearTurnMessages(); // Retrieve and clear them
    }

    public boolean isGameOver() {
        if (state == null || state.getPlayer() == null) return true; // Not yet initialized
        if (!state.getPlayer().isAlive()) return true; // Player HP <= 0
        return state.getSteps() >= MAX_STEPS_ALLOWED; // Max steps reached
    }

    public boolean hasWonGame() {
        return state != null && state.getLevel() == 2 && state.hasReachedLadderThisTurn();
    }

    // --- Save/Load Game State ---
    public void saveGameState() {
        if (this.state == null) {
            System.err.println("GameEngine: Cannot save, game state is null.");
            return;
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILENAME))) {
            oos.writeObject(this.state);
            System.out.println("Game state saved to " + SAVE_FILENAME);
            state.addTurnMessage("Game saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving game state: " + e.getMessage());
            e.printStackTrace();
            state.addTurnMessage("Error: Could not save game. " + e.getMessage());
        }
    }

    public boolean loadGameState() {
        File saveFile = new File(SAVE_FILENAME);
        if (!saveFile.exists()) {
            System.err.println("Load game: Save file not found - " + SAVE_FILENAME);
            // Controller will need to inform user, or this method can add a message to a temporary state
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILENAME))) {
            GameState loadedState = (GameState) ois.readObject();
            if (loadedState != null) {
                this.state = loadedState; // Replace current state
                System.out.println("Game state loaded from " + SAVE_FILENAME);
                this.state.addTurnMessage("Game loaded successfully.");
                return true;
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            System.err.println("Error loading game state: " + e.getMessage());
            e.printStackTrace();
        }
        // If loading failed, add a message to current state if it exists
        if (this.state != null) { // this.state might be null if called before startNewGame
            this.state.addTurnMessage("Error: Could not load game. Save file might be corrupt or incompatible.");
        } else { // If state is null, create a temporary one to hold the message for the GUI
            GameState tempState = new GameState(MAP_SIZE, initialDifficulty); // Or a default size/diff
            tempState.addTurnMessage("Error: Could not load game. Save file might be corrupt or incompatible.");
            this.state = tempState; // So GUI can fetch this message
        }
        return false;
    }

    // --- Top Score Management ---
    @SuppressWarnings("unchecked")
    private void loadTopScores() {
        File scoresFile = new File(TOP_SCORES_FILENAME);
        if (scoresFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(scoresFile))) {
                Object loadedObject = ois.readObject();
                if (loadedObject instanceof List) {
                    this.topScores = (List<ScoreEntry>) loadedObject;
                    this.topScores.removeIf(java.util.Objects::isNull); // Sanitize
                    Collections.sort(this.topScores);
                    System.out.println("Top scores loaded from " + TOP_SCORES_FILENAME + ". Count: " + this.topScores.size());
                } else {
                    this.topScores = new ArrayList<>(); // Initialize if file format is wrong
                    System.err.println("Error loading top scores: File content is not a List.");
                }
            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                System.err.println("Error loading top scores: " + e.getMessage());
                this.topScores = new ArrayList<>();
            }
        } else {
            System.out.println("No top scores file found (" + TOP_SCORES_FILENAME + "). Starting with an empty list.");
            this.topScores = new ArrayList<>();
        }
    }

    private void saveTopScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TOP_SCORES_FILENAME))) {
            oos.writeObject(this.topScores);
            System.out.println("Top scores saved to " + TOP_SCORES_FILENAME);
        } catch (IOException e) {
            System.err.println("Error saving top scores: " + e.getMessage());
        }
    }

    // Inside GameEngine.java

    public boolean isTopScore(int currentScore) {
        if (currentScore == -1) { // Explicitly exclude -1 scores
            return false;
        }
        if (topScores.size() < MAX_TOP_SCORES) {
            return true; // Always a top score if the list isn't full
        }
        // List is full, check if current score is higher than the lowest score in the top 5
        // Assuming topScores is sorted descending, the last element is the lowest among the top.
        return currentScore > topScores.get(MAX_TOP_SCORES - 1).getScore(); // Use .getScore() here
    }

    public void addPlayerScore(String playerName, int score, LocalDate date) {
        if (score == -1) {
            System.out.println("Attempted to add a score of -1 to top scores. Aborted.");
            return;
        }
        topScores.add(new ScoreEntry(playerName, score, date));
        Collections.sort(topScores); // Sorts descending due to ScoreEntry.compareTo
        while (topScores.size() > MAX_TOP_SCORES) {
            topScores.removeLast();
        }
        saveTopScores();
    }

    public List<ScoreEntry> getTopScores() {
        return new ArrayList<>(this.topScores); // Return a defensive copy
    }

    // --- Text Mode Game ---
    private void printTextHelp() {
        System.out.println("\n--- MiniDungeon Help ---");
        System.out.println("Goal: Achieve the highest score and escape the dungeon.");
        System.out.println("Controls: Type 'u' (up), 'd' (down), 'l' (left), 'r' (right) to move.");
        System.out.println("  Each move costs 1 step. Max steps: 100.");
        System.out.println("Player Stats: Starts with 10 HP. If HP reaches 0, game over (score -1).");
        System.out.println("Items & Interactions:");
        System.out.println("  P: Player (You!)");
        System.out.println("  G: Gold (+2 score, item removed)");
        System.out.println("  H: Health Potion (+4 HP, max 10 HP, item removed)");
        System.out.println("  T: Trap (-2 HP, trap remains active!)");
        System.out.println("  M: Melee Mutant (-2 HP, +2 score, mutant removed)");
        System.out.println("  R: Ranged Mutant (Attacks from 2 tiles, 50% chance, -2 HP.");
        System.out.println("     Defeat by stepping on it: +2 score, no HP loss, mutant removed)");
        System.out.println("  L: Ladder (Advance to next level or exit game)");
        System.out.println("  E: Entry point");
        System.out.println("Levels: Two levels. Reaching Ladder on L1 -> L2 (difficulty +2). Ladder on L2 -> Win!");
        System.out.println("Game Features (GUI): Save, Load, Top Scores, Help.");
        System.out.println("------------------------\n");
    }

    public void playTextGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWelcome to MiniDungeon (Text Mode)!");
        printTextHelp(); // Show help at the start

        while (true) {
            state.printMap();
            System.out.printf("Level: %d | HP: %d | Score: %d | Steps: %d/%d | Difficulty: %d\n",
                    state.getLevel(), state.getPlayer().getHp(), state.getPlayer().getScore(),
                    state.getSteps(), MAX_STEPS_ALLOWED, state.getDifficulty());

            List<String> messages = state.getAndClearTurnMessages();
            for (String msg : messages) {
                System.out.println("MESSAGE: " + msg);
            }

            if (isGameOver()) {
                if (!state.getPlayer().isAlive()) System.out.println("Game Over - You died.");
                else System.out.println("Game Over - You ran out of steps.");
                state.getPlayer().setScore(-1);
                System.out.println("Final Score: " + state.getPlayer().getScore());
                break;
            }

            if (hasWonGame()) {
                System.out.println("Congratulations! You escaped the dungeon from Level 2!");
                System.out.println("Final Score: " + state.getPlayer().getScore());
                // Text mode doesn't currently prompt for name for top scores
                if (isTopScore(state.getPlayer().getScore())) {
                    System.out.println("You made it into the Top 5!");
                    // For simplicity, text mode doesn't take name input here, but engine methods support it
                }
                break;
            }

            if (state.hasReachedLadderThisTurn() && state.getLevel() == 1) {
                advanceToNextLevel();
                continue;
            }

            System.out.print("Move (u/d/l/r), 'help', or 'q' to quit: ");
            String moveInput = scanner.nextLine().trim().toLowerCase();
            Direction dir;

            switch (moveInput) {
                case "u": dir = Direction.UP; break;
                case "d": dir = Direction.DOWN; break;
                case "l": dir = Direction.LEFT; break;
                case "r": dir = Direction.RIGHT; break;
                case "help": printTextHelp(); continue;
                case "q":
                    System.out.println("Quitting game. Final Score: " + state.getPlayer().getScore());
                    scanner.close();
                    return;
                default: System.out.println("Invalid input."); continue;
            }

            state.movePlayer(dir);
        }
        scanner.close();
    }

    public static void main(String[] args) {
        Scanner consoleScanner = new Scanner(System.in); // Renamed to avoid conflict
        int difficulty = 3;
        System.out.print("Enter difficulty for MiniDungeon (0-10, default 3): ");
        if (consoleScanner.hasNextInt()) {
            int inputDiff = consoleScanner.nextInt();
            if (inputDiff >= 0 && inputDiff <= 10) {
                difficulty = inputDiff;
            } else {
                System.out.println("Invalid difficulty value. Using default (3).");
            }
        } else {
            System.out.println("Invalid input type. Using default (3).");
        }
        consoleScanner.nextLine(); // Consume newline

        GameEngine engine = new GameEngine(difficulty);
        engine.startNewGame();
        engine.playTextGame();

        consoleScanner.close();
        System.out.println("\nText game finished. Thanks for playing!");
    }
}