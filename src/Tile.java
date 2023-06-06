import javax.swing.*;
import java.awt.*;

public abstract class Tile {
    public static final int TILE_SIZE = 40;

    protected static ImageIcon getSprite(String className) {
        return new ImageIcon("assets/" + className.toLowerCase() + ".png");
    }

    public abstract void draw(JPanel panel, Graphics page);
}
