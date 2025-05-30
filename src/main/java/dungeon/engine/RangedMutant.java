/**
 * Represents a stationary ranged mutant that can attack players from a distance.
 * Players can also defeat it by stepping directly onto its tile.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.engine;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;

public class RangedMutant implements Entity, Serializable {

    @Serial
    private static final long serialVersionUID = 20240530L; // Unique ID for serialization
    private final Random random = new Random(); // For determining attack success

    /**
     * Returns the map symbol for a ranged mutant.
     * @return 'R' character.
     */
    @Override
    public char getSymbol() {
        return 'R';
    }

    /**
     * Defines interaction when a player steps directly on the ranged mutant.
     * Player gains score; no HP is lost from this direct engagement.
     * The mutant is then typically removed by GameState.
     * @param player The player interacting with (defeating) the mutant.
     * @return String message describing the outcome.
     */
    @Override
    public String interact(Player player) {
        player.adjustScore(2);
        // Per assignment: No HP lost when defeating directly (stepping on it), +2 score.
        return "You attacked a ranged mutant and won. +2 score.";
    }

    /**
     * Checks if the ranged mutant's tile is passable.
     * It's passable because the player can step on it to defeat it directly.
     * @return true, indicating the tile can be stepped on for interaction.
     */
    @Override
    public boolean isPassable() {
        // Player needs to be able to step onto the mutant's tile to defeat it directly.
        return true;
    }

    /**
     * Checks if this mutant can attack the player from its current location.
     * Attacks if player is within 2 tiles horizontally or vertically (not on the same tile).
     * @param playerX Player's X-coordinate.
     * @param playerY Player's Y-coordinate.
     * @param mutantX This mutant's X-coordinate.
     * @param mutantY This mutant's Y-coordinate.
     * @return true if the mutant can attack, false otherwise.
     */
    public boolean canAttack(int playerX, int playerY, int mutantX, int mutantY) {
        int deltaX = Math.abs(playerX - mutantX);
        int deltaY = Math.abs(playerY - mutantY);

        // Attack if in the same row/column, 1 or 2 tiles away.
        if (playerX == mutantX && deltaY > 0 && deltaY <= 2) {
            return true;
        }
        return playerY == mutantY && deltaX > 0 && deltaX <= 2;
    }

    /**
     * Determines if the ranged mutant's attack attempt is successful.
     * @return true if the attack hits (50% chance), false otherwise.
     */
    public boolean tryAttack() {
        return random.nextDouble() < 0.5; // 50% chance to hit
    }
}