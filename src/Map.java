import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Facilitates the viewing and solving of user-created maps using a stack.
 */
public class Map extends JPanel implements ActionListener {
    public static final String DEFAULT_MAP = """
                                             ##########
                                             ###...#C##
                                             #...#....#
                                             ##.####.##
                                             #..###...#
                                             ##..G##.##
                                             ##########""";

    private Timer clock;
    private Stack<Tile> pathStack;
    private Tile[][] mapGrid;
    private Character playerCharacter;

    private final Driver PARENT;
    private final String FILE_PATH;

    /**
     * Constructor for the <code>Map</code> class. Initializes the map to be
     * traversed by reading it in from a text file.
     *
     * @param parent the <code>Driver</code> object this method was called from.
     * @param filePath the path of the text file this map should be initialized
     *                 from.
     */
    public Map(Driver parent, String filePath) {
        PARENT = parent;
        FILE_PATH = filePath;

        clock = new Timer(250, this);
        pathStack = new Stack<>();

        Scanner mapReader;
        try {
            mapReader = new Scanner(new File(filePath));
        } catch (NullPointerException | FileNotFoundException e) {
            mapReader = new Scanner(DEFAULT_MAP);
        }

        ArrayList<char[]> charGrid = new ArrayList<>();
        while (mapReader.hasNextLine()) {
            charGrid.add(mapReader.nextLine().trim().toCharArray());
        }

        int longestLine = 0;
        for (char[] line : charGrid) {
            if (line.length > longestLine)
                longestLine = line.length;
        }

        mapGrid = new Tile[longestLine][charGrid.size()];

        char currentChar;
        char[] currentRow;
        for (int i = 0; i < mapGrid.length; i++) {
            for (int j = 0; j < mapGrid[i].length; j++) {
                currentRow = charGrid.get(j);
                if (i < currentRow.length)
                    currentChar = currentRow[i];
                else
                    currentChar = '#';

                if (currentChar == '.')
                    mapGrid[i][j] = new Path(i, j);
                else if (currentChar == 'G')
                    mapGrid[i][j] = new Goal(i, j);
                else if (currentChar == 'C') {
                    mapGrid[i][j] = new Path(i, j);
                    if (pathStack.empty() == false)
                        pathStack.removeAllElements();
                    pathStack.push(mapGrid[i][j]);
                    playerCharacter = new Character(i, j);
                } else
                    mapGrid[i][j] = new Obstacle(i, j);
            }
        }

        if (playerCharacter == null) {
            outer:
            for (Tile[] column : mapGrid) {
                for (Tile tile : column) {
                    if (tile instanceof Path path) {
                        playerCharacter = new Character(path.getX_POS(), path.getY_POS());
                        pathStack.push(mapGrid[path.getX_POS()][path.getY_POS()]);

                        break outer;
                    }
                }
            }
        }

        for (int i = 0; i < mapGrid.length; i++) {
            for (int j = 0; j < mapGrid[i].length; j++) {
                if (mapGrid[i][j] instanceof Path currentPath) {
                    for (int k = Math.max(i - 1, 0); k < Math.min(i + 2, mapGrid.length); k++) {
                        if (k != i && (mapGrid[k][j] instanceof Path || mapGrid[k][j] instanceof Goal))
                            currentPath.setNeighbourDirection(2 - (k - i));
                    }
                    for (int k = Math.max(j - 1, 0); k < Math.min(j + 2, mapGrid[0].length); k++) {
                        if (k != j && (mapGrid[i][k] instanceof Path || mapGrid[i][k] instanceof Goal))
                            currentPath.setNeighbourDirection(1 + k - j);
                    }
                }
            }
        }

        if (hasPaths())
            setPreferredSize(new Dimension(mapGrid.length * Tile.TILE_SIZE, mapGrid[0].length * Tile.TILE_SIZE));
        else {
            mapGrid[0][0] = new Path(0, 0);
            playerCharacter = new Character(0, 0);
        }

    }

    /**
     *  @return whether <code>mapGrid</code> contains at least 1 <code>Path</code>
     *          object.
     */
    public boolean hasPaths() {
        for (Tile[] column : mapGrid) {
            for (Tile tile : column) {
                if (tile instanceof Path)
                    return true;
            }
        }

        return false;
    }

    public String getFILE_PATH() {
        return FILE_PATH;
    }

