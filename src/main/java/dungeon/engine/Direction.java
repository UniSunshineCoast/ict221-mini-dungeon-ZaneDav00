/** Player Direction Controls
 * Author: Zane Davis
 * Student ID: 1174117
 * Due Date: 30th May 2025
 */
package dungeon.engine;

public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Converts delta coordinates (dx, dy) to a Direction enum constant.
     *
     * @param dx The change in x-coordinate.
     * @param dy The change in y-coordinate.
     * @return The corresponding Direction.
     * @throws IllegalArgumentException if the delta values do not match a predefined Direction.
     */
    public static Direction fromDelta(int dx, int dy) {
        for (Direction dir : values()) {
            if (dir.dx == dx && dir.dy == dy) {
                return dir;
            }
        }
        throw new IllegalArgumentException("Invalid direction delta: (" + dx + ", " + dy + ")");
    }
}