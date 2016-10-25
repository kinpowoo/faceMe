package com.jinhanyu.jack.faceme.entity;

import com.jinhanyu.jack.faceme.Utils;

import cn.bmob.v3.BmobObject;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class Comment extends BmobObject implements Comparable<Comment>{
 private User toUser;
    private User commentor;
    private Status toStatus;
    private String text;
    private String serch;


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

    public String getSerch() {
        return serch;
    }

    public void setSerch(String serch) {
        this.serch = serch;
    }

    @Override
    public int compareTo(Comment o) {
        return (int)(Utils.parseDate(this.getCreatedAt()).getTime()-Utils.parseDate(o.getCreatedAt()).getTime())/1000;
    }
}
