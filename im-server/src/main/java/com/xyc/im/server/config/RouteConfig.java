package com.xyc.im.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xuyicheng
 * @date 2019/6/12 16:45
 */
@ConfigurationProperties(prefix = "im.route.url")
@Component
public class RouteConfig {
    private String logout;

    public String getLogout() {
        return logout;
    }

    public void setLogout(String logout) {
        this.logout = logout;
    }
}
