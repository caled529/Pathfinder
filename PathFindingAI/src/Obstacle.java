import javax.swing.*;
import java.awt.*;

import static java.lang.invoke.MethodHandles.lookup;

public class Obstacle extends Tile {
    static final ImageIcon SPRITE = getSprite(lookup().lookupClass().getSimpleName());
    private final int X_POS, Y_POS;
    public Obstacle(int xPos, int yPos) {
        this.X_POS = xPos;
        this.Y_POS = yPos;
    }

    public void draw(JPanel panel, Graphics page) {
        SPRITE.paintIcon(panel, page, X_POS * TILE_SIZE, Y_POS * TILE_SIZE);
    }
}
