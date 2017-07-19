package org.randac;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.randac.tick.Tick;
import org.randac.tick.TickQueue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author smokey
 *      Date: 7/16/2017
 *      Description: Randomized autoclicker that utilizes hotkeys and random movements and takes
 *      short afk breaks using random times. Built for runescape.
 */
public class Application extends JFrame
        implements NativeKeyListener {


    private static boolean running = true;
    private MouseBot bot;
    private MainPanel settings;
    private Point start; 

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
        settings = new MainPanel();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(730, 178));
        setAlwaysOnTop(true);
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }
        LogManager.getLogManager().reset();
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        GlobalScreen.addNativeKeyListener(this);
        this.add(settings, BorderLayout.CENTER);
        pack();
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case NativeKeyEvent.VC_S:

                if(bot != null) {
                    if (!bot.on)
                        bot.start();
                    break;
                }
                bot = new MouseBot();
                bot.start(); 
                break;
            case NativeKeyEvent.VC_D:
                if(bot == null)
                    return;
                bot.stop();
                break;
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
//unused
    }

    public static boolean isRunning() {
        return running;
    }

    private class MouseBot extends Tick {
        private TickQueue tq;
        private Application app;
        private ExecutorService executor;
        private Robot robot;
        boolean on = false;
        public void start() {
            if(on)
                return;
            on = true;
            executor = Executors.newSingleThreadExecutor();
            tq = new TickQueue();
            try {
                robot = new Robot();
                if(pointMatrix == null && settings.randomize.isSelected()) {
                    start = getMousePosition();
                    pointMatrix = createPointMatrix(start);
                }
            } catch (AWTException e) {
                e.printStackTrace();
            }
            executor.submit(tq);
            tq.add(this);
            interval = 500;
        }

        public void stop() {
            on = false;
            executor.shutdown();
            //System.exit(0);
        }
        private Point[] pointMatrix = new Point[9];
        @Override
        public void execute() {
            System.out.println("wut1");
            if(on)
                if(!settings.randomize.isSelected()) {
                    mouseClick();
                } else {
                    interval = (long) ((250 * Math.random()) + 250);
                    mouseClick(pointMatrix[(int)(9 * Math.random())]);
                }

        }

        public void mouseClick(Point p) {
            System.out.println("trying to move");
            robot.mouseMove((int)p.getX(), (int)p.getY());
            mouseClick();
            //back to center so we dont get thrown off or move more than 1 pixel to a time
            robot.mouseMove(pointMatrix[CENTER].x, pointMatrix[CENTER].y);
        }

        public void mouseClick() {
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        }

        public Point[] createPointMatrix(Point center) {
            Point[] pointMatrix = new Point[9];
            pointMatrix[WEST] = new Point(center.x - 1, center.y);
            pointMatrix[NORTHWEST] = new Point(center.x - 1, center.y + 1);
            pointMatrix[NORTH] = new Point(center.x, center.y + 1);
            pointMatrix[NORTHEAST] = new Point(center.x + 1, center.y + 1);
            pointMatrix[EAST] = new Point(center.x + 1, center.y);
            pointMatrix[SOUTHEAST] = new Point(center.x - 1, center.y + 1);
            pointMatrix[SOUTH] = new Point(center.x, center.y - 1);
            pointMatrix[SOUTHWEST] = new Point(center.x - 1, center.y - 1);
            pointMatrix[CENTER] = new Point(center.x, center.y);
            return pointMatrix;
        }
    }
 
    public static final byte  WEST = 0, NORTHWEST = 1, NORTH = 2, NORTHEAST = 3,
            CENTER = 4, EAST = 5, SOUTHEAST = 6, SOUTH = 7, SOUTHWEST = 8;
}
