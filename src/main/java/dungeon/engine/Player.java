package dungeon.engine;

public class Player {
    private int x, y, hp, score, steps;
    private final int maxHp = 10;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.hp = maxHp;
        this.score = 0;
        this.steps = 0;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public int getHp() { return hp; }
    public void adjustHp(int delta) { this.hp = Math.min(maxHp, hp + delta); }
    public int getScore() { return score; }
    public void adjustScore(int delta) { this.score += delta; }
    public int getSteps() { return steps; }
    public void incrementSteps() { this.steps++; }
    public boolean isAlive() { return hp > 0; }
}