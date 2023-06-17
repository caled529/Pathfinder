import javax.swing.*;
import java.awt.*;

/**
 * Marks where the character should start on a map. Only used by the map editor
 * as it is saved as the character which is used to place the
 * <code>Character</code> object on initialization of maps.
 */
public class CharacterStart extends Tile {
    static final ImageIcon SPRITE = new ImageIcon("assets/characterStart.png");

    private final int X_POS, Y_POS;

    public CharacterStart(int xPos, int yPos) {
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

