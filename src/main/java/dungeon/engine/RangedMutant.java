package dungeon.engine;

import java.util.Random;

public class RangedMutant implements Entity {
    private final Random random = new Random();

    @Override
    public char getSymbol() { return 'R'; }

    @Override
    public String interact(Player player) {
        player.adjustScore(2);
        return "You attacked a ranged mutant and won. +2 score.";
    }

    @Override
    public boolean isPassable() { return true; }

    public boolean canAttack(int px, int py, int rx, int ry) {
        return (px == rx && Math.abs(py - ry) <= 2) || (py == ry && Math.abs(px - rx) <= 2);
    }

    public boolean tryAttack() {
        return random.nextDouble() < 0.5;
    }
}