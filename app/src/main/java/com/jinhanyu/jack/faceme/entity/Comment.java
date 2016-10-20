package com.jinhanyu.jack.faceme.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class Comment extends BmobObject{
 private User toUser;
    private User commentor;
    private Status toStatus;
    private String text;


    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public User getCommentor() {
        return commentor;
    }

    public void setCommentor(User commentor) {
        this.commentor = commentor;
    }

    public Status getToStatus() {
        return toStatus;
    }

    public void setToStatus(Status toStatus) {
        this.toStatus = toStatus;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
