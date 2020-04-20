package zoomapi.utils;

import java.util.Date;

public class Throttle {
    private long time_stamp;
    private double INTERVAL = 0.10;
    public Throttle(){
        time_stamp = new Date().getTime();
    }

    public void throttled(){
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
        System.out.println("Throttled time stamp: " + time_stamp);
    }
}
