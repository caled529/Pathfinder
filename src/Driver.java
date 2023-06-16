import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * @author Cale Dillon
 * @version 2023/06/08
 */
public class Driver extends JFrame implements ActionListener {
    JFrame frame;
    Map viewer;
    MapEditor editor;
    JScrollPane mapScrollPane, buttonScrollPane, editorInternalScrollPane;
    JPanel viewerButtonPanel, editorInternalContainer, editorChangeHorizontal,
            editorChangeVertical, editorButtonPanel, editorTileSelect;
    JButton // map viewer buttons
            viewerStart, viewerStop, viewerReset, viewerLoad, startEditor,
            // map editor menu buttons
            editorLoad, save, exitEditor,
            // map editor selector buttons
            editorPath, editorGoal, editorObstacle, editorCharacterStart,
            // map editor size buttons
            editorIncreaseHorizontal, editorDecreaseHorizontal,
            editorIncreaseVertical, editorDecreaseVertical;
    final Font CALIBRI20 = new Font("Calibri", Font.PLAIN, 20);

    /**
     * Constructor for the <code>Driver</code> class. Initializes all graphical
     * components
     */
    public Driver() {
        // -------------------- viewer initialization --------------------

        viewer = new Map(this, "maps/default.txt");

        mapScrollPane = new JScrollPane(viewer);

        viewerButtonPanel = new JPanel();

        viewerStart = makeButton("Start", null, '\n', true, CALIBRI20);

        viewerStop = makeButton("Stop", null, '\n', false, CALIBRI20);

        viewerReset = makeButton("Reset", null, 'R', true, CALIBRI20);

        viewerLoad = makeButton("Load", null, 'L', true, CALIBRI20);

        startEditor = makeButton("Editor", null, 'E', true, CALIBRI20);
        
        viewerButtonPanel.add(viewerStart);
        viewerButtonPanel.add(viewerStop);
        viewerButtonPanel.add(viewerReset);
        viewerButtonPanel.add(viewerLoad);
        viewerButtonPanel.add(startEditor);
        
        buttonScrollPane = new JScrollPane(viewerButtonPanel);
        buttonScrollPane.setPreferredSize(new Dimension(0, 50));

        // -------------------- editor initialization --------------------

        editorButtonPanel = new JPanel();

        editorLoad = makeButton("Load", null, 'L', false, CALIBRI20);

        save = makeButton("Save", null, 'S', false, CALIBRI20);

        exitEditor = makeButton("Exit", null, 'E', false, CALIBRI20);
        
        editorButtonPanel.add(editorLoad);
        editorButtonPanel.add(save);
        editorButtonPanel.add(exitEditor);

        // -------------------- tile selector initialization --------------------
        
        editorTileSelect = new JPanel();
        
        editorPath = makeButton("Path", new ImageIcon("assets/path.png"), 'P',
                false, null);
        
        editorGoal = makeButton("Goal", new ImageIcon("assets/goal.png"), 'G',
                false, null);
        
        editorObstacle = makeButton("Obstacle",
                new ImageIcon("assets/obstacle.png"), 'O', false, null);
        
        editorCharacterStart = makeButton("Character Start",
                new ImageIcon("assets/characterStart.png"), 'C', false, null);
        
        editorTileSelect.add(editorPath);
        editorTileSelect.add(editorGoal);
        editorTileSelect.add(editorObstacle);
        editorTileSelect.add(editorCharacterStart);
        editorTileSelect.setPreferredSize(new Dimension(150, 0));
        
        // --------------- editor window and inc/dec initialization ---------------

        editorInternalContainer = new JPanel();
        editorInternalContainer.setLayout(new BorderLayout());

        editor = new MapEditor(this, "maps/default.txt");

        editorChangeHorizontal = new JPanel();

        editorIncreaseHorizontal = makeButton(">", null, '.', false, null);

        editorDecreaseHorizontal = makeButton("<", null, ',', false, null);
        
        editorChangeHorizontal.add(editorIncreaseHorizontal);
        editorChangeHorizontal.add(editorDecreaseHorizontal);
        editorChangeHorizontal.setPreferredSize(new Dimension(25, 0));

        editorChangeVertical = new JPanel();

        editorIncreaseVertical = makeButton("v", null, '=', false, null);

        editorDecreaseVertical = makeButton("^", null, '-', false, null);

        editorChangeVertical.add(editorIncreaseVertical);
        editorChangeVertical.add(editorDecreaseVertical);
        editorChangeVertical.setPreferredSize(new Dimension(0, 25));

        editorInternalScrollPane = new JScrollPane(editor);

        editorInternalContainer.add(editorInternalScrollPane, BorderLayout.CENTER);
        editorInternalContainer.add(editorChangeHorizontal, BorderLayout.EAST);
        editorInternalContainer.add(editorChangeVertical, BorderLayout.SOUTH);

        // -------------------- JFrame initialization --------------------

        frame = new JFrame();
        frame.setTitle("Pathfinder");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(640, 480));
        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        frame.setMaximumSize(new Dimension(screenBounds.width, screenBounds.height));

