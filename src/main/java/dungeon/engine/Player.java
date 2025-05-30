/** Represents the player, tracking position, health, and score.
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.engine;

import java.io.Serial;
import java.io.Serializable;

public class Player implements Serializable {

    @Serial
    private static final long serialVersionUID = 20240530L; // Standardized a bit

    private int x;
    private int y;
    private int hp;
    private int score;

    private final int maxHp = 10;

    public Player(int initialX, int initialY) {
        this.x = initialX;
        this.y = initialY;
        this.hp = this.maxHp; // Initialize with maxHp
        this.score = 0;
    }

    // --- Position ---
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // --- Health Points (HP) ---
    public int getHp() {
        return hp;
    }

    /**
     * Adjusts the player's current HP by the given delta.
     * Ensures HP does not go below 0 or exceed maxHp.
     * @param delta The amount to change HP by (can be positive or negative).
     */
    public void adjustHp(int delta) {
        this.hp += delta;
        if (this.hp < 0) {
            this.hp = 0;
        } else if (this.hp > this.maxHp) {
            this.hp = this.maxHp;
        }
    }

    /**
     * Sets the player's current HP to a specific value.
     * Ensures HP does not go below 0 or exceed maxHp.
     * @param newHp The new HP value.
     */
    public void setHp(int newHp) {
        if (newHp < 0) {
            this.hp = 0;
        } else this.hp = Math.min(newHp, this.maxHp);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    // --- Score ---
    public int getScore() {
        return score;
    }

    public void adjustScore(int delta) {
        this.score += delta;
    }

    public void setScore(int newScore) {
        this.score = newScore;
    }
}