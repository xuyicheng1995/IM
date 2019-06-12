package com.xyc.commons.pojo;

import java.io.Serializable;

/**
 * @author xuyicheng
 * @date 2019/6/12 19:01
 */
public class ChatInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -653331833928487964L;

    private String command;
    private long time;
    private Integer userId;
    private String content;

    public ChatInfo(String command, long time, Integer userId, String content) {
        this.command = command;
        this.time = time;
        this.userId = userId;
        this.content = content;
    }
    public ChatInfo() {
    }
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
