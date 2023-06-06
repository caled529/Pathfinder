import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import static javax.swing.JOptionPane.showMessageDialog;

/**
 * @author Cale Dillon
 * @version 2023/06/06
 */
public class Driver extends JFrame implements ActionListener {
    JFrame frame;
    Map map;
    JScrollPane mapScrollPane, buttonScrollPane;
    JPanel buttonPanel;
    JButton start, stop, reset, load, save;

    public Driver() {
        frame = new JFrame();
        frame.setTitle("Pathfinder");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        map = new Map(this, null);

        mapScrollPane = new JScrollPane(map);
        //mapScrollPane.setPreferredSize(new Dimension(640, 480));

        buttonPanel = new JPanel();

        start = new JButton("Start");
        start.addActionListener(this);
        start.setFont(new Font("Calibri", Font.PLAIN, 20));
        start.setMnemonic('\n');
        start.setEnabled(true);

        stop = new JButton("Stop");
        stop.addActionListener(this);
        stop.setFont(new Font("Calibri", Font.PLAIN, 20));
        stop.setMnemonic('\n');
        stop.setEnabled(false);
        
        reset = new JButton("Reset");
        reset.addActionListener(this);
        reset.setFont(new Font("Calibri", Font.PLAIN, 20));
        reset.setMnemonic('R');
        reset.setEnabled(true);

        load = new JButton("Load");
        load.addActionListener(this);
        load.setFont(new Font("Calibri", Font.PLAIN, 20));
        load.setMnemonic('L');
        load.setEnabled(true);

        save = new JButton("Save");
        save.addActionListener(this);
        save.setFont(new Font("Calibri", Font.PLAIN, 20));
        save.setMnemonic('S');
        save.setEnabled(true);
        
        buttonPanel.add(start);
        buttonPanel.add(stop);
        buttonPanel.add(reset);
        buttonPanel.add(load);
        buttonPanel.add(save);

        buttonScrollPane = new JScrollPane(buttonPanel);
        buttonScrollPane.setPreferredSize(new Dimension(0, 50));

        frame.getContentPane().add(mapScrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(buttonScrollPane, BorderLayout.SOUTH);
        frame.setMinimumSize(new Dimension(450, 240));
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton sourceButton = (JButton) e.getSource();

        if (sourceButton == start) {
            map.start();

            start.setEnabled(false);

            stop.setEnabled(true);
        }
        if (sourceButton == stop) {
            map.stop();

            stop.setEnabled(false);

            start.setEnabled(true);
        }
        if (sourceButton == reset) {
            if (map.goalFound()) {
                start.setEnabled(true);
                stop.setEnabled(false);
            }
            map.reset();
        }
        if (sourceButton == load) {
            map.stop();

            stop.setEnabled(false);

            start.setEnabled(true);

            FileDialog fd = new FileDialog(this, "Choose a file", FileDialog.LOAD);
            fd.setDirectory("maps");
            fd.setFile("*.txt");
            fd.setVisible(true);
            String filePath = fd.getDirectory() + "/" + fd.getFile();
            if (new Scanner(filePath).hasNextLine()) {
                String oldPath = map.getFILE_PATH();
                map = new Map(this, filePath);
                if (map.hasPaths() == false) {
                    showMessageDialog(null, "Please submit a map with at least one path (\".\").");
                    map = new Map(this, oldPath);
                }
                frame.getContentPane().remove(mapScrollPane);
                mapScrollPane = new JScrollPane(map);
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
                pWriter.write(map.toString());
                pWriter.close();
            } catch (FileNotFoundException ex) {
                showMessageDialog(null, "Unable to write to file.");
                throw new RuntimeException(ex);
            }
        }
    }

    public static void main(String[] args) {
        new Driver();
    }
}
