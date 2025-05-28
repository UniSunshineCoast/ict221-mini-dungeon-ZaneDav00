// File: Ladder.java
package dungeon.engine;

public class Ladder implements Entity {
    @Override
    public char getSymbol() { return 'L'; }

    @Override
    public String interact(Player player) {
        return "You climbed the ladder!";
    }

    @Override
    public boolean isPassable() { return true; }
}