        frame.getContentPane().add(mapScrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(buttonScrollPane, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public JButton makeButton(String buttonText, ImageIcon icon, char mnemonic,
                              boolean isEnabled, Font font) {
        JButton newButton = new JButton(buttonText, icon);
        
        newButton.addActionListener(this);
        newButton.setMnemonic(mnemonic);
        newButton.setEnabled(isEnabled);
        newButton.setFont(font);

        return newButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();

        if (sourceButton == viewerStart) {
            viewer.start();

            viewerStart.setEnabled(false);

            viewerStop.setEnabled(true);
        }
        if (sourceButton == viewerStop) {
            viewer.stop();

            viewerStop.setEnabled(false);

            viewerStart.setEnabled(true);
        }
        if (sourceButton == viewerReset) {
            if (viewer.goalFound()) {
                viewerStart.setEnabled(true);
                viewerStop.setEnabled(false);
            }
            viewer.reset();
        }
        if (sourceButton == viewerLoad) {
            viewer.stop();

            viewerStop.setEnabled(false);

            viewerStart.setEnabled(true);

            FileDialog fd = new FileDialog(this, "Choose a file", FileDialog.LOAD);
            fd.setDirectory("maps");
            fd.setFile("*.txt");
            fd.setVisible(true);
            String filePath = fd.getDirectory() + "/" + fd.getFile();
            if (new Scanner(filePath).hasNextLine()) {
                String oldPath = viewer.getFILE_PATH();
                viewer = new Map(this, filePath);
                if (viewer.hasPaths() == false) {
                    showMessageDialog(null, "Please submit a map with at least one path (\".\").");
                    viewer = new Map(this, oldPath);
                }
                frame.getContentPane().remove(mapScrollPane);
                mapScrollPane = new JScrollPane(viewer);
                frame.getContentPane().add(mapScrollPane, BorderLayout.CENTER);
                frame.pack();
            }
            else
                showMessageDialog(null, "Please submit a text file with characters.");
        }
        if (sourceButton == startEditor) {
            viewer.stop();

            Component[] components;

            components = viewerButtonPanel.getComponents();

            for (Component component : components) {
                component.setEnabled(false);
            }

            components = editorButtonPanel.getComponents();

            for (Component component : components) {
                component.setEnabled(true);
            }

            components = editorTileSelect.getComponents();

            for (Component component : components) {
                component.setEnabled(true);
            }

            components = editorChangeHorizontal.getComponents();

            for (Component component : components) {
                component.setEnabled(true);
            }

            components = editorChangeVertical.getComponents();

            for (Component component : components) {
                component.setEnabled(true);
            }

            editorInternalContainer.remove(editorInternalScrollPane);
            editor = new MapEditor(this, viewer.getFILE_PATH());
            editorInternalScrollPane = new JScrollPane(editor);
            editorInternalContainer.add(editorInternalScrollPane, BorderLayout.CENTER);

            frame.getContentPane().removeAll();
            buttonScrollPane = new JScrollPane(editorButtonPanel);
            frame.getContentPane().add(editorInternalContainer, BorderLayout.CENTER);
            frame.getContentPane().add(buttonScrollPane, BorderLayout.SOUTH);
            frame.getContentPane().add(editorTileSelect, BorderLayout.EAST);
            editor.revalidate();
            frame.pack();
            frame.repaint();
        }
        if (sourceButton == editorLoad) {
            FileDialog fd = new FileDialog(this, "Choose a file", FileDialog.LOAD);
            fd.setDirectory("maps");
            fd.setFile("*.txt");
            fd.setVisible(true);
            String filePath = fd.getDirectory() + "/" + fd.getFile();
            if (new Scanner(filePath).hasNextLine()) {
                editor = new MapEditor(this, filePath);
                frame.getContentPane().remove(mapScrollPane);
                mapScrollPane = new JScrollPane(viewer);
                frame.getContentPane().add(mapScrollPane, BorderLayout.CENTER);
                frame.pack();
            }
            else
                showMessageDialog(null, "Please submit a text file with characters.");
        }
        if (sourceButton == save) {
            FileDialog fd = new FileDialog(this, "Save file", FileDialog.SAVE);
            fd.setDirectory("maps");
            fd.setFile(".txt");
            fd.setVisible(true);
            String filePath = fd.getDirectory() + "/" + fd.getFile();

            try {
                PrintWriter pWriter = new PrintWriter(filePath);
                pWriter.write(editor.toString());
                pWriter.close();
            } catch (FileNotFoundException ex) {
                showMessageDialog(null, "Unable to write to file.");
                throw new RuntimeException(ex);
            }
        }
        if (sourceButton == exitEditor) {
            Component[] components;

            components = viewerButtonPanel.getComponents();

            for (Component component : components) {
                component.setEnabled(true);
            }

            components = editorButtonPanel.getComponents();

            for (Component component : components) {
                component.setEnabled(false);
            }

            components = editorTileSelect.getComponents();

            for (Component component : components) {
                component.setEnabled(false);
            }

            components = editorChangeHorizontal.getComponents();

            for (Component component : components) {
                component.setEnabled(false);
            }

            components = editorChangeVertical.getComponents();

            for (Component component : components) {
                component.setEnabled(false);
            }

            String filePath = "maps/untitledMap.txt";

            try {
                PrintWriter pWriter = new PrintWriter(filePath);
                pWriter.write(editor.toString());
                pWriter.close();
            } catch (FileNotFoundException ex) {
                showMessageDialog(null, "Unable to write to file.");
                throw new RuntimeException(ex);
            }

            viewer = new Map(this, filePath);
            viewer.setLayout(new BorderLayout());

            frame.getContentPane().removeAll();
            mapScrollPane = new JScrollPane(viewer);
            buttonScrollPane = new JScrollPane(viewerButtonPanel);
            frame.getContentPane().add(mapScrollPane, BorderLayout.CENTER);
            frame.getContentPane().add(buttonScrollPane, BorderLayout.SOUTH);
            frame.pack();
        }
        if (sourceButton == editorIncreaseHorizontal)
            editor.expandHorizontal();
        if (sourceButton == editorDecreaseHorizontal)
            editor.decreaseHorizontal();
        if (sourceButton == editorIncreaseVertical)
            editor.expandVertical();
        if (sourceButton == editorDecreaseVertical)
            editor.decreaseVertical();
        if (sourceButton == editorPath)
            editor.setSelectedTile("path");
        if (sourceButton == editorGoal)
            editor.setSelectedTile("goal");
        if (sourceButton == editorObstacle)
            editor.setSelectedTile("obstacle");
        if (sourceButton == editorCharacterStart)
            editor.setSelectedTile("characterStart");
    }

    public static void main(String[] args) {
        new Driver();
    }
}