package org.randac.tick;

import org.randac.Application;

import java.util.ArrayList;

/**
 * Created by smokey on 7/18/2017.
 */
public class TickQueue extends ArrayList<Tick> implements Runnable {

    public void run() {
        while(Application.isRunning()) {
            long cur = System.currentTimeMillis();
            for(Tick tick : this) {
                if(cur - tick.interval >= tick.last) {
                    tick.execute();
                    tick.last = cur;
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
