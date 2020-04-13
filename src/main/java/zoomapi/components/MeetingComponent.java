package zoomapi.components;

import zoomapi.utils.ApiClient;

public class MeetingComponent extends ApiClient{
    public MeetingComponent(String base_uri, int timeout){
        super(base_uri, timeout);
    }
}
