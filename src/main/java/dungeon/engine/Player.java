package dungeon.engine;

public class Player {
    private int x, y, hp, score;
    private final int maxHp = 10;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = maxHp;
        this.score = 0;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public int getHp() { return hp; }

    public void adjustHp(int delta) {
        this.hp += delta;
        if (this.hp < 0) {
            this.hp = 0;
        }
        if (this.hp > maxHp) {
            this.hp = maxHp;
        }
    }

    // Add this method 👇
    public void setHp(int newHp) {
        if (newHp < 0) {
            this.hp = 0;
        } else if (newHp > maxHp) {
            this.hp = maxHp;
        } else {
            this.hp = newHp;
        }
    }

    public int getScore() { return score; }
    public void adjustScore(int delta) { this.score += delta; }
    public void setScore(int newScore) { this.score = newScore; }

    public boolean isAlive() { return hp > 0; }
}