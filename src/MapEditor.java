import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MapEditor extends JPanel implements MouseListener {
    ArrayList<ArrayList<Tile>> map;
    String selectedTileType;
    Driver parent;
    int topOffset;

    public MapEditor(Driver parent, String filePath) {
        this.parent = parent;
        topOffset = 25;

        selectedTileType = "path";

        Scanner mapReader = null;
        boolean fileReadError = false;
        try {
            mapReader = new Scanner(new File(filePath));
        } catch (NullPointerException | FileNotFoundException e) {
            fileReadError = true;
        }

        map = new ArrayList<>();
        if (fileReadError) {
            map.add(new ArrayList<>());
            map.get(0).add(new Path(0, 0));
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
            for (int i = 0; i < longestLine; i++) {
                map.add(new ArrayList<>());

                for (int j = 0, n = charGrid.size(); j < n; j++) {
                    currentRow = charGrid.get(j);
                    if (i < currentRow.length)
                        currentChar = currentRow[i];
                    else
                        currentChar = '#';

                    if (currentChar == '.')
                        map.get(i).add(new Path(i, j));
                    else if (currentChar == 'G')
                        map.get(i).add(new Goal(i, j));
                    else if (currentChar == 'C') {
                        map.get(i).add(new CharacterStart(i, j));
                    } else
                        map.get(i).add(new Obstacle(i, j));
                }
            }
        }

        addMouseListener(this);
        this.setPreferredSize(new Dimension(map.size() * Tile.TILE_SIZE,
                map.get(0).size() * Tile.TILE_SIZE + topOffset));
    }

    public void setSelectedTile(String type) {
        selectedTileType = type;

        repaint();
    }

    public void expandHorizontal() {
        map.add(new ArrayList<>());

        for (int i = 0, n = map.get(0).size(), x = map.size() - 1; i < n; i++) {
            map.get(x).add(new Path(x, i));
        }

        if (map.size() >= 255)
            parent.editorIncreaseHorizontal.setEnabled(false);

        parent.editorDecreaseHorizontal.setEnabled(true);

        this.setPreferredSize(new Dimension(map.size() * Tile.TILE_SIZE,
                                    map.get(0).size() * Tile.TILE_SIZE + topOffset));
        this.revalidate();

        repaint();
    }

    public void decreaseHorizontal() {
        map.remove(map.size() - 1);

        if (map.size() == 1) {
            parent.editorDecreaseHorizontal.setEnabled(false);
        }

        this.setPreferredSize(new Dimension(map.size() * Tile.TILE_SIZE,
                                    map.get(0).size() * Tile.TILE_SIZE + topOffset));
        this.revalidate();

        repaint();
    }

    public void expandVertical() {
        for (int i = 0, n = map.size(), y = map.get(0).size(); i < n; i++) {
            map.get(i).add(new Path(i, y));
        }

        if (map.get(0).size() >= 255)
            parent.editorIncreaseVertical.setEnabled(false);

        parent.editorDecreaseVertical.setEnabled(true);

        this.setPreferredSize(new Dimension(map.size() * Tile.TILE_SIZE,
                                    map.get(0).size() * Tile.TILE_SIZE + topOffset));
        this.revalidate();

        repaint();
    }

    public void decreaseVertical() {
        for (int i = 0, n = map.size(), y = map.get(0).size() - 1; i < n; i++) {
            map.get(i).remove(y);
        }

        if (map.get(0).size() == 1) {
            parent.editorDecreaseVertical.setEnabled(false);
        }

        this.setPreferredSize(new Dimension(map.size() * Tile.TILE_SIZE,
                                    map.get(0).size() * Tile.TILE_SIZE + topOffset));
        this.revalidate();

        repaint();
    }

    public String toString() {
        StringBuilder mapString = new StringBuilder();

        for (int i = 0, n = map.get(0).size(); i < n; i++) {
            for (int j = 0, length = map.size(); j < length; j++) {
                if (map.get(j).get(i) instanceof CharacterStart)
                    mapString.append('C');
                else if (map.get(j).get(i) instanceof Path)
                    mapString.append('.');
                else if (map.get(j).get(i) instanceof Goal)
                    mapString.append('G');
                else if (map.get(j).get(i) instanceof Obstacle)
                    mapString.append('#');
            }
            mapString.append("\n");
        }

        return mapString.toString();
    }

    @Override
    protected void paintComponent(Graphics page) {
        super.paintComponent(page);

        page.drawString("Selected: " + selectedTileType, 0, 15);

        for (ArrayList<Tile> column : map) {
            for (Tile tile : column) {
                tile.draw(this, page, 0, topOffset);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }
    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent me) {
        int mapWidth = map.size() * Tile.TILE_SIZE;
        int mapHeight = map.get(0).size() * Tile.TILE_SIZE;

        if (me.getX() < mapWidth && me.getY() > topOffset && me.getY() < mapHeight + topOffset) {
            int gridX = me.getX() / Tile.TILE_SIZE;
            int gridY = (me.getY() - topOffset) / Tile.TILE_SIZE;

            if (map.get(gridX).get(gridY).getClass().getSimpleName().equals(selectedTileType) == false) {
                switch (selectedTileType) {
                    case "path" -> map.get(gridX).set(gridY, new Path(gridX, gridY));
                    case "goal" -> map.get(gridX).set(gridY, new Goal(gridX, gridY));
                    case "characterStart" -> map.get(gridX).set(gridY, new CharacterStart(gridX, gridY));
                    case "obstacle" -> map.get(gridX).set(gridY, new Obstacle(gridX, gridY));
                }
            }
        }

        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }
    @Override
    public void mouseExited(MouseEvent e) {

    }
}
