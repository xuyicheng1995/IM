package com.xyc.im.client.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xuyicheng
 * @date 2019/6/12 16:45
 */
@ConfigurationProperties(prefix = "im.route.url")
@Component
public class RouteConfig {
    private String getRoute;
    private String chat;
    private String logout;

    public String getLogout() {
        return logout;
    }

    public void setLogout(String logout) {
        this.logout = logout;
    }

    public String getChat() {
        return chat;
    }
    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getGetRoute() {
        return getRoute;
    }

    public void setGetRoute(String getRoute) {
        this.getRoute = getRoute;
    }
}
