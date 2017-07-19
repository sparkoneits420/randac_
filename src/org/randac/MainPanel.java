package org.randac;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.Highlighter;
import java.awt.*;

/**
 * @author smokey
 */
public class MainPanel extends JPanel {

    public JLabel label;
    private JPanel panel1;
    public JCheckBox randomize;
    public JCheckBox breaks;


    public MainPanel() {
        super();
        initComponents();
    }

    private void initComponents() {

        randomize = new JCheckBox("Randomize movements and interval??");
        randomize.setFocusable(false);
        breaks = new JCheckBox("Take breaks?");
        breaks.setFocusable(false);
        label = new JLabel("Live settings, 'S' on your keyboard to start 'D' to stop");
        panel1 = new JPanel();
        panel1.setBorder(new BevelBorder(BevelBorder.LOWERED));
        panel1.add(randomize);
        panel1.add(breaks);
        add(label, "South");
        add(panel1, "South");
    }
}
