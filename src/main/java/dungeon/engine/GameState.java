// Updated GameState class using Option 2 (Player not stored in Entity map)
package dungeon.engine;

public class GameState {
    private Entity[][] map;
    private Player player;
    private int playerX, playerY;
    private int steps;
    private int level;

    public GameState(int size, int difficulty) {
        this.map = new Entity[size][size];
        this.level = 1;
    }

    public Entity[][] getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPlayerPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
        player.setPosition(x, y);
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    public int getLevel() {
        return level;
    }

    public void advanceLevel() {
        level++;
        steps = 0;
    }

    public void movePlayer(Direction dir) {
        int newX = playerX + dir.dx;
        int newY = playerY + dir.dy;

        if (newX >= 0 && newX < map.length && newY >= 0 && newY < map.length) {
            Entity entity = map[newX][newY];

            if (entity == null || entity.isPassable()) {
                String message = (entity != null) ? entity.interact(player) : null;
                if (entity != null) map[newX][newY] = null; // remove entity after interaction
                if (message != null) System.out.println(message);
                setPlayerPosition(newX, newY);
                steps++;
            } else {
                System.out.println("You can't move there.");
            }
        } else {
            System.out.println("Invalid move: out of bounds.");
        }
    }

    public boolean hasReachedLadder() {
        Entity current = map[playerX][playerY];
        return current instanceof Ladder;
    }

    public boolean isGameOver() {
        return !player.isAlive();
    }

    public void printMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                if (i == playerX && j == playerY) {
                    System.out.print("P ");
                } else if (map[i][j] != null) {
                    System.out.print(map[i][j].getSymbol() + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    public void clearCurrentCell() {
        map[playerX][playerY] = null;
    }
}
