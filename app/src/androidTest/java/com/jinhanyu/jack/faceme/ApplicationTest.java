package com.jinhanyu.jack.faceme;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testLogin(){
        AllQueries.login();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testFollow(){
        AllQueries.followUser();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void testDongtai(){
        AllQueries.getFriendCircleStatuses();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testLikeStatus(){
        AllQueries.likeAstatus();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testFavorate(){
        AllQueries.getFavorites("8cB5MMMR");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testCommentList(){
        Status status = new Status();
        status.setObjectId("7BKT999k");
        AllQueries.getComments(status);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void testStatusList(){
        AllQueries.getStatuses(User.getCurrentUser(User.class));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testGetStatusesByTag(){
        AllQueries.getStatusesByTag("小电影");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void testGetFollower(){
        AllQueries.getFollowerList("XQm2333J");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
