package zoomapi.utils;

import java.util.Map;

public interface OauthCondition {
    boolean isTrue(Map<String, String> message);
}
