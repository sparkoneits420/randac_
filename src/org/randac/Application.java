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
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    private ArrayList<Point> pointMatrix;
    private int matrixIndex = 0;
    private long interval1, last1;
    private boolean breakNow;
    private TickQueue tq;
    private ExecutorService executor;
    private Robot robot;
    private Point start;
    private Future<?> threadFuture;

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
                if(bot == null) {
                    bot = new MouseBot();
                }
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

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
//unused
    }


    public static boolean isRunning() {
        return running;
    }

    private class MouseBot extends Tick {

        public void start() {
            running = true;
            if (executor == null)
                executor = Executors.newSingleThreadExecutor();
            if(tq == null)
                tq = new TickQueue();
            if(pointMatrix == null)
                start = MouseInfo.getPointerInfo().getLocation();
                pointMatrix = createPointMatrix(start, 2);
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
                System.exit(2);
            }
            threadFuture = executor.submit(tq);
            tq.add(this);
            interval = getInterval();
        }

        public void stop() {
            threadFuture.cancel(true);
            executor = null;
            tq.clear();
            tq = null;
            pointMatrix = null;
            running = false;
        }

        @Override
        public void execute() {
            if(!isRunning())
                return;
            long cur = System.currentTimeMillis();
            if (cur - last1 >= interval1 && (settings.breaks.isSelected())) {
                breakNow = true;
                while (breakNow) {
                    try {
                        long duration = (long) (3000 + 30000 * Math.random());
                        Thread.sleep(duration);
                        last1 = cur;
                        breakNow = false;
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                        //System.exit(3);
                    }
                }
                interval1 = (long) (1500000 + (1500000 * Math.random()));

            }
            interval = getInterval();
            if(settings.randomize.isSelected()) {
                mouseClick(getNextPoint());
            } else
                mouseClick();
        }

        public void mouseClick(Point p) {
            int x = (int) p.getX(), y = (int) p.getY();
            Point loc = MouseInfo.getPointerInfo().getLocation();
            if (Math.abs(loc.getX() - x) > 5 || Math.abs(loc.getY() - y) > 5) {
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

        private Point last;

        public Point getNextPoint() {
            //for(;;) {
                Point p = pointMatrix.get((int) (Math.random() * pointMatrix.size()));
                if(matrixIndex >= pointMatrix.size()) {
                    matrixIndex = 0;
                }
               // System.out.println(pointMatrix.size());
                //if (getDistance(last, p) <= 1) {
                 //   last = p;
                    return p;
                //}
            //}
        }

        /*public void createPointMatrix(Point center, int radius) {
            for (int variable = 0; variable < radius; variable++) {
                pointMatrix.add(WEST * variable, new Point(center.x - (int)
                        (Math.random() * variable + 1), center.y));
                pointMatrix.add(NORTHWEST * variable, new Point(center.x - (int)
                        (Math.random() * variable + 1),
                        center.y + (int) (Math.random() * variable + 1)));
                pointMatrix.add(NORTH * variable, new Point(center.x, center.y + (int)
                        (Math.random() * variable + 1)));
                pointMatrix.add(NORTHEAST * variable, new Point(center.x + (int)
                        (Math.random() * variable + 1), center.y + (int)
                        (Math.random() * variable + 1)));
                pointMatrix.add(EAST * variable, new Point(center.x + (int)
                        (Math.random() * variable + 1), center.y));
                pointMatrix.add(SOUTHEAST * variable, new Point(center.x - (int)
                        (Math.random() * variable + 1),
                        center.y + (int) (Math.random() * variable + 1)));
                pointMatrix.add(SOUTH * variable, new Point(center.x, center.y - (int)
                        (Math.random() * variable + 1)));
                pointMatrix.add(SOUTHWEST * variable, new Point(center.x - (int)
                        (Math.random() * variable + 1),
                        center.y - (int) (Math.random() * variable + 1)));
                pointMatrix.add(CENTER * variable, new Point(center.x, center.y));
            }
        }*/

        public ArrayList<Point> createPointMatrix(Point center, int radius) {
            ArrayList<Point> points = new ArrayList<>();
            double h = center.getX(), k = center.getY();
            last = center;
            //System.out.println("lol");
            for(int r = 0; r < radius; r++) {
                for(int t = 0; t < 360; t++) {
                    double temp = t * (Math.PI / 180);
                    int x = (int) (r * Math.sin(temp) + h);
                    int y = (int) (r * Math.cos(temp) + k);

                    Point point = new Point(x, y);
                    if(!points.contains(point)) {
                        points.add(point);
                        //System.out.println("R: " + r + ", x: " + x + ", y: " + y + ", angle: " + t);
                    }
                }
            }
            return points;
        }

        public double getDistance(Point p1, Point p2) {
            int x1 = (int)p1.getX(), y1 = (int)p1.getY(),
                    x2 = (int)p2.getX(), y2 = (int)p2.getY();
            int x3 = (x2 - x1) * (x2 - x1),
                    y3 = (y2 - y1) * (y2 - y1);
            return Math.sqrt(x3 + y3);
        }
    }
}
