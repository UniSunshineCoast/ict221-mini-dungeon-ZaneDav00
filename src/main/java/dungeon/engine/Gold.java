/**
 * Represents a gold item in the dungeon that players can collect for score.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.engine;

import java.io.Serial;
import java.io.Serializable;

public class Gold implements Entity, Serializable {

    @Serial
    private static final long serialVersionUID = 20240530L; // Unique ID for serialization

    /**
     * Returns the map symbol for gold.
     * @return 'G' character.
     */
    @Override
    public char getSymbol() {
        return 'G';
    }

    /**
     * Defines the interaction when a player collects gold.
     * Increases player's score. The gold is then typically removed from the map by GameState.
     * @param player The player collecting the gold.
     * @return String message describing the action.
     */
    @Override
    public String interact(Player player) {
        player.adjustScore(2);
        return "You picked up gold! +2 score.";
    }

    /**
     * Checks if the gold tile is passable.
     * Gold should be passable for the player to step on it and collect it.
     * @return true, indicating the gold tile can be stepped on.
     */
    @Override
    public boolean isPassable() {
        return true; // Player needs to be able to step onto the gold tile to pick it up.
    }
}