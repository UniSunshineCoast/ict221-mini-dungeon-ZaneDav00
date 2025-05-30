/**
 * Holds all current state information for a game session in MiniDungeon.
 * This includes the map, player, current level, steps, difficulty, and messages.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.engine;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // For Objects.requireNonNull

public class GameState implements Serializable {

    @Serial
    private static final long serialVersionUID = 20240530L; // Consistent serialVersionUID

    private final Entity[][] map;
    private Player player;
    private int playerX, playerY;
    private int steps;
    private int level;
    private int difficulty;
    private boolean ladderReachedThisTurn = false;

    private final List<String> turnMessages;

    /**
     * Constructs a new GameState.
     * @param size The size of the square map (e.g., 10 for a 10x10 map).
     * @param initialDifficulty The starting difficulty for the game.
     * @throws IllegalArgumentException if size is not positive.
     */
    public GameState(int size, int initialDifficulty) {
        if (size <= 0) {
            throw new IllegalArgumentException("Map size must be positive.");
        }
        this.map = new Entity[size][size];
        this.level = 1;
        this.difficulty = initialDifficulty;
        this.turnMessages = new ArrayList<>();
    }

    // --- Accessors ---
    public Entity[][] getMap() { return map; }
    public Player getPlayer() { return player; }
    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
    public int getSteps() { return steps; }
    public int getLevel() { return level; }
    public int getDifficulty() { return difficulty; }
    public boolean hasReachedLadderThisTurn() { return ladderReachedThisTurn; }

    // --- Mutators ---
    public void setPlayer(Player player) {
        this.player = Objects.requireNonNull(player, "Player cannot be null in GameState.");
    }

    public void setPlayerPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
        if (this.player != null) {
            this.player.setPosition(x, y);
        }
    }

    public void setSteps(int steps) {
        this.steps = Math.max(0, steps);
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    /**
     * Sets the current difficulty level of the game.
     * @param difficulty The new difficulty level.
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Reinitializes critical state aspects for advancing to a new level.
     * Typically called by the GameEngine.
     * @param newDifficulty The difficulty for the new level.
     */
    public void reinitializeForNextLevel(int newDifficulty) {
        this.level++;
        this.steps = 0;
        this.difficulty = newDifficulty;
        this.ladderReachedThisTurn = false;
        this.turnMessages.clear();
    }

    // --- Message Handling ---
    public void addTurnMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            this.turnMessages.add(message);
        }
    }

    public List<String> getAndClearTurnMessages() {
        if (turnMessages.isEmpty()) {
            return List.of();
        }
        List<String> currentMessages = new ArrayList<>(turnMessages);
        turnMessages.clear();
        return currentMessages;
    }

    // --- Core Game Logic: Player Movement & Interactions ---
    public void movePlayer(Direction dir) {
        Objects.requireNonNull(dir, "Direction cannot be null for movePlayer.");
        if (player == null || !player.isAlive()) {
            addTurnMessage("Cannot move, player is not active or null.");
            return;
        }

        ladderReachedThisTurn = false;
        turnMessages.clear(); // Clear messages for this new move action

        int newX = playerX + dir.dx;
        int newY = playerY + dir.dy;

        if (newX >= 0 && newX < map.length && newY >= 0 && newY < map.length) { // Check bounds
            Entity entityOnNewCell = map[newX][newY];
            setPlayerPosition(newX, newY);
            steps++;
            addTurnMessage("You moved " + dir.name().toLowerCase() + ".");

            if (entityOnNewCell != null) {
                String interactionMessage = entityOnNewCell.interact(player);
                addTurnMessage(interactionMessage);

                if (entityOnNewCell instanceof Ladder) {
                    ladderReachedThisTurn = true;
                } else if (entityOnNewCell instanceof Trap) {
                    if (!player.isAlive()) addTurnMessage("The trap was fatal!");
                } else {
                    map[newX][newY] = null; // Remove other consumables/defeated items
                }
            }

            if (player.isAlive()) {
                checkForRangedMutantAttacks();
            }
        } else {
            addTurnMessage("Invalid move: you tried to move out of bounds.");
        }
    }

    private void checkForRangedMutantAttacks() {
        if (player == null) return;
        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {
                if (map[r][c] instanceof RangedMutant mutant) {
                    if (mutant.canAttack(playerX, playerY, r, c)) {
                        if (mutant.tryAttack()) {
                            player.adjustHp(-2);
                            addTurnMessage("A ranged mutant at (" + r + "," + c + ") hit you! -2 HP.");
                            if (!player.isAlive()) {
                                addTurnMessage("The ranged attack was fatal!");
                                return;
                            }
                        } else {
                            addTurnMessage("A ranged mutant at (" + r + "," + c + ") attacked but missed.");
                        }
                    }
                }
            }
        }
    }

    // --- Text-Mode Display Utility ---
    public void printMap() {
        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {
                if (r == playerX && c == playerY) System.out.print("P ");
                else if (map[r][c] != null) System.out.print(map[r][c].getSymbol() + " ");
                else System.out.print(". ");
            }
            System.out.println();
        }
    }
}