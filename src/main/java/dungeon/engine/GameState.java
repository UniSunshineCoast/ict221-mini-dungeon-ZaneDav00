package dungeon.engine;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private Entity[][] map;
    private Player player;
    private int playerX, playerY;
    private int steps;
    private int level;
    private int difficulty; // Added difficulty
    private boolean ladderReachedThisTurn = false; // More specific flag

    // Messages generated per turn, for GUI/Text display later
    private List<String> turnMessages = new ArrayList<>();

    public GameState(int size, int initialDifficulty) {
        this.map = new Entity[size][size];
        this.level = 1; // Default to level 1
        this.difficulty = initialDifficulty;
        // Player should be set by GameEngine after GameState is created
    }

    public Entity[][] getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPlayerPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
        if (player != null) {
            player.setPosition(x, y);
        }
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) { // Added setter
        this.level = level;
    }

    public int getDifficulty() { // Added getter
        return difficulty;
    }

    public void setDifficulty(int difficulty) { // Added setter
        this.difficulty = difficulty;
    }


    public void advanceLevel(int newDifficulty) {
        level++;
        steps = 0; // Reset steps for the new level
        this.difficulty = newDifficulty; // Set new difficulty for the next level
        this.ladderReachedThisTurn = false; // Reset flag
        // Map regeneration and player placement will be handled by GameEngine
    }

    // Method to get and clear messages for the turn
    public List<String> getAndClearTurnMessages() {
        if (turnMessages.isEmpty()) {
            return List.of(); // Return empty immutable list if no messages
        }
        List<String> messages = new ArrayList<>(turnMessages);
        turnMessages.clear();
        return messages;
    }

    public void movePlayer(Direction dir) {
        if (player == null || !player.isAlive()) {
            turnMessages.add("Cannot move, player is not active.");
            return;
        }

        ladderReachedThisTurn = false; // Reset at the start of a move attempt
        turnMessages.clear(); // Clear messages from previous turn part

        int newX = playerX + dir.dx;
        int newY = playerY + dir.dy;

        if (newX >= 0 && newX < map.length && newY >= 0 && newY < map.length) {
            Entity entityOnNewCell = map[newX][newY];

            // Check for non-passable entities (e.g., Walls, if implemented)
            // For now, all current entities are passable or become passable after interaction.
            // If you add a Wall entity: if (entityOnNewCell instanceof Wall) { turnMessages.add("Blocked by a wall."); return; }

            // Player moves to the new cell
            setPlayerPosition(newX, newY);
            steps++;
            turnMessages.add("You moved " + dir.name().toLowerCase() + ".");


            if (entityOnNewCell != null) {
                String interactionMessage = entityOnNewCell.interact(player);
                if (interactionMessage != null && !interactionMessage.isEmpty()) {
                    turnMessages.add(interactionMessage);
                }

                if (entityOnNewCell instanceof Ladder) {
                    ladderReachedThisTurn = true;
                    // Don't remove the ladder here; GameEngine will handle level transition
                } else if (entityOnNewCell instanceof Trap) {
                    // Trap remains active, so DO NOT remove it from the map
                    if (!player.isAlive()) {
                        turnMessages.add("The trap was fatal!");
                    }
                } else {
                    // For other items like Gold, Potions, defeated Mutants: remove them
                    map[newX][newY] = null;
                }
            }

            // After player's action, check for Ranged Mutant attacks (if player is still alive)
            if (player.isAlive()) {
                checkForRangedMutantAttacks();
            }

        } else {
            turnMessages.add("Invalid move: you tried to move out of bounds.");
        }
    }

    private void checkForRangedMutantAttacks() {
        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {
                if (map[r][c] instanceof RangedMutant mutant) {
                    if (mutant.canAttack(playerX, playerY, r, c)) {
                        if (mutant.tryAttack()) {
                            player.adjustHp(-2);
                            String attackMsg = "A ranged mutant at (" + r + "," + c + ") hit you! -2 HP.";
                            turnMessages.add(attackMsg);
                            System.out.println(attackMsg); // For text UI, GUI should use turnMessages
                            if (!player.isAlive()) {
                                turnMessages.add("The ranged attack was fatal!");
                                System.out.println("The ranged attack was fatal!");
                                return; // Stop further attacks if player dies
                            }
                        } else {
                            String missMsg = "A ranged mutant at (" + r + "," + c + ") attacked but missed.";
                            turnMessages.add(missMsg);
                            System.out.println(missMsg); // For text UI
                        }
                    }
                }
            }
        }
    }

    public boolean hasReachedLadderThisTurn() {
        return ladderReachedThisTurn;
    }

    public boolean isGameOver() {
        // Game over if player is not alive or max steps reached (max steps check is usually in GameEngine)
        return player == null || !player.isAlive();
    }

    public void printMap() { // For text-based game
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                if (i == playerX && j == playerY) {
                    System.out.print("P ");
                } else if (map[i][j] != null) {
                    System.out.print(map[i][j].getSymbol() + " ");
                } else {
                    System.out.print(". "); // Empty cell
                }
            }
            System.out.println();
        }
    }
}