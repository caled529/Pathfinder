import javax.swing.*;
import java.awt.*;

/**
 * <code>Character</code> is a marker that represents the position of the
 * uppermost <code>Tile</code> object within a <code>Map</code>'s
 * <code>pathStack</code> in order to allow users to visualize the solving
 * process.
 */
public class Character {
    private static final ImageIcon[] DIRECTIONAL_SPRITES = {new ImageIcon("assets/characterN.png"),
                                                            new ImageIcon("assets/characterE.png"),
                                                            new ImageIcon("assets/characterS.png"),
                                                            new ImageIcon("assets/characterW.png")};

    private final int INITIAL_X, INITIAL_Y;
    private int xPosition, yPosition;
    private Directions direction;

    /**
     * Constructor for the <code>Character</code> class.
     * @param xPosition where on the x-axis the <code>Character</code> should be
     *                  placed.
     * @param yPosition where on the y-axis the <code>Character</code> should be
     *                  placed.
     */
    public Character(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;

        INITIAL_X = xPosition;
        INITIAL_Y = yPosition;

        direction = Directions.NORTH;
    }

    /**
     * Calculates the direction the <code>Character</code> object would be
     * facing to move to the given position, then moves it to that position.
     *
     * @param newXPosition where to move on the x-axis.
     * @param newYPosition where to move on the y-axis.
     */
    public void move(int newXPosition, int newYPosition) {
        int xDirection = newXPosition - xPosition;
        int yDirection = newYPosition - yPosition;

        if (yDirection < 0)
            direction = Directions.NORTH;
        else if (xDirection > 0)
            direction = Directions.EAST;
        else if (yDirection > 0)
            direction = Directions.SOUTH;
        else if (xDirection < 0)
            direction = Directions.WEST;

        xPosition = newXPosition;
        yPosition = newYPosition;
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public Directions getDirection() {
        return direction;
    }

    /**
     * Moves the <code>Character</code> object back to its starting position.
     */
    public void reset() {
        xPosition = INITIAL_X;
        yPosition = INITIAL_Y;

        direction = Directions.NORTH;
    }

    public void draw(JPanel panel, Graphics page) {
        DIRECTIONAL_SPRITES[direction.getValue() - 1].paintIcon(panel, page,
                xPosition * Tile.TILE_SIZE, yPosition * Tile.TILE_SIZE);
    }
}
