package zoomapi.botAPIs.subscribe;

import java.util.ArrayList;

public class Event {
    ArrayList<CommandEventHandler> observers;
    public Event(){
        observers = new ArrayList<>();
    }
    public void addObserver(CommandEventHandler observer){
        observers.add(observer);
    }

    public void notifyObservers(){
        for(int i = 0; i < observers.size(); i++){
            observers.get(i).update();
        }
    }
}
