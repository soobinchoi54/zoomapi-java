package lib.utils;

import java.util.Date;

public class Throttle {
    private volatile long time_stamp;
    private double INTERVAL = 1.5;
    public Throttle(){
        time_stamp = new Date().getTime();
    }

    public synchronized void throttled(){
        long cur = new Date().getTime();
        long diff = cur - this.time_stamp;
        if(diff < (long) (INTERVAL*1000)){
            try{
                long to_sleep = (long) (INTERVAL*1000 - (int) diff);
                Thread.sleep(to_sleep);
            } catch (InterruptedException e){
                System.out.println("Throttle failed");
            }
        }
        this.time_stamp = new Date().getTime();
        // System.out.println("Throttled time stamp: " + time_stamp);
    }
}
