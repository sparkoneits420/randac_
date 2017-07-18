package org.rac;

import javax.swing.*;
import java.awt.*;

/**
 * @author smokey
 * @date 7/16/2017
 * @description
 */
public class MainPanel extends JPanel {

    public JButton done;
    private JPanel panel1;
    private JCheckBox randomize;
    private JCheckBox breaks;


    public MainPanel() {
        super();
        initComponents();
    }

    private void initComponents() {

        randomize = new JCheckBox("Randomize movements and interval??");
        breaks = new JCheckBox("Take breaks?");
        done = new JButton("Done");
        panel1 = new JPanel();
        panel1.add(randomize, "North");
        panel1.add(breaks, "South");
        panel1.add(done);
        add(panel1, BorderLayout.CENTER);
    }
}
