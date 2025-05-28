package dungeon.engine;

public class Trap implements Entity {
    @Override
    public char getSymbol() { return 'T'; }

    @Override
    public String interact(Player player) {
        player.adjustHp(-2);
        return "You fell into a trap! -2 HP.";
    }

    @Override
    public boolean isPassable() { return true; }
}