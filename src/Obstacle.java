import javax.swing.*;
import java.awt.*;

/**
 * Represents an impassable obstacle within a map.
 */
public class Obstacle extends Tile {
    static final ImageIcon SPRITE = new ImageIcon("assets/obstacle.png");

    private final int X_POS, Y_POS;

    public Obstacle(int xPos, int yPos) {
        X_POS = xPos;
        Y_POS = yPos;
    }

    @Override
    public int getX_POS() {
        return X_POS;
    }

    @Override
    public int getY_POS() {
        return Y_POS;
    }

    public void draw(JPanel panel, Graphics page, int xOffset, int yOffset) {
        SPRITE.paintIcon(panel, page, X_POS * TILE_SIZE + xOffset, Y_POS * TILE_SIZE + yOffset);
    }
}
