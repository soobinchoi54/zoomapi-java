package zoomapi.utils;

import org.json.JSONObject;

public interface OauthCondition {
    boolean isTrue(JSONObject message);
}
