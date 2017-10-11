package com.spider.proxypool.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by 13 on 2017/10/11.
 */
public abstract class Scheduler implements Runnable {

    private final ScheduledExecutorService exec =
            Executors.newScheduledThreadPool(1);


    private final long defaultInterval;
    private final TimeUnit defaultUnit;


    public Scheduler(long defaultInterval, TimeUnit defaultUnit) {
        this.defaultInterval = defaultInterval;
        this.defaultUnit = defaultUnit;
    }

    public void schedule(long interval, TimeUnit unit){
        exec.scheduleAtFixedRate(this, 0, interval, unit);
    }

    public void schedule(){
        schedule(defaultInterval, defaultUnit);
    }

    public void shutdown(){
        exec.shutdown();
    }

    public void shutdownNow(){
        exec.shutdownNow();
    }

}
