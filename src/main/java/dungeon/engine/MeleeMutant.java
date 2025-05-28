package dungeon.engine;

public class MeleeMutant implements Entity {
    @Override
    public char getSymbol() { return 'M'; }

    @Override
    public String interact(Player player) {
        player.adjustHp(-2);
        player.adjustScore(2);
        return "You fought a melee mutant! -2 HP, +2 score.";
    }

    @Override
    public boolean isPassable() { return true; }
}