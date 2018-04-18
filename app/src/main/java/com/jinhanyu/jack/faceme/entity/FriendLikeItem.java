package com.jinhanyu.jack.faceme.entity;

/**
 * Created by anzhuo on 2016/11/9.
 */

public class FriendLikeItem implements Comparable<FriendLikeItem>{

    private User friend;
    private Status status;

    public FriendLikeItem(User friend, Status status) {
        this.friend = friend;
        this.status = status;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return friend.getNickname() + "  liked  " + status.getText();
    }

    @Override
    public int compareTo(FriendLikeItem o) {
        return status.getCreatedAt().compareTo(o.status.getCreatedAt());
    }
}
