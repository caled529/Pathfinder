import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Facilitates modification and creation of maps by the user.
 */
public class MapEditor extends JPanel implements MouseListener {
    private ArrayList<ArrayList<Tile>> mapGrid;
    private String selectedTileType, savedFilePath;
    private boolean mapSaved;

    private final int TOP_OFFSET;

    /**
     * Constructor for the <code>MapEditor</code> class. Initializes the map to
     * be edited by reading it in from a text file.
     *
     * @param filePath the path of the text file this map should be initialized
     *                 from.
     */
    public MapEditor(String filePath) {
        selectedTileType = "path";

        TOP_OFFSET = 25;

        Scanner mapReader = null;
        boolean fileReadError = false;
        try {
            mapReader = new Scanner(new File(filePath));
        } catch (NullPointerException | FileNotFoundException e) {
            fileReadError = true;
        }

        mapGrid = new ArrayList<>();
        if (fileReadError) {
            mapGrid.add(new ArrayList<>());
            mapGrid.get(0).add(new Path(0, 0));
        } else {
            ArrayList<char[]> charGrid = new ArrayList<>();
            while (mapReader.hasNextLine()) {
                charGrid.add(mapReader.nextLine().trim().toCharArray());
            }

            int longestLine = 0;
            for (char[] line : charGrid) {
                if (line.length > longestLine)
                    longestLine = line.length;
            }

            char currentChar;
            char[] currentRow;
            for (int i = 0, n = Math.min(longestLine, 256); i < n; i++) {
                mapGrid.add(new ArrayList<>());

                for (int j = 0, o = Math.min(charGrid.size(), 256); j < o; j++) {
                    currentRow = charGrid.get(j);
                    if (i < currentRow.length)
                        currentChar = currentRow[i];
                    else
                        currentChar = '#';

                    if (currentChar == '.')
                        mapGrid.get(i).add(new Path(i, j));
                    else if (currentChar == 'G')
                        mapGrid.get(i).add(new Goal(i, j));
                    else if (currentChar == 'C') {
                        mapGrid.get(i).add(new CharacterStart(i, j));
                    } else
                        mapGrid.get(i).add(new Obstacle(i, j));
                }
            }
        }

        addMouseListener(this);
        this.setPreferredSize(new Dimension(mapGrid.size() * Tile.TILE_SIZE,
                mapGrid.get(0).size() * Tile.TILE_SIZE + TOP_OFFSET));
    }

    /**
     * Adds a column to <code>mapGrid</code> and populates it with
     * <code>Path</code> objects.
     */
    public void increaseHorizontal() {
        mapGrid.add(new ArrayList<>());

        for (int i = 0, n = mapGrid.get(0).size(), x = mapGrid.size() - 1; i < n; i++) {
            mapGrid.get(x).add(new Path(x, i));
        }

        this.setPreferredSize(new Dimension(mapGrid.size() * Tile.TILE_SIZE,
                mapGrid.get(0).size() * Tile.TILE_SIZE + TOP_OFFSET));
        this.revalidate();

        repaint();
    }

    /**
     * Removes a column from <code>mapGrid</code>.
     */
    public void decreaseHorizontal() {
        mapGrid.remove(mapGrid.size() - 1);

        this.setPreferredSize(new Dimension(mapGrid.size() * Tile.TILE_SIZE,
                mapGrid.get(0).size() * Tile.TILE_SIZE + TOP_OFFSET));
        this.revalidate();

        repaint();
    }

    public int getMapWidth() {
        return mapGrid.size();
    }

    /**
     * Adds a row to <code>mapGrid</code> and populates it with
     * <code>Path</code> objects.
     */
    public void increaseVertical() {
        for (int i = 0, n = mapGrid.size(), y = mapGrid.get(0).size(); i < n; i++) {
            mapGrid.get(i).add(new Path(i, y));
        }

        this.setPreferredSize(new Dimension(mapGrid.size() * Tile.TILE_SIZE,
                mapGrid.get(0).size() * Tile.TILE_SIZE + TOP_OFFSET));
        this.revalidate();

        repaint();
    }

    /**
     * Removes a row from <code>mapGrid</code>.
     */
    public void decreaseVertical() {
        for (int i = 0, n = mapGrid.size(), y = mapGrid.get(0).size() - 1; i < n; i++) {
            mapGrid.get(i).remove(y);
        }

        this.setPreferredSize(new Dimension(mapGrid.size() * Tile.TILE_SIZE,
                mapGrid.get(0).size() * Tile.TILE_SIZE + TOP_OFFSET));
        this.revalidate();

        repaint();
    }

    public int getMapHeight() {
        return mapGrid.get(0).size();
    }

    /**
     * Sets the class to be used when drawing <code>Tile</code> objects onto the
     * map using the mouse.
     *
     * @param type the name of the class to be used.
     */
    public void setSelectedTile(String type) {
        selectedTileType = type;

        repaint();
    }

    public void setFilePath(String filePath) {
        savedFilePath = filePath;
    }

    public String getSavedFilePath() {
        return savedFilePath;
    }

    public boolean isSaved() {
        return mapSaved;
    }

    /**
     * Iterates through each <code>Tile</code> in <code>map</code> and builds a
     * string of characters representing each object.
     *
     * @return the map as a String.
     */
    public String toString() {
        StringBuilder mapString = new StringBuilder();

        for (int i = 0, n = mapGrid.get(0).size(); i < n; i++) {
            for (int j = 0, length = mapGrid.size(); j < length; j++) {
                if (mapGrid.get(j).get(i) instanceof CharacterStart)
                    mapString.append('C');
                else if (mapGrid.get(j).get(i) instanceof Path)
                    mapString.append('.');
                else if (mapGrid.get(j).get(i) instanceof Goal)
                    mapString.append('G');
                else if (mapGrid.get(j).get(i) instanceof Obstacle)
                    mapString.append('#');
            }
            mapString.append("\n");
        }

        mapSaved = true;

        return mapString.toString();
    }

    @Override
    protected void paintComponent(Graphics page) {
        super.paintComponent(page);

        page.drawString("Selected: " + selectedTileType, 0, 15);

        for (ArrayList<Tile> column : mapGrid) {
            for (Tile tile : column) {
                tile.draw(this, page, 0, TOP_OFFSET);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}

    /**
     * Changes the <code>Tile</code> object under the mouse to one of the type
     * specified by <code>selectedTileType</code>.
     *
     * @param me the event to be processed.
     */
    @Override
    public void mouseReleased(MouseEvent me) {
        int mapWidth = mapGrid.size() * Tile.TILE_SIZE;
        int mapHeight = mapGrid.get(0).size() * Tile.TILE_SIZE;

        if (me.getX() < mapWidth && me.getY() > TOP_OFFSET && me.getY() < mapHeight + TOP_OFFSET) {
            int gridX = me.getX() / Tile.TILE_SIZE;
            int gridY = (me.getY() - TOP_OFFSET) / Tile.TILE_SIZE;

            switch (selectedTileType) {
                case "path" -> mapGrid.get(gridX).set(gridY, new Path(gridX, gridY));
                case "goal" -> mapGrid.get(gridX).set(gridY, new Goal(gridX, gridY));
                case "characterStart" -> mapGrid.get(gridX).set(gridY, new CharacterStart(gridX, gridY));
                case "obstacle" -> mapGrid.get(gridX).set(gridY, new Obstacle(gridX, gridY));
            }
        }

        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}