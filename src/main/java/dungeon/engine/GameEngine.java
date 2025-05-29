package dungeon.engine;

import java.util.Random;
import java.util.Scanner;
import java.util.List;

public class GameEngine {

    private GameState state;
    private static final int MAP_SIZE = 10;
    private static final int MAX_STEPS_ALLOWED = 100; // Renamed for clarity
    private int initialDifficulty;
    private int playerStartX = MAP_SIZE - 1; // Default for Level 1
    private int playerStartY = 0;            // Default for Level 1

    public GameEngine(int difficulty) {
        this.initialDifficulty = difficulty;
        // state is initialized in startNewGame or advanceToNextLevel
    }

    public void startNewGame() {
        this.playerStartX = MAP_SIZE - 1; // Bottom-left for level 1
        this.playerStartY = 0;
        this.state = new GameState(MAP_SIZE, this.initialDifficulty);
        this.state.setLevel(1);
        Player player = new Player(playerStartX, playerStartY); // Player created here
        this.state.setPlayer(player); // Set player in GameState
        generateLevel(playerStartX, playerStartY);
        System.out.println("Game started. Level 1. Difficulty: " + this.state.getDifficulty());
    }

    public boolean advanceToNextLevel() {
        if (state.getLevel() == 1) {
            int ladderX = state.getPlayerX(); // Player is on the ladder
            int ladderY = state.getPlayerY();

            int currentScore = state.getPlayer().getScore();
            int currentHp = state.getPlayer().getHp();
            int currentDifficulty = state.getDifficulty();

            int nextLevelDifficulty = Math.min(currentDifficulty + 2, 10); // Increase difficulty, max 10

            // Prepare for level 2
            this.playerStartX = ladderX; // Level 2 starts where ladder was
            this.playerStartY = ladderY;

            this.state = new GameState(MAP_SIZE, nextLevelDifficulty); // New state for new level
            this.state.setLevel(2);
            Player player = new Player(playerStartX, playerStartY); // Create player for new state
            player.setScore(currentScore); // Carry over score
            player.setHp(currentHp);       // Carry over HP
            this.state.setPlayer(player);  // Set player in GameState

            generateLevel(playerStartX, playerStartY);
            System.out.println("Advanced to Level 2! Difficulty: " + this.state.getDifficulty());
            return true; // Advanced to level 2
        }
        return false; // Cannot advance further or already on max level
    }


    // Getter for the raw map data (Entities)
    public Entity[][] getMapEntities() {
        return state.getMap();
    }

    public int getPlayerX() {
        return state.getPlayerX();
    }

    public int getPlayerY() {
        return state.getPlayerY();
    }

    public Player getPlayer() {
        return state.getPlayer();
    }

    public GameState getState() { // Expose state if controller needs more direct access (e.g. level)
        return state;
    }


    // This method is primarily for GUI interaction
    public List<String> handlePlayerMove(Direction direction) {
        if (state.isGameOver() || state.getSteps() >= MAX_STEPS_ALLOWED) {
            return List.of("Game is over, cannot move.");
        }
        state.movePlayer(direction); // GameState handles logic and internal messages
        return state.getAndClearTurnMessages(); // Return messages for GUI
    }


