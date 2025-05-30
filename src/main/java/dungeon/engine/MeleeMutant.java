/**
 * Represents a stationary melee mutant in the dungeon.
 * Players interact by stepping on it, resulting in combat.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.engine;

import java.io.Serial;
import java.io.Serializable;

public class MeleeMutant implements Entity, Serializable {

    @Serial
    private static final long serialVersionUID = 20240530L; // Unique ID for serialization

    /**
     * Returns the map symbol for a melee mutant.
     * @return 'M' character.
     */
    @Override
    public char getSymbol() {
        return 'M';
    }

    /**
     * Defines the interaction when a player encounters a melee mutant.
     * The player loses HP, gains score, and the mutant is typically removed by GameState.
     * @param player The player fighting the mutant.
     * @return String message describing the outcome of the fight.
     */
    @Override
    public String interact(Player player) {
        player.adjustHp(-2);    // Player loses 2 HP
        player.adjustScore(2);  // Player gains 2 score
        return "You fought a melee mutant! -2 HP, +2 score.";
    }

    /**
     * Checks if the melee mutant's tile is passable.
     * It's considered passable because the player must step onto its tile to initiate combat.
     * @return true, indicating the tile can be stepped on for interaction.
     */
    @Override
    public boolean isPassable() {
        // Player needs to step onto the mutant's tile to trigger the interaction (fight).
        // After interaction, the mutant is removed from the map.
        return true;
    }
}