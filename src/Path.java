import javax.swing.*;
import java.awt.*;

public class Path extends Tile {
    static final ImageIcon SPRITE = new ImageIcon("assets/path.png");
    static final ImageIcon[] STEP_DIRECTIONS = {new ImageIcon("assets/pathN.png"),
                                                new ImageIcon("assets/pathE.png"),
                                                new ImageIcon("assets/pathS.png"),
                                                new ImageIcon("assets/pathW.png")};
    static final ImageIcon COMPLETED_PATH = new ImageIcon("assets/pathFinished.png");
    private final int X_POS, Y_POS;
    private int state;
    private boolean visited, completed;
    private final boolean[] neighbourDirections;

    public Path(int xPos, int yPos) {
        X_POS = xPos;
        Y_POS = yPos;
        state = 0;
        visited = false;
        completed = false;
        neighbourDirections = new boolean[Directions.values().length];
    }

    public int getX_POS() {
        return X_POS;
    }

    public int getY_POS() {
        return Y_POS;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited() {
        visited = true;
    }

    public void setCompleted() {
        completed = true;
    }

    public void reset() {
        state = 0;
        visited = false;
        completed = false;
    }

    public boolean hasNeighbour(int direction) {
        if (direction >= 0 && direction < neighbourDirections.length)
            return neighbourDirections[direction];
        else
            return false;
    }

    public void setNeighbourDirection(int direction) {
        if (direction >= 0 && direction < neighbourDirections.length)
            neighbourDirections[direction] = true;
    }

    @Override
    public void draw(JPanel panel, Graphics page, int xOffset, int yOffset) {
        ImageIcon image = SPRITE;

        if (completed)
            image = COMPLETED_PATH;
        else if (state > 0 && state < 5)
            image = STEP_DIRECTIONS[state - 1];

        image.paintIcon(panel, page, X_POS * TILE_SIZE + xOffset, Y_POS * TILE_SIZE + yOffset);
    }
}
