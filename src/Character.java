import javax.swing.*;
import java.awt.*;

public class Character {
    private static final ImageIcon[] DIRECTIONAL_SPRITES = {new ImageIcon("assets/characterN.png"),
                                                            new ImageIcon("assets/characterE.png"),
                                                            new ImageIcon("assets/characterS.png"),
                                                            new ImageIcon("assets/characterW.png")};

    private final int INITIAL_X, INITIAL_Y;
    private int xPosition, yPosition;
    private Directions direction;
    public Character(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;

        INITIAL_X = xPosition;
        INITIAL_Y = yPosition;

        direction = Directions.NORTH;
    }

    public void move(int newXPosition, int newYPosition) {
        int xDirection = newXPosition - xPosition;
        int yDirection = newYPosition - yPosition;

        if (yDirection == -1)
            direction = Directions.NORTH;
        else if (xDirection == 1)
            direction = Directions.EAST;
        else if (yDirection == 1)
            direction = Directions.SOUTH;
        else if (xDirection == -1)
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

    public int getINITIAL_X() {
        return INITIAL_X;
    }

    public int getINITIAL_Y() {
        return INITIAL_Y;
    }

    public Directions getDirection() {
        return direction;
    }

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
