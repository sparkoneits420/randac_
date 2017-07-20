package org.randac;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.Highlighter;
import java.awt.*;

/**
 * @author smokey
 */
public class MainPanel extends JPanel {

    public JLabel label1, label2;
    private JPanel panel1;
    public JCheckBox randomize;
    public JCheckBox breaks;


    public MainPanel() {
        super();
        initComponents();
    }

    private void initComponents() {

        randomize = new JCheckBox("Randomize movements and interval?");
        randomize.setFocusable(false);
        breaks = new JCheckBox("Take breaks?");
        breaks.setFocusable(false);
        label1 = new JLabel("Live settings, 'S' on your keyboard to start 'D' to stop");
        label2 = new JLabel("The default interval is 250ms + 250 * random ");
        panel1 = new JPanel();
        panel1.setBorder(new TitledBorder(new EtchedBorder(), "Settings..."));
        panel1.add(randomize);
        panel1.add(breaks);
        add(label1, "North");

        add(panel1, "Center");
        add(label2, "South");
    }
}
