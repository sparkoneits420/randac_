package org.randac.tick;

/**
 * Created by smokey on 7/18/2017.
 */
public abstract class Tick {
    public long interval = 500, last = 0;

    public abstract void execute();
}
