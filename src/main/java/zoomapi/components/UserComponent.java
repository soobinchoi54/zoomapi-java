package zoomapi.components;

import zoomapi.utils.ApiClient;

public class UserComponent extends ApiClient{
    public UserComponent(String base_uri, int timeout){
        super(base_uri, timeout);
    }
}
