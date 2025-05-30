/**
 * Represents a health potion in the dungeon that restores player HP upon collection.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.engine;

import java.io.Serial;
import java.io.Serializable;

public class HealthPotion implements Entity, Serializable {

    @Serial
    private static final long serialVersionUID = 20240530L; // Unique ID for serialization

    /**
     * Returns the map symbol for a health potion.
     * @return 'H' character.
     */
    @Override
    public char getSymbol() {
        return 'H';
    }

    /**
     * Defines the interaction when a player consumes a health potion.
     * Increases player's HP. The potion is then typically removed from the map by GameState.
     * @param player The player consuming the potion.
     * @return String message describing the action.
     */
    @Override
    public String interact(Player player) {
        player.adjustHp(4); // Restores 4 HP (Player.adjustHp handles max HP limit)
        return "You drank a health potion! +4 HP.";
    }

    /**
     * Checks if the health potion tile is passable.
     * Potions should be passable for the player to step on and consume them.
     * @return true, indicating the health potion tile can be stepped on.
     */
    @Override
    public boolean isPassable() {
        return true; // Player needs to be able to step onto the potion tile to consume it.
    }
}