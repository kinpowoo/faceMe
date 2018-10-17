package com.jinhanyu.jack.faceme.entity;

import com.jinhanyu.jack.faceme.Utils;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by jianbo on 2016/10/19.
 */
public class Status extends BmobObject implements Comparable<Status>{
    private User author;
    private String text,locName;
    private BmobFile photo;
    private BmobRelation likes;
    private List<String> tags;
    private BmobGeoPoint location;
    private boolean favoritedByMe2;
    private Integer favoriteNum;
    private Integer commentNum;

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public Integer getFavoriteNum() {
        return favoriteNum;
    }

    public void setFavoriteNum(Integer favoriteNum) {
        this.favoriteNum = favoriteNum;
    }

    public boolean isFavoritedByMe2() {
        return favoritedByMe2;
    }

    public void setFavoritedByMe2(boolean favoritedByMe) {
        this.favoritedByMe2 = favoritedByMe;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
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

    public BmobFile getPhoto() {
        return photo;
    }

    public void setPhoto(BmobFile photo) {
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
