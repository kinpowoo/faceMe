package com.jinhanyu.jack.faceme;

import android.util.Log;

import com.jinhanyu.jack.faceme.entity.Comment;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by anzhuo on 2016/10/31.
 */

public class AllQueries {


    public static void login() {
//        Log.i("currentUser",User.getCurrentUser(User.class).getGender());
        User.loginByAccount("chenli", "12345", new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                Log.i("user", "...");
                Log.i("user", user.getGender());
            }
        });
    }

    public static void getFavoriteNum(Status status){
        BmobQuery<User> userBmobQuery = new BmobQuery<>();
        userBmobQuery.addWhereRelatedTo("likes", new BmobPointer(status));
        userBmobQuery.count(User.class, new CountListener() {
            @Override
            public void done(Integer num, BmobException e) {
                Log.i("favoriteNum",num+"");

            }
        });

    }

    public static void followUser() {
        User user =new User();
        user.setObjectId("XQm2333J");
        User loginUser = User.getCurrentUser(User.class);
        //user.setObjectId("8cB5MMMR");

        BmobRelation followings = new BmobRelation();
        followings.add(user);
        loginUser.setFollowing(followings);
        loginUser.update(loginUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("bmob", "关注用户成功");

                } else {
                    Log.i("bmob", "失败：" + e.getMessage());
                }
            }
        });


    }

    public static void likeAstatus() {
        Status status = new Status();
        status.setObjectId("7BKT999k");
        BmobRelation likes = new BmobRelation();
        User user = new User();
        user.setObjectId("XQm2333J");
        likes.add(user);
        status.setLikes(likes);
        status.update("7BKT999k", new UpdateListener() {
            @Override
            public void done(BmobException e) {

            }
        });
    }

    /**
     * 得到用户的收藏列表(Status)
     *
     * @param userId 用户Id
     */
    public static void getFavorites(String userId) {

        BmobQuery<Status> statusQuery = new BmobQuery<>();
        BmobQuery<User> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId", userId);
        statusQuery.addWhereMatchesQuery("likes", "_User", innerQuery);
        statusQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> list, BmobException e) {
                Log.i("results", list.size() + "");
            }
        });


    }

    /**
     * 得到用户的粉丝列表
     *
     */
    public static void getFollowerList(String userId){
        BmobQuery<User> followerQuery=new BmobQuery<>();
        BmobQuery<User> innerQuery=new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId",userId);
        followerQuery.addWhereMatchesQuery("following","_User",innerQuery);
        followerQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
               Log.i("follower",list.get(0).getUsername());
            }
        });


    }


    /**
     * 得到当前用户的朋友圈动态
     */
    public static void getFriendCircleStatuses() {

        //子查询(主查询)
        BmobQuery<User> innerQuery = new BmobQuery<>();
        //子查询之一： 查询朋友
        BmobQuery<User> followingQuery = new BmobQuery<>();
        followingQuery.addWhereRelatedTo("following", new BmobPointer(User.getCurrentUser(User.class)));
        //子查询之二： 查询自己
        BmobQuery<User> selfQuery = new BmobQuery<>();
        selfQuery.addWhereEqualTo("objectId", User.getCurrentUser(User.class).getObjectId());
        //合并子查询
        List<BmobQuery<User>> addonQueries = new ArrayList<>();
        addonQueries.add(selfQuery);
        addonQueries.add(followingQuery);
        //添加到主查询中
        innerQuery.or(addonQueries);

        //最终查询
        BmobQuery<Status> statusQuery = new BmobQuery<>();
        statusQuery.addWhereMatchesQuery("author", "_User", innerQuery);
        statusQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> list, BmobException e) {
                Log.i("results", list.size() + "");
                for (Status status : list) {
                    Log.i("text", status.getText());
                    Log.i("tags", status.getTags().toString());
                    Log.i("photo", status.getPhoto().getUrl());
                }
            }
        });
    }

    /**
     * 根据微博拿评论
     * @param status
     */
    public static void getComments(Status status) {
        BmobQuery<Comment> commentBmobQuery = new BmobQuery<>();
        commentBmobQuery.addWhereEqualTo("toStatus", new BmobPointer(status));
        commentBmobQuery.include("commentor,replyToUser");
        commentBmobQuery.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                Log.i("size",list.size()+"");
                for (Comment comment: list){
                    String replyToUser = comment.getReplyToUser()==null? "" : comment.getReplyToUser().getUsername();
                    Log.i("comment",comment.getText()+"...."
                           +comment.getCommentor().getUsername()+"..."
                            +"replyTo: "+ replyToUser
                         );
                }
            }
        });

    }

    /**
     * 根据用户拿微博
     * @param user
     */
    public static void getStatuses(User user){
        BmobQuery<Status> statusBmobQuery = new BmobQuery<>();
        statusBmobQuery.addWhereEqualTo("author",new BmobPointer(user));
        statusBmobQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> list, BmobException e) {
                for (Status status : list) {
                    Log.i("text", status.getText());
                    Log.i("tags", status.getTags().toString());
                    Log.i("photo", status.getPhoto().getUrl());
                }
            }
        });
    }

    public static void getStatusesByTag(String tag){

        BmobQuery<Status> tagQuery = new BmobQuery<>();
        List<String> tags = new ArrayList<>();
        tags.add(tag);
        tagQuery.addWhereContainsAll("tags", tags);
        tagQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> list, BmobException e) {
                for (Status status : list) {
                    Log.i("text", status.getText());
                    Log.i("tags", status.getTags().toString());
                    Log.i("photo", status.getPhoto().getUrl());
                }
            }
        });
    }


}