    /**
     * @return whether the tile <code>playerCharacter</code> is standing on is
     * an instance of <code>Goal</code>.
     */
    public boolean goalFound() {
        return mapGrid[playerCharacter.getXPosition()][playerCharacter.getYPosition()] instanceof Goal;
    }

    private void pathComplete() {
        clock.stop();
        PARENT.viewerStop.setEnabled(false);
        showMessageDialog(null, "You made it!");
    }

    /**
     * Handles solving the map while allowing the user to view the process in
     * steps.
     *
     * @param ae the <code>Timer</code> event that triggers this method.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        Path currentPath = (Path) pathStack.peek();
        currentPath.setVisited();

        int xPos = currentPath.getX_POS();
        int yPos = currentPath.getY_POS();

        /* Checks if a goal is adjacent to the current path. Performed before
         * the regular movement between paths because it's reliant on methods
         * found in the Path class and not the Goal class. This has the nice
         * side effect of making the path solving slightly smarter, as adjacent
         * goals won't simply be passed. */
        for (int i = Math.max(xPos - 1, 0); i < Math.min(xPos + 2, mapGrid.length); i++) {
            if (i != xPos && mapGrid[i][yPos] instanceof Goal goal) {
                moveTo(goal);

                pathComplete();

                return;
            }
        }
        for (int i = Math.max(yPos - 1, 0); i < Math.min(yPos + 2, mapGrid[0].length); i++) {
            if (i != yPos && mapGrid[xPos][i] instanceof Goal goal) {
                moveTo(goal);

                pathComplete();

                return;
            }
        }

        // Steps to the next path on the map.
        for (Directions d : Directions.values()) {
            if (currentPath.hasNeighbour(d.getValue() - 1)) {
                if ((d == Directions.NORTH || d == Directions.SOUTH)
                        && mapGrid[xPos][yPos + (d.getValue() - 2)] instanceof Path currentNeighbour) {
                    if (currentNeighbour.isVisited() == false) {
                        moveTo(currentNeighbour);

                        return;
                    }
                }
                if ((d == Directions.EAST || d == Directions.WEST)
                        && mapGrid[xPos - (d.getValue() - 3)][yPos] instanceof Path currentNeighbour) {
                    if (currentNeighbour.isVisited() == false) {
                        moveTo(currentNeighbour);

                        return;
                    }
                }
            }
        }

        /* Marks the current path and backtracks if no unvisited paths are
           adjacent to it. */
        ((Path) pathStack.pop()).setCompleted();

        if (pathStack.empty()) {
            showMessageDialog(null, "No path to goal found.");
            reset();
        } else {
            currentPath = (Path) pathStack.peek();
            playerCharacter.move(currentPath.getX_POS(), currentPath.getY_POS());
        }

        repaint();
    }

    /**
     * Moves <code>playerCharacter</code> to <code>destination</code>, then
     * copies <code>playerCharacter</code>'s direction value onto the
     * <code>Path</code> object at the top of <code>pathStack</code> and pushes
     * <code>destination</code> onto <code>pathStack</code>.
     *
     * @param destination the <code>Tile</code> to move to.
     */
    public void moveTo(Tile destination) {
        playerCharacter.move(destination.getX_POS(), destination.getY_POS());
        ((Path) pathStack.peek()).setState(playerCharacter.getDirection().getValue());
        pathStack.push(destination);

        repaint();
    }

    /**
     * Starts the <code>Timer</code> responsible for animating map traversal.
     */
    public void start() {
        clock.start();
    }

    /**
     * Stops the <code>Timer</code> responsible for animating map traversal.
     */
    public void stop() {
        clock.stop();
    }

    /**
     * Resets all <code>Path</code> tiles in <code>mapGrid</code> to their default
     * state, moves <code>playerCharacter</code> back to the starting position,
     * and pushes the <code>Path</code> tile at the starting position back onto
     * the <code>pathStack</code>.
     */
    public void reset() {
        for (Tile[] column : mapGrid) {
            for (Tile tile : column) {
                if (tile instanceof Path path)
                    path.reset();
            }
        }

        playerCharacter.reset();
        pathStack.clear();
        pathStack.push(mapGrid[playerCharacter.getXPosition()][playerCharacter.getYPosition()]);

        repaint();
    }

    @Override
    protected void paintComponent(Graphics page) {
        super.paintComponent(page);

        for (Tile[] column : mapGrid) {
            for (Tile tile : column) {
                tile.draw(this, page, 0, 0);
            }
        }

        playerCharacter.draw(this, page);
    }
}