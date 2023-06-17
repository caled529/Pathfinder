import javax.swing.*;
import java.awt.*;

/**
 * Abstract class all map objects inherit from to allow them to all reside under
 * one date structure of type <code>Tile</code>.
 */
public abstract class Tile {
    public static final int TILE_SIZE = 40;

    public abstract void draw(JPanel panel, Graphics page, int xOffset, int yOffset);

    public abstract int getX_POS();

    public abstract int getY_POS();
}
