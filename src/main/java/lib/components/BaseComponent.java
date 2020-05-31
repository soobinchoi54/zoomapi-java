package lib.components;

import lib.utils.ApiClient;

import java.util.Map;

public class BaseComponent extends ApiClient{
    public BaseComponent(String base_uri, Map<String, String> config){
        super(base_uri, 15);
        this.config = config;
    }
}
