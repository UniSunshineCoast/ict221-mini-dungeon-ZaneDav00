package dungeon.engine;

import java.util.Scanner;
import java.util.Random;

public class GameEngine {

    private GameState state;
    private static final int MAP_SIZE = 10;
    private static final int MAX_STEPS = 100;

    // Add this method to get the map size
    public int getSize() {
        return state.getMap().length;
    }

    public Cell[][] getGuiMap() {
        Entity[][] logicalMap = state.getMap();
        Cell[][] guiMap = new Cell[getSize()][getSize()];
        Player player = state.getPlayer();

        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                Cell cell = new Cell();

                if (i == player.getX() && j == player.getY()) {
                    cell.setPlayerSymbol();
                } else {
                    cell.setEntity(logicalMap[i][j]);
                }

                guiMap[i][j] = cell;
            }
        }

        return guiMap;
    }


    public boolean movePlayer(int dx, int dy) {
        int newX = state.getPlayerX() + dx;
        int newY = state.getPlayerY() + dy;

        if (newX >= 0 && newX < MAP_SIZE && newY >= 0 && newY < MAP_SIZE) {
            Entity[][] map = state.getMap();
            Entity entity = map[newX][newY];

            state.movePlayer(Direction.fromDelta(dx, dy));

            if (entity != null && !(entity instanceof Ladder)) {
                map[newX][newY] = null;
            }

            System.out.println("Player moved to: (" + newX + ", " + newY + "), entity: " + map[newX][newY]);

            if (entity instanceof Ladder) {
                System.out.println("You climbed the ladder!");
                // You could set a game-over flag here or in state
            }

            return true;
        }
        return false;
    }

    public boolean hasReachedLadder() {
        return state.hasReachedLadder();
    }


    public GameEngine(int difficulty) {
        this.state = new GameState(MAP_SIZE, difficulty);
        generateLevel();
    }

    public Player getPlayer() {
        return state.getPlayer();
    }


    private void generateLevel() {
        Entity[][] map = state.getMap();
        Player player = new Player(0, 0);
        state.setPlayer(player);
        state.setSteps(0);

        // Clear map
        for (int i = 0; i < MAP_SIZE; i++) {
            for (int j = 0; j < MAP_SIZE; j++) {
                map[i][j] = null;
            }
        }

        // Place Entry
        map[MAP_SIZE - 1][0] = new Entry();
        state.setPlayerPosition(MAP_SIZE - 1, 0);

        // Place Player
        player.setPosition(MAP_SIZE - 1, 0);

        // Place random entities
        placeRandomItems(map, new Gold(), 5);
        placeRandomItems(map, new Trap(), 5);
        placeRandomItems(map, new MeleeMutant(), 3);
        placeRandomItems(map, new RangedMutant(), 3);
        placeRandomItems(map, new HealthPotion(), 2);
        placeRandomItems(map, new Ladder(), 1);
    }

    private void placeRandomItems(Entity[][] map, Entity entity, int count) {
        Random rand = new Random();
        int placed = 0;
        while (placed < count) {
            int x = rand.nextInt(MAP_SIZE);
            int y = rand.nextInt(MAP_SIZE);
            if (map[x][y] == null) {
                map[x][y] = entity;
                placed++;
            }
        }
    }




    public int getSteps() {
        return state.getSteps();
    }

    public boolean isGameOver() {
        return !state.getPlayer().isAlive();
    }

    public void playTextGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to MiniDungeon!");

        while (!state.isGameOver()) {
            state.printMap();
            System.out.printf("HP: %d | Score: %d | Steps: %d/%d\n", state.getPlayer().getHp(), state.getPlayer().getScore(), state.getSteps(), MAX_STEPS);
            System.out.print("Move (u/d/l/r): ");
            String move = scanner.nextLine().trim().toLowerCase();
            Direction dir = switch (move) {
                case "u" -> Direction.UP;
                case "d" -> Direction.DOWN;
                case "l" -> Direction.LEFT;
                case "r" -> Direction.RIGHT;
                default -> null;
            };

            if (dir != null) {
                state.movePlayer(dir);
            } else {
                System.out.println("Invalid input. Use u/d/l/r.");
            }

            if (state.getSteps() >= MAX_STEPS || !state.getPlayer().isAlive()) {
                System.out.println("Game Over. You lost.");
                state.getPlayer().adjustScore(-1);
                break;
            }

            if (state.hasReachedLadder()) {
                if (state.getLevel() == 2) {
                    System.out.println("You escaped the dungeon!");
                    break;
                } else {
                    System.out.println("Advancing to Level 2!");
                    state.advanceLevel();
                    generateLevel();
                }
            }
        }

        System.out.println("Final Score: " + state.getPlayer().getScore());
    }

    public static void main(String[] args) {
        GameEngine engine = new GameEngine(3);
        engine.playTextGame();
    }


}
