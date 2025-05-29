package dungeon.engine;

import java.util.Random;

public class RangedMutant implements Entity {
    private final Random random = new Random();

    @Override
    public char getSymbol() { return 'R'; }

    @Override
    public String interact(Player player) {
        // Player steps on the Ranged Mutant
        player.adjustScore(2);
        // As per assignment: "No HP lost when defeating directly, +2 score."
        return "You attacked a ranged mutant and won. +2 score.";
    }

    @Override
    public boolean isPassable() { return true; } // Player can step on it to defeat it

    /**
     * Checks if the mutant can attack the player from its position (rx, ry)
     * to player's position (px, py).
     * Attacks from 2 tiles away (horizontal or vertical).
     */
    public boolean canAttack(int playerX, int playerY, int mutantX, int mutantY) {
        int deltaX = Math.abs(playerX - mutantX);
        int deltaY = Math.abs(playerY - mutantY);

        // Attack if in the same row and within 2 tiles (but not on the same tile)
        if (playerX == mutantX && deltaY > 0 && deltaY <= 2) {
            return true;
        }
        // Attack if in the same column and within 2 tiles (but not on the same tile)
        if (playerY == mutantY && deltaX > 0 && deltaX <= 2) {
            return true;
        }
        return false;
    }

    /**
     * Ranged mutant attempts an attack.
     * @return true if the attack hits (50% chance), false otherwise.
     */
    public boolean tryAttack() {
        return random.nextDouble() < 0.5; // 50% chance
    }
}