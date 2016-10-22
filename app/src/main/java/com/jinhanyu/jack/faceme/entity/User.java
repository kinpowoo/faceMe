package com.jinhanyu.jack.faceme.entity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class User extends BmobUser {
     private String portrait;
    private String nickname;
     private BmobRelation likes;
    private BmobRelation following;
    private BmobRelation followers;
    private BmobRelation statuses;
    private Integer likesNum;
    private Integer followingNum;
    private Integer followersNum;
    private Integer statusesNum;
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getStatusesNum() {
        return statusesNum;
    }

    public void setStatusesNum(Integer statusesNum) {
        this.statusesNum = statusesNum;
    }
    public Integer getLikesNum() {
        return likesNum;
    }

    public void setLikesNum(Integer likesNum) {
        this.likesNum = likesNum;
    }

    public Integer getFollowingNum() {
        return followingNum;
    }

    public void setFollowingNum(Integer followingNum) {
        this.followingNum = followingNum;
    }

    public Integer getFollowersNum() {
        return followersNum;
    }

    public void setFollowersNum(Integer followersNum) {
        this.followersNum = followersNum;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public BmobRelation getStatuses() {
        return statuses;
    }

    public void setStatuses(BmobRelation statuses) {
        this.statuses = statuses;
    }


    public BmobRelation getFollowers() {
        return followers;
    }

    public void setFollowers(BmobRelation followers) {
        this.followers = followers;
    }

    public BmobRelation getFollowing() {
        return following;
    }

    public void setFollowing(BmobRelation following) {
        this.following = following;
    }


    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
