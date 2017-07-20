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
import java.util.ArrayList;
import java.util.LinkedList;
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
        setPreferredSize(new Dimension(450, 178));
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
            case NativeKeyEvent.VC_F6:

                if(bot != null) {
                    if (!bot.on)
                        bot.start();
                    break;
                }
                bot = new MouseBot();
                bot.start(); 
                break;
            case NativeKeyEvent.VC_F2:
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
            createPointMatrix(MouseInfo.getPointerInfo().getLocation());
            try {
                robot = new Robot();
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
        private ArrayList<Point> pointMatrix = new ArrayList<>(200);
        int matrixIndex = 0;
        long interval1, last1;
        boolean breakNow;

        @Override
        public void execute() {
            //System.out.println("wut1");

            if(on)
                if(!settings.randomize.isSelected()) {
                    long cur = System.currentTimeMillis();
                    if(cur - last1 >= interval1) {//execute without additional event
                        breakNow = true;
                        while(breakNow) {
                            try {
                                System.out.println("taking  break...");
                                long duration = (long) (3000 + 1500 * Math.random());
                                Thread.sleep(duration);
                                System.out.println("done... breaked for " + (double)duration / 1000+ " seconds");

                                last1 = cur;
                                //stop for 3 seconds or more
                                breakNow = false;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        interval1 = (long) (100000 + (125000 * Math.random()));

                    }
                    interval = (long) ((125 * Math.random()) + 125);
                    mouseClick();
                } else {
                    try {
                        long cur = System.currentTimeMillis();
                        if(cur - last1 >= interval1 && (settings.breaks.isSelected())) {//execute without additional event
                            breakNow = true;
                            while(breakNow) {
                                try {
                                    //System.out.println("taking  break...");
                                    long duration = (long) (3000 + 1500 * Math.random());
                                    Thread.sleep(duration);
                                    //System.out.println("done... breaked for " + (double)duration / 1000+ " seconds");

                                    last1 = cur;
                                    //stop for 3 seconds or more
                                    breakNow = false;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            interval1 = (long) (10000 + (15000 * Math.random()));

                        }
                        interval = (long) ((125 * Math.random()) + 125);
                        if(matrixIndex < pointMatrix.size()) {
                            mouseClick(pointMatrix.get(matrixIndex++));
                        } else {
                            matrixIndex = 0;
                            mouseClick(pointMatrix.get(matrixIndex));
                        }
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                }

        }

        public void mouseClick(Point p) throws NullPointerException {
            int x = (int)p.getX(), y = (int)p.getY();
            robot.mouseMove(x, y);
            mouseClick();
        }

        public void mouseClick() {
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        }

        public void createPointMatrix(Point center) {
            for(int variable = 0; variable < 4; variable++) {

                pointMatrix.add(WEST * variable, new Point(center.x - (int) (Math.random() * variable + 1), center.y));
                pointMatrix.add(NORTHWEST * variable, new Point(center.x - (int) (Math.random() * variable + 1),
                        center.y + (int) (Math.random() * variable + 1)));
                pointMatrix.add(NORTH * variable, new Point(center.x, center.y + (int) (Math.random() * variable + 1)));
                pointMatrix.add(NORTHEAST * variable, new Point(center.x + (int) (Math.random() * variable + 1),
                        center.y + (int) (Math.random() * variable + 1)));
                pointMatrix.add(EAST * variable, new Point(center.x + (int) (Math.random() * variable + 1), center.y));
                pointMatrix.add(SOUTHEAST * variable, new Point(center.x - (int) (Math.random() * variable + 1),
                        center.y + (int) (Math.random() * variable + 1)));
                pointMatrix.add(SOUTH * variable, new Point(center.x, center.y - (int) (Math.random() * variable + 1)));
                pointMatrix.add(SOUTHWEST * variable, new Point(center.x - (int) (Math.random() * variable + 1),
                        center.y - (int) (Math.random() * variable + 1)));
                pointMatrix.add(CENTER * variable, new Point(center.x, center.y));
            }
            for(Point p : pointMatrix) {
                //System.out.println("x: " + p.getX() + ", y: " + p.getY());
            }
        }
    }

    public static final byte  WEST = 0, NORTHWEST = 1, NORTH = 2, NORTHEAST = 3,
            CENTER = 4, EAST = 5, SOUTHEAST = 6, SOUTH = 7, SOUTHWEST = 8;
}
