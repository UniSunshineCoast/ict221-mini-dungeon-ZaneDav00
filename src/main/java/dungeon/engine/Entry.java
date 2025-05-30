/** Represents the entry point of a dungeon level.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.engine;

import java.io.Serial;
import java.io.Serializable;

public class Entry implements Entity, Serializable {

    @Serial
    private static final long serialVersionUID = 20240530L; // Example serialVersionUID

    @Override
    public char getSymbol() {
        return 'E';
    }

    @Override
    public String interact(Player player) {
        // This message will be added to turnMessages by GameState.movePlayer()
        return "You are at the dungeon entry.";
    }

    /**
     * The entry point is passable for the player to be on it.
     * @return true, as the entry point is passable.
     */
    @Override
    public boolean isPassable() {
        return true;
    }
}