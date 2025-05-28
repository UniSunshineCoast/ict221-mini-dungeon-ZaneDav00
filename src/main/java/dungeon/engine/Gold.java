package dungeon.engine;

public class Gold implements Entity {
    @Override
    public char getSymbol() { return 'G'; }

    @Override
    public String interact(Player player) {
        player.adjustScore(2);
        return "You picked up gold! +2 score.";
    }

    @Override
    public boolean isPassable() { return true; }
}