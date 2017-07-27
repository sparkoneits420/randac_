package org.randac.tick;

import java.util.ArrayList;

/**
 * @author smokey
 * @date 7/18/2017
 */
public class TickQueue extends ArrayList<Tick> implements Runnable {

    public void run() {
        while(true) {
            long cur = System.currentTimeMillis();
            for(Tick tick : this) {
                if(cur - tick.last >= tick.interval) {
                    tick.execute();
                    tick.last = cur;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
