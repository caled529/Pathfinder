import javax.swing.*;
import java.awt.*;

public abstract class Tile {
    public static final int TILE_SIZE = 40;

    public abstract void draw(JPanel panel, Graphics page, int xOffset, int yOffset);
}
