import javax.swing.*;
import java.awt.*;

public class Goal extends Tile {
    static final ImageIcon SPRITE = new ImageIcon("assets/goal.png");
    private final int X_POS, Y_POS;
    public Goal(int xPos, int yPos) {
        this.X_POS = xPos;
        this.Y_POS = yPos;
    }

    public int getX_POS() {
        return X_POS;
    }

    public int getY_POS() {
        return Y_POS;
    }

    @Override
    public void draw(JPanel panel, Graphics page, int xOffset, int yOffset) {
        Goal.SPRITE.paintIcon(panel, page, X_POS * TILE_SIZE + xOffset, Y_POS * TILE_SIZE + yOffset);
    }
}
