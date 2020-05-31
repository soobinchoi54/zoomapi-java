package lib.subscription;

import lib.utils.Unit;

import java.util.ArrayList;

/************************
 * Singleton Abstract Class Event
 ************************/

public abstract class EventHandler implements Runnable{

    protected Thread thread;
    protected ArrayList<ChannelObserver> observers;
    protected volatile boolean work = false;
    protected EventHandler(){
        this.observers = new ArrayList<>();
        if(thread == null) this.thread = new Thread(this);
    }

    public synchronized void addObserver(ChannelObserver observer){
        observers.add(observer);
    }

    public synchronized void deleteObserver(ChannelObserver observer){
        observers.remove(observer);
    }

    public synchronized void notifyObservers(Unit e){
        for(int i = 0; i < observers.size(); i++){
            observers.get(i).update(e);
        }
    }

    public ArrayList<ChannelObserver> getObservers(){
        return this.observers;
    }

    public void startWorking(){
        this.work = true;
        this.thread.start();
    }

    public void stopWorking(){
        this.work = false;
        try{
            this.thread.join();
            this.thread.interrupt();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
