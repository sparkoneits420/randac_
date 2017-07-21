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
    public JTextField interval;

    public MainPanel() {
        super();
        initComponents();
    }

    private void initComponents() {

        randomize = new JCheckBox("Randomize movements and interval?");
        breaks = new JCheckBox("Take breaks?");
        interval = new JTextField("", 12);
        label1 = new JLabel("Live settings, 'F6' on your keyboard to start, and 'F2' to stop.");
        label2 = new JLabel("The default interval is 125 + (0 - 125) (ms)(random) or 250ms(non-random)");
        panel1 = new JPanel();
        panel1.add(new JLabel("Interval in milliseconds:"), "North");
        panel1.add(interval, "North");
        panel1.add(new JSeparator());
        JPanel panel2 = new JPanel();
        panel2.add(randomize, "South");
        panel2.add(breaks,"South");
        add(label1, "North");
        add(new JSeparator());
        add(label2, "South");
        add(panel1, "Center");
        add(new JSeparator(), "Center");
        add(panel2, "South");


    }
}
