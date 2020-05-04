package zoomapi.botAPIs.subscribe;

import zoomapi.utils.Message;

import java.util.ArrayList;

/************************
 * Singleton Abstract Class Event
 ************************/

public abstract class Event implements Runnable{

    protected Thread thread;
    protected ArrayList<ChannelObserver> observers;
    protected volatile boolean work = false;
    protected Event(){
        this.observers = new ArrayList<>();
        if(thread == null) this.thread = new Thread(this);
    }

    public synchronized void addObserver(ChannelObserver observer){
        observers.add(observer);
    }

    public synchronized void deleteObserver(ChannelObserver observer){
        observers.remove(observer);
    }

    public synchronized void notifyObservers(Message message){
        for(int i = 0; i < observers.size(); i++){
            observers.get(i).update(message);
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
