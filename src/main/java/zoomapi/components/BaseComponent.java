package zoomapi.components;

import zoomapi.utils.ApiClient;

public class BaseComponent extends ApiClient{
    public BaseComponent(String base_uri, int timeout){
        super(base_uri, timeout);
    }
}