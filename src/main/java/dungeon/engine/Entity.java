/** Defines an entity (Gold, Mutant, Trap, etc...)
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.engine;

public interface Entity {

    /**
     * Gets the character symbol representing the entity on the map.
     *
     * @return The character symbol of the entity.
     */
    char getSymbol();

    /**
     * Defines the interaction logic when a player encounters this entity.
     * This method can modify the player's state (e.g., HP, score) and
     * should return a message describing the interaction.
     *
     * @param player The player interacting with this entity.
     * @return A string message describing the outcome of the interaction.
     * Can be null or empty if there's no specific message.
     */
    String interact(Player player);


    boolean isPassable();
}