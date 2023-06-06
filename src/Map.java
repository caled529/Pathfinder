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

public class Map extends JPanel implements ActionListener {
    public static final String DEFAULT_MAP = """
                                             ##########
                                             ###...#C##
                                             #...#....#
                                             ##.####.##
                                             #..###...#
                                             ##..G##.##
                                             ##########""";

    Timer clock;
    Stack<Tile> pathStack;
    Tile[][] map;
    Character playerCharacter;
    Driver parent;

    final String FILE_PATH;

    public Map(Driver parent, String filePath) {
        this.parent = parent;
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

        map = new Tile[longestLine][charGrid.size()];

        char currentChar;
        char[] currentRow;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                currentRow = charGrid.get(j);
                if (i < currentRow.length)
                    currentChar = currentRow[i];
                else
                    currentChar = '#';

                if (currentChar == '.')
                    map[i][j] = new Path(i, j);
                else if (currentChar == 'G')
                    map[i][j] = new Goal(i, j);
                else if (currentChar == 'C') {
                    map[i][j] = new Path(i, j);
                    if (pathStack.empty() == false)
                        pathStack.removeAllElements();
                    pathStack.push(map[i][j]);
                    playerCharacter = new Character(i, j);
                } else
                    map[i][j] = new Obstacle(i, j);
            }
        }

        if (playerCharacter == null) {
            outer:
            for (Tile[] row : map) {
                for (Tile tile : row) {
                    if (tile instanceof Path path) {
                        playerCharacter = new Character(path.getX_POS(), path.getY_POS());
                        pathStack.push(map[path.getX_POS()][path.getY_POS()]);

                        break outer;
                    }
                }
            }
        }

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] instanceof Path currentPath) {
                    /* Thought to use a cool ternary operator statement here to
                       ensure k would be in the map bounds, but IntelliJ
                       recommended this simple function call instead. */
                    for (int k = Math.max(i - 1, 0); k < Math.min(i + 2, map.length); k++) {
                        if (k != i && (map[k][j] instanceof Path || map[k][j] instanceof Goal))
                            currentPath.setNeighbourDirection(2 - (k - i));
                    }
                    for (int k = Math.max(j - 1, 0); k < Math.min(j + 2, map[0].length); k++) {
                        if (k != j && (map[i][k] instanceof Path || map[i][k] instanceof Goal))
                            currentPath.setNeighbourDirection(1 + k - j);
                    }
                }
            }
        }
        if (hasPaths())
            setPreferredSize(new Dimension(map.length * Tile.TILE_SIZE, map[0].length * Tile.TILE_SIZE));
    }

    public boolean hasPaths() {
        for (Tile[] row : map) {
            for (Tile tile : row) {
                if (tile instanceof Path)
                    return true;
            }
        }

        return false;
    }

    public String getFILE_PATH() {
        return FILE_PATH;
    }

    public boolean goalFound() {
        return map[playerCharacter.getXPosition()][playerCharacter.getYPosition()] instanceof Goal;
    }

    private void pathComplete() {
        clock.stop();
        parent.stop.setEnabled(false);
        showMessageDialog(null, "You made it!");
    }

    public String toString() {
        StringBuilder mapString = new StringBuilder();

        int characterXPos = playerCharacter.getINITIAL_X();
        int characterYPos = playerCharacter.getINITIAL_Y();

        for (int i = 0; i < map[0].length; i++) {
            for (int j = 0; j < map.length; j++) {
                if (i == characterYPos && j == characterXPos)
                    mapString.append('C');
                else if (map[j][i] instanceof Path)
                    mapString.append('.');
                else if (map[j][i] instanceof Goal)
                    mapString.append('G');
                else if (map[j][i] instanceof Obstacle)
                    mapString.append('#');
            }
            mapString.append("\n");       }

        return mapString.toString();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Path currentPath = (Path) pathStack.peek();
        currentPath.setVisited();

        Path currentNeighbour;

        int xPos = currentPath.getX_POS();
        int yPos = currentPath.getY_POS();

        for (int i = Math.max(xPos - 1, 0); i < Math.min(xPos + 2, map.length); i++) {
            if (i != xPos && map[i][yPos] instanceof Goal) {
                pathStack.push(map[i][yPos]);
                playerCharacter.move(((Goal) pathStack.peek()).getX_POS(), ((Goal) pathStack.peek()).getY_POS());
                currentPath.setState(playerCharacter.getDirection().getValue());

                repaint();
                pathComplete();

                return;
            }
        }
        for (int i = Math.max(yPos - 1, 0); i < Math.min(yPos + 2, map[0].length); i++) {
            if (i != yPos && map[xPos][i] instanceof Goal) {
                pathStack.push(map[xPos][i]);
                playerCharacter.move(((Goal) pathStack.peek()).getX_POS(), ((Goal) pathStack.peek()).getY_POS());
                currentPath.setState(playerCharacter.getDirection().getValue());

                repaint();
                pathComplete();

                return;
            }
        }

        for (Directions d : Directions.values()) {
            if (currentPath.hasNeighbour(d.getValue() - 1)) {
                if (d == Directions.NORTH || d == Directions.SOUTH) {
                    currentNeighbour = (Path) map[xPos][yPos + (d.getValue() - 2)];
                    if (currentNeighbour.isVisited() == false) {
                        pathStack.push(currentNeighbour);
                        playerCharacter.move(currentNeighbour.getX_POS(), currentNeighbour.getY_POS());
                        currentPath.setState(playerCharacter.getDirection().getValue());

                        repaint();
                        return;
                    }
                }
                if (d == Directions.EAST || d == Directions.WEST) {
                    currentNeighbour = (Path) map[xPos - (d.getValue() - 3)][yPos];
                    if (currentNeighbour.isVisited() == false) {
                        pathStack.push(currentNeighbour);
                        playerCharacter.move(currentNeighbour.getX_POS(), currentNeighbour.getY_POS());
                        currentPath.setState(playerCharacter.getDirection().getValue());

                        repaint();
                        return;
                    }
                }
            }
        }

        ((Path) pathStack.pop()).setCompleted();
        if (pathStack.empty()) {
            showMessageDialog(null, "Game over");
            reset();
        } else {
            currentPath = (Path) pathStack.peek();
            playerCharacter.move(currentPath.getX_POS(), currentPath.getY_POS());
        }

        repaint();
    }

    public void start() {
        clock.start();
    }

    public void stop() {
        clock.stop();
    }

    public void reset() {
        for (Tile[] row : map) {
            for (Tile tile : row) {
                if (tile instanceof Path path)
                    path.reset();
            }
        }

        playerCharacter.reset();
        pathStack.clear();
        pathStack.push(map[playerCharacter.getXPosition()][playerCharacter.getYPosition()]);

        repaint();
    }

    @Override
    protected void paintComponent(Graphics page) {
        super.paintComponent(page);

        for (Tile[] row : map) {
            for (Tile tile : row) {
                tile.draw(this, page);
            }
        }

        playerCharacter.draw(this, page);
    }
}