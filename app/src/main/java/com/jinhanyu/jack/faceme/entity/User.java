package com.jinhanyu.jack.faceme.entity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class User extends BmobUser {
    private BmobFile portrait;
    private String nickname;
    private BmobRelation following;
    private String gender;
    private Integer followingNum;
    private Integer followerNum;
    private Integer statusNum;
    private boolean isFollowing;

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public Integer getStatusNum() {
        return statusNum;
    }

    public void setStatusNum(Integer statusNum) {
        this.statusNum = statusNum;
    }

    public Integer getFollowerNum() {
        return followerNum;
    }

    public void setFollowerNum(Integer followerNum) {
        this.followerNum = followerNum;
    }

    public Integer getFollowingNum() {
        return followingNum;
    }

    public void setFollowingNum(Integer followingNum) {
        this.followingNum = followingNum;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }



    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }




    public BmobRelation getFollowing() {
        return following;
    }

    public void setFollowing(BmobRelation following) {
        this.following = following;
    }


    public BmobFile getPortrait() {
        return portrait;
    }

    public void setPortrait(BmobFile portrait) {
        this.portrait = portrait;
    }
}
