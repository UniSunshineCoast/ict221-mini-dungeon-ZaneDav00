// File: Entity.java
package dungeon.engine;

public interface Entity {
    char getSymbol();
    String interact(Player player);
    boolean isPassable();
}
