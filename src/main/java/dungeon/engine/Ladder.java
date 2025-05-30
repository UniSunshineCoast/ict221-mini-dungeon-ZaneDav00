/**
 * Represents a ladder in the dungeon for level progression.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.engine;

import java.io.Serial;
import java.io.Serializable;

public class Ladder implements Entity, Serializable {

    @Serial
    private static final long serialVersionUID = 20240530L;

    /**
     * Returns the map symbol for the ladder.
     * @return 'L' character.
     */
    @Override
    public char getSymbol() {
        return 'L';
    }

    /**
     * Interaction message when player steps on the ladder.
     * @param player The interacting player.
     * @return Interaction message.
     */
    @Override
    public String interact(Player player) {
        return "You climbed the ladder!";
    }

    /**
     * Checks if the ladder tile is passable.
     * @return true, as players must step on ladders.
     */
    @Override
    public boolean isPassable() {
        return true;
    }
}