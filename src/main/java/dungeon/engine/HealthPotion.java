package dungeon.engine;

public class HealthPotion implements Entity {
    @Override
    public char getSymbol() { return 'H'; }

    @Override
    public String interact(Player player) {
        player.adjustHp(4);
        return "You drank a health potion! +4 HP.";
    }

    @Override
    public boolean isPassable() { return true; }
}