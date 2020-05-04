package zoomapi.botAPIs.subscribe;

import zoomapi.OauthZoomClient;
import zoomapi.utils.Event;
import zoomapi.utils.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscribeAgency {
    private static final int MAX_EVENT_NUMBER = 10;

    /**
     * Command event #1: notify new messages
     */
    public static final int NOTIFY_NEW_MESSAGES = 1;

    /**
     * Command event #2: notify new members
     */
    public static final int NOTIFY_NEW_MEMBERS = 2;

    /**
     * Command event #3: notify message updates
     */
    public static final int NOTIFY_MESSAGE_UPDATES = 3;

    /**
     * A HashMap array:
     * Array stores the HashMap of specify Event functionality
     * HashMap stores the ChannelName, <T> Event(ChannelName) pair
     */
    private static List<Map<String, EventHandler>> events;

    public static void initialize(){
        events = new ArrayList<>(MAX_EVENT_NUMBER);
        for(int i = 0; i < MAX_EVENT_NUMBER; i++){
            events.add(new HashMap<>());
        }
    }

    public static void subscribeTo(int eventCode, ChannelObserver observer){
        checkValidation(eventCode);
        String channelName = observer.getChannelName();
        String fromDate = observer.getObservingDate()[0];
        String toDate = observer.getObservingDate()[1];
        OauthZoomClient client = observer.getClient();
        Map<String, EventHandler> map = events.get(eventCode);
        if(map.containsKey(channelName)){
            map.get(channelName).addObserver(observer);
        }else{
            map.put(channelName, createNewEvent(eventCode, client, channelName, fromDate, toDate));
            map.get(channelName).addObserver(observer);
            map.get(channelName).startWorking();
        }
    }

    private static void checkValidation(int eventCode){
        if(eventCode>=MAX_EVENT_NUMBER || eventCode<0){
            throw new IllegalArgumentException("Invalid Event Code");
        }
    }
    private static EventHandler createNewEvent(int eventCode, OauthZoomClient client, String channelName, String fromDate, String toDate){
        if(eventCode == NOTIFY_NEW_MESSAGES){
            return new NewMessagesEventHandler(client,channelName, fromDate, toDate);
        }else if(eventCode == NOTIFY_NEW_MEMBERS){
            return new NewMembersEventHandler(client, channelName, fromDate, toDate);
        } else if(eventCode == NOTIFY_MESSAGE_UPDATES){
            return new UpdatedMessagesEventHandler(client,channelName, fromDate, toDate);
        }else return null;
    }

    // will call the call-back method once data changes
    public static void announce(int eventCode, String channelName, Event e){
        events.get(eventCode).get(channelName).notifyObservers(e);
    }

    public static void unsubscribeFrom(int eventCode, ChannelObserver observer){
        String channelName = observer.getChannelName();
        EventHandler event = events.get(eventCode).get(channelName);
        event.deleteObserver(observer);
        if(event.getObservers().size()==0) event.stopWorking();
    }
}