    private void generateLevel(int pStartX, int pStartY) {
        Entity[][] map = state.getMap(); // Get map from current state
        Player player = state.getPlayer(); // Get player from current state

        state.setSteps(0); // Reset steps for the level

        // Clear map
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                map[i][j] = null;
            }
        }

        // Player position is set based on pStartX, pStartY
        state.setPlayerPosition(pStartX, pStartY);
        player.setPosition(pStartX, pStartY); // Ensure player object also knows its position

        // For Level 1, place an Entry symbol. For Level 2, player just starts there.
        if (state.getLevel() == 1) {
            if (map[pStartX][pStartY] == null) { // Only place if empty (player is there conceptually)
                // map[pStartX][pStartY] = new Entry(); // Player IS the entry marker essentially
            } else {
                // This case should ideally not happen if pStartX,pStartY is clear for entry
                System.err.println("Warning: Player start position for Level 1 was not empty!");
            }
        }
        // Ensure the player's starting cell on the map is clear of other items initially
        // map[pStartX][pStartY] = null; // Or handled by player presence

        // Place random entities
        // Ensure items are not placed on the player's starting cell initially
        placeRandomItems(map, new Gold(), 5, pStartX, pStartY);
        placeRandomItems(map, new Trap(), 5, pStartX, pStartY);
        placeRandomItems(map, new MeleeMutant(), 3, pStartX, pStartY);

        // Ranged mutant count based on difficulty from GameState
        int rangedMutantCount = state.getDifficulty();
        placeRandomItems(map, new RangedMutant(), rangedMutantCount, pStartX, pStartY);

        placeRandomItems(map, new HealthPotion(), 2, pStartX, pStartY);

        // Place Ladder (not on player's start cell)
        placeRandomItems(map, new Ladder(), 1, pStartX, pStartY);
    }

    private void placeRandomItems(Entity[][] map, Entity itemType, int count, int playerAvoidX, int playerAvoidY) {
        Random rand = new Random();
        int placed = 0;
        for (int i = 0; i < count; ) { // Loop until 'count' items are placed
            int x = rand.nextInt(MAP_SIZE);
            int y = rand.nextInt(MAP_SIZE);
            // Check if cell is empty AND not the player's starting cell OR an already placed item.
            // Also, for level 1, entry is fixed, ladder is random.
            // For level 2, entry is previous ladder, ladder is random again.
            boolean isPlayerStartCell = (x == playerAvoidX && y == playerAvoidY);
            boolean isEntryCellForLevel1 = (state.getLevel() == 1 && x == MAP_SIZE -1 && y == 0);

            // Prevent placing items on player's current start or fixed entry for L1
            if (map[x][y] == null && !isPlayerStartCell) {
                if (state.getLevel() == 1 && itemType instanceof Ladder && isEntryCellForLevel1) {
                    continue; // Don't place ladder on fixed L1 entry
                }
                // For itemType, create a new instance for each placement if they have state
                // For now, assuming entities are stateless or one instance is fine for type.
                // If entities had unique state (e.g. a mutant with its own HP), you'd do:
                // map[x][y] = createNewInstanceOf(itemType);
                if (itemType instanceof Gold) map[x][y] = new Gold();
                else if (itemType instanceof Trap) map[x][y] = new Trap();
                else if (itemType instanceof MeleeMutant) map[x][y] = new MeleeMutant();
                else if (itemType instanceof RangedMutant) map[x][y] = new RangedMutant();
                else if (itemType instanceof HealthPotion) map[x][y] = new HealthPotion();
                else if (itemType instanceof Ladder) map[x][y] = new Ladder();
                else map[x][y] = itemType; // Fallback, but better to instantiate
                i++;
            }
            // Add a counter to prevent infinite loops if map is too full, though unlikely with 10x10
        }
    }

    public int getSteps() {
        return state.getSteps();
    }
    public int getMaxSteps() {
        return MAX_STEPS_ALLOWED;
    }

    public boolean isGameOver() {
        if (state == null || state.getPlayer() == null) return true; // Not initialized
        if (!state.getPlayer().isAlive()) {
            return true;
        }
        if (state.getSteps() >= MAX_STEPS_ALLOWED) {
            return true;
        }
        return false;
    }

    public boolean hasWonGame() {
        // Win if player reached ladder on level 2
        return state.getLevel() == 2 && state.hasReachedLadderThisTurn();
    }


    public void playTextGame() {
        Scanner scanner = new Scanner(System.in);
        startNewGame(); // Initialize and start level 1

        System.out.println("Welcome to MiniDungeon (Text Mode)!");

        while (true) {
            state.printMap();
            System.out.printf("Level: %d | HP: %d | Score: %d | Steps: %d/%d | Difficulty: %d\n",
                    state.getLevel(), state.getPlayer().getHp(), state.getPlayer().getScore(),
                    state.getSteps(), MAX_STEPS_ALLOWED, state.getDifficulty());

            // Display messages from the last turn
            List<String> messages = state.getAndClearTurnMessages(); // Important to clear them from GameState
            for (String msg : messages) {
                System.out.println("MESSAGE: " + msg);
            }

            if (isGameOver()) {
                if (!state.getPlayer().isAlive()){
                    System.out.println("Game Over - You died.");
                } else if (state.getSteps() >= MAX_STEPS_ALLOWED) {
                    System.out.println("Game Over - You ran out of steps.");
                }
                state.getPlayer().setScore(-1); // Set score to -1 on loss
                System.out.println("Final Score: " + state.getPlayer().getScore());
                break;
            }

            if (hasWonGame()){
                System.out.println("Congratulations! You escaped the dungeon from Level 2!");
                System.out.println("Final Score: " + state.getPlayer().getScore());
                // Potentially trigger top score saving here
                break;
            }

            if (state.hasReachedLadderThisTurn() && state.getLevel() == 1) {
                System.out.println("You reached the ladder! Advancing to Level 2...");
                advanceToNextLevel();
                state.getAndClearTurnMessages(); // Clear any messages from ladder interaction itself
                continue; // Restart loop for new level's display
            }


            System.out.print("Move (u/d/l/r) or q to quit: ");
            String moveInput = scanner.nextLine().trim().toLowerCase();
            Direction dir = null;
            switch (moveInput) {
                case "u": dir = Direction.UP; break;
                case "d": dir = Direction.DOWN; break;
                case "l": dir = Direction.LEFT; break;
                case "r": dir = Direction.RIGHT; break;
                case "q": System.out.println("Quitting game. Final Score: " + state.getPlayer().getScore()); return;
                default: System.out.println("Invalid input. Use u/d/l/r or q."); continue;
            }

            if (dir != null) {
                state.movePlayer(dir); // This now populates turnMessages in state
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        // Get difficulty from user (0-10, default 3)
        Scanner sc = new Scanner(System.in);
        int difficulty = 3; // Default
        System.out.print("Enter difficulty (0-10, default 3): ");
        if (sc.hasNextInt()) {
            int inputDiff = sc.nextInt();
            if (inputDiff >= 0 && inputDiff <= 10) {
                difficulty = inputDiff;
            } else {
                System.out.println("Invalid difficulty, using default 3.");
            }
        } else {
            System.out.println("No valid input, using default 3.");
        }
        // sc.nextLine(); // consume newline if needed before further input

        GameEngine engine = new GameEngine(difficulty);
        engine.playTextGame();
    }
}