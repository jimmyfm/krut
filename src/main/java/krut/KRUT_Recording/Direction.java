package krut.KRUT_Recording;

/**
 * A class that can hold a direction in a two-dimensional coordinate
 * system. The x and y components of the direction are stored as
 * double values. The class also has a method for normalizing a direction
 * represented by two integer components.
 * <p>
 * This class is used to move the capRect in the capRectMover() and
 * getDirectionEdgeIntersection() methods.
 */
public class Direction {
    /**
     * The x component of the direction.
     */
    public final double x;
    /**
     * The y component of the direction.
     */
    public final double y;

    /**
     * A constructor creating a Direction object with the
     * given x and y components.
     *
     * @param x The x-component of the Direction.
     * @param y The y-component of the Direction.
     */
    Direction(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * A method to take two integer components, and return a normalized
     * Direction.
     *
     * @param x The x-component of the direction.
     * @param y The y-component of the direction.
     * @return A new Direction object holding a normalized representation
     * of the given direction. The direction (1, 0) is returned
     * if the arguments to this method were both 0.
     */
    public Direction normalize(final int x, final int y) {
        final double length = Math.sqrt(x * x + y * y);
		if (0 < length)
			return new Direction(x / length, y / length);
		else
			return new Direction(1, 0);
    }
}