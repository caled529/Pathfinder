import javax.swing.*;
import java.awt.*;

import static java.lang.invoke.MethodHandles.lookup;

public class Goal extends Tile {
    static final ImageIcon SPRITE = getSprite(lookup().lookupClass().getSimpleName());
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
    public void draw(JPanel panel, Graphics page) {
        Goal.SPRITE.paintIcon(panel, page, X_POS * TILE_SIZE, Y_POS * TILE_SIZE);
    }
}
