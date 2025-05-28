// File: Entry.java
package dungeon.engine;

public class Entry implements Entity {
    @Override
    public char getSymbol() { return 'E'; }

    @Override
    public String interact(Player player) { return "You are at the dungeon entry."; }

    @Override
    public boolean isPassable() { return true; }
}
