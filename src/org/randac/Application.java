package org.randac;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.randac.tick.Tick;
import org.randac.tick.TickQueue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
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
 *      @implNote excuse my conventions, it was written in a hurry so its horribly cluttered and
 *      looks like shit
 */
public class Application extends JFrame
        implements NativeKeyListener {

    public static final byte  WEST = 0, NORTHWEST = 1, NORTH = 2, NORTHEAST = 3,
            CENTER = 4, EAST = 5, SOUTHEAST = 6, SOUTH = 7, SOUTHWEST = 8;

    private static boolean running = true;
    private MouseBot bot;
    private MainPanel settings;
    private ArrayList<Point> pointMatrix = new ArrayList<>();
    private int matrixIndex = 0;
    private long interval1, last1;
    private boolean breakNow;
    private TickQueue tq;
    private ExecutorService executor;
    private Robot robot;
    private boolean on = false;

    public Application() {
        super("RANDAC - 1.0");
        initComponents();
    }

    public static void main(String args[]) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
                setDefaultLookAndFeelDecorated(true);
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            Application app = new Application();
            SwingUtilities.updateComponentTreeUI(app);
            app.setVisible(true);
        });
    }

    private void initComponents() {
        settings = new MainPanel();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(550, 200));
        setAlwaysOnTop(true);
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
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
    public void nativeKeyPressed(NativeKeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case NativeKeyEvent.VC_F6:
                if(bot != null) {
                    if (!on)
                        bot.start();
                    break;
                }
                bot = new MouseBot();
                bot.start(); 
                break;
            case NativeKeyEvent.VC_F2:
                if(bot == null)
                    return;
                on = false;
                break;
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
//unused
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
//unused
    }


    public static boolean isRunning() {
        return running;
    }

    private class MouseBot extends Tick {

        public void start() {
            if (on)
                return;
            on = true;
            executor = Executors.newSingleThreadExecutor();
            tq = new TickQueue();
            createPointMatrix(MouseInfo.getPointerInfo().getLocation());
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
                System.exit(2);
            }
            executor.submit(tq);
            tq.add(this);
            interval = getInterval();
        }

        @Override
        public void execute() {
            if(!on)
                return;
            long cur = System.currentTimeMillis();
            if (cur - last1 >= interval1 && (settings.breaks.isSelected())) {
                breakNow = true;
                while (breakNow) {
                    try {
                        long duration = (long) (3000 + 1500 * Math.random());
                        Thread.sleep(duration);
                        last1 = cur;
                        breakNow = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.exit(3);
                    }
                }
                interval1 = (long) (10000 + (15000 * Math.random()));

            }
            interval = getInterval();
            if(settings.randomize.isSelected()) {
                if (matrixIndex < pointMatrix.size()) {
                    mouseClick(pointMatrix.get(matrixIndex++));
                } else {
                    matrixIndex = 0;
                    mouseClick(pointMatrix.get(matrixIndex));
                }
            } else
                mouseClick();
        }

        public void mouseClick(Point p) {
            int x = (int) p.getX(), y = (int) p.getY();
            Point loc = MouseInfo.getPointerInfo().getLocation();
            if (Math.abs(loc.getX() - x) >= 4 && Math.abs(loc.getY() - y) >= 4) {
                Point p2 = pointMatrix.get(CENTER);
                robot.mouseMove((int) p2.getX(), (int) p2.getY());

            }
            robot.mouseMove(x, y);
            mouseClick();
        }

        public void mouseClick() {
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        }

        public long getInterval() {
            if (settings.interval.getText().isEmpty()) {
                return (long) (((settings.randomize.isSelected() ? 125 : 50) * Math.random()) + (settings.randomize.isSelected() ? 125 : 250));
            }
            return Long.parseLong(settings.interval.getText());
        }

        public void createPointMatrix(Point center) {
            for (int variable = 0; variable < 3; variable++) {
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
        }
    }
}
