package org.rac;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author smokey
 *      Date: 7/16/2017
 *      Description: Randomized autoclicker that utilizes hotkeys and random movements and takes
 *      short afk breaks using random times. Built for runescape.
 */
public class Application extends JFrame implements ActionListener, KeyListener {


    public Application() {
        super("RANDAC");
        initComponents();
    }

    public static void main(String args[]) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            new Application().setVisible(true);
        });
    }

    private void initComponents() {
        MainPanel settings = new MainPanel();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(730, 130));
        settings.done.addActionListener(this);
        this.add(settings, BorderLayout.CENTER);
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand().toLowerCase();
        switch (cmd) {
            case "done":
                //todo: robot logic
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case 0x53://VK_S for start
                break;
            case 0x1b://VK_ESCAPE
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
//unused
    }

    @Override
    public void keyReleased(KeyEvent e) {
//unused
    }
}
