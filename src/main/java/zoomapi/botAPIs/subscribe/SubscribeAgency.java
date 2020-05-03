package zoomapi.botAPIs.subscribe;

public class SubscribeAgency {
    private static int MAX_EVENT_NUMBER = 3;
    private static Event[] events = new Event[MAX_EVENT_NUMBER];
    public static void initialize(){
        for(int i = 0; i < MAX_EVENT_NUMBER; i++){
            events[i] = new Event();
        }
    }

    public static void subscribeTo(int eventCode, CommandEventHandler handler){
        events[eventCode].addObserver(handler);
    }

    // will call the call-back method once data changes
    public static void announce(int eventCode, String eventParams){
        events[eventCode].notifyObservers();
    }
}
