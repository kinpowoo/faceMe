package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.User;
import com.jinhanyu.jack.faceme.ui.UserProfileActivity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by jianbo on 2016/10/20.
 */
public class LikesAdapter extends BaseAdapter{
    private List<User> data;
    private Context context;
    private User currentUser=Utils.getCurrentUser();
    private boolean following=false;
    private String currentUserId=Utils.getCurrentUser().getObjectId();
    public LikesAdapter(List<User> data, Context context) {
       this.data=data;
        this.context=context;
    }

    public void refreshDataSource(List<User> newData){
        data=newData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data==null?0:data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder3 viewHold;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.likes_list_item, null);
            viewHold = new ViewHolder3();
            viewHold.userPortrait = (SimpleDraweeView) view.findViewById(R.id.sdv_likes_item_userPortrait);
            viewHold.username = (TextView) view.findViewById(R.id.tv_likes_item_username);
            viewHold.nickname = (TextView) view.findViewById(R.id.tv_likes_item_nickname);
            viewHold.follow = (Button) view.findViewById(R.id.btn_likes_item_follow);
            view.setTag(viewHold);
        } else {
            viewHold = (ViewHolder3) view.getTag();
        }
        final User user = data.get(position);
        viewHold.username.setText(user.getUsername());
        viewHold.nickname.setText(user.getNickname());
        viewHold.userPortrait.setImageURI(user.getPortrait().getUrl());

        viewHold.userPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("userId",user.getObjectId());
                context.startActivity(intent);
            }
        });
        viewHold.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("userId",user.getObjectId());
                context.startActivity(intent);
            }
        });
        viewHold.nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("userId",user.getObjectId());
                context.startActivity(intent);
            }
        });



        BmobQuery<User> followerQuery=new BmobQuery<>();
        BmobQuery<User> innerQuery=new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId",user.getObjectId());
        followerQuery.addWhereMatchesQuery("following","_User",innerQuery);
        followerQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                for(User user1:list){
                    Log.i("haha",list.get(position).getUsername());

                    if(user1.getObjectId().equals(currentUserId)){
                        following=true;
                        user.setFollowing(true);
                        viewHold.follow.setText("取消关注");
                    }
                }
                if(following==false) {

                    if(user.getObjectId().equals(currentUserId)){
                        viewHold.follow.setText("不可选");
                        viewHold.follow.setEnabled(false);
                    }else {
                        viewHold.follow.setText("关注");
                    }
                }
            }
        });


            viewHold.follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHold.follow.setEnabled(false);
                    if (following) {
                        BmobRelation relation = new BmobRelation();
                        relation.remove(user);
                        currentUser.setFollowing(relation);
                        currentUser.setFollowingNum(currentUser.getFollowingNum() - 1);
                        currentUser.update(currentUserId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                    viewHold.follow.setText("关注");
                                    user.setFollowing(false);
                                    following = false;
                                    viewHold.follow.setEnabled(true);
                            }
                        });
                    } else {
                        BmobRelation relation = new BmobRelation();
                        relation.add(user);
                        currentUser.setFollowing(relation);
                        currentUser.setFollowingNum(currentUser.getFollowingNum() + 1);
                        currentUser.update(currentUserId, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                    viewHold.follow.setText("取消关注");
                                    user.setFollowing(true);
                                    following = true;
                                    viewHold.follow.setEnabled(true);
                            }
                        });
                    }
                }
            });



        return view;
    }


    class ViewHolder3 {
        protected SimpleDraweeView userPortrait;
        protected TextView username, nickname;
        protected Button follow;
    }
}
