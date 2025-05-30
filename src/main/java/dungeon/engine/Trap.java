/**
 * Represents a trap entity in the dungeon that harms the player upon interaction.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.engine;

import java.io.Serial;
import java.io.Serializable;

public class Trap implements Entity, Serializable {

    @Serial
    private static final long serialVersionUID = 20240530L; // Unique ID for serialization

    /**
     * Returns the map symbol for a trap.
     * @return 'T' character.
     */
    @Override
    public char getSymbol() {
        return 'T';
    }

    /**
     * Defines the interaction when a player steps on a trap.
     * Decreases the player's HP. The trap remains on the map.
     * @param player The player interacting with the trap.
     * @return String message describing the trap's effect.
     */
    @Override
    public String interact(Player player) {
        player.adjustHp(-2); // Trap deals 2 damage
        return "You fell into a trap! -2 HP.";
    }

    /**
     * Checks if the trap tile is passable.
     * Traps are considered passable as the player steps on them to trigger the effect.
     * @return true, indicating the trap tile can be stepped on.
     */
    @Override
    public boolean isPassable() {
        return true; // Player needs to step onto the trap's tile to trigger it.
        // The trap itself doesn't block movement onto its cell.
    }
}