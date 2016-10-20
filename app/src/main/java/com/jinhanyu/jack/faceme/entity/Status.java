package com.jinhanyu.jack.faceme.entity;

import com.jinhanyu.jack.faceme.Utils;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by jianbo on 2016/10/19.
 */
public class Status extends BmobObject implements Comparable<Status>{
    private User author;
    private String text,photo;
    private BmobRelation likes;
    private BmobRelation comments;
    private Integer likesNum,commentsNum;

    public Integer getLikesNum() {
        return likesNum;
    }

    public void setLikesNum(Integer likesNum) {
        this.likesNum = likesNum;
    }

    public Integer getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(Integer commentsNum) {
        this.commentsNum = commentsNum;
    }

    public BmobRelation getComments() {
        return comments;
    }

    public void setComments(BmobRelation comments) {
        this.comments = comments;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    @Override
    public int compareTo(Status o) {
        return (int)(Utils.parseDate(this.getCreatedAt()).getTime()-Utils.parseDate(o.getCreatedAt()).getTime())/1000;
    }
}
