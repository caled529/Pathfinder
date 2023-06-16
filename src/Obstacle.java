import javax.swing.*;
import java.awt.*;

public class Obstacle extends Tile {
    static final ImageIcon SPRITE = new ImageIcon("assets/obstacle.png");
    private final int X_POS, Y_POS;
    public Obstacle(int xPos, int yPos) {
        this.X_POS = xPos;
        this.Y_POS = yPos;
    }

    public void draw(JPanel panel, Graphics page, int xOffset, int yOffset) {
        SPRITE.paintIcon(panel, page, X_POS * TILE_SIZE + xOffset, Y_POS * TILE_SIZE + yOffset);
    }
}
