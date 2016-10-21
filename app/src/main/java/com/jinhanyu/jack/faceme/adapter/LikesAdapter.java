package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by jianbo on 2016/10/20.
 */
public class LikesAdapter extends CommonAdapter<User> implements View.OnClickListener {
    private User user;
    private Boolean isFollowing = false;
    private List<User> queryList;

    public LikesAdapter(List<User> data, Context context) {
        super(data, context);
    }
    public void refreshDataSource(List<User> newData){
        data=newData;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
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
        user = data.get(position);
        viewHold.username.setText(user.getUsername());
        viewHold.nickname.setText(user.getNickname());
        viewHold.userPortrait.setImageURI(Uri.parse(user.getPortrait()));

        viewHold.userPortrait.setOnClickListener(this);
        viewHold.username.setOnClickListener(this);
        viewHold.nickname.setOnClickListener(this);

        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("following", new BmobPointer(Utils.getCurrentUser()));
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                queryList = list;
                if (list.contains(user)) {
                    isFollowing = true;
                }
            }
        });
        viewHold.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobRelation relation = new BmobRelation();
                if (queryList.contains(user)) {
                    relation.remove(user);
                    Utils.getCurrentUser().setFollowing(relation);
                    Utils.getCurrentUser().increment("followingNum");
                    Utils.getCurrentUser().update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                viewHold.follow.setText("关注");
                                isFollowing = false;
                            }
                        }
                    });
                    relation.setObjects(null);
                    relation.remove(Utils.getCurrentUser());
                    user.setFollowers(relation);
                    user.increment("followersNum",-1);
                    user.update();

                } else {
                    relation.add(user);
                    Utils.getCurrentUser().setFollowers(relation);
                    Utils.getCurrentUser().increment("followingNum", -1);
                    Utils.getCurrentUser().update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                viewHold.follow.setText("取消关注");
                                isFollowing = true;
                            }
                        }
                    });

                    relation.setObjects(null);
                    relation.add(Utils.getCurrentUser());
                    user.increment("followersNum");
                    user.setFollowers(relation);
                    user.update();
                }
            }
        });
        if (isFollowing) {
            viewHold.follow.setText("取消关注");
        } else {
            viewHold.follow.setText("关注");
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sdv_likes_item_userPortrait
                    | R.id.tv_likes_item_username
                    | R.id.tv_likes_item_nickname:
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("userId",user.getObjectId());
                context.startActivity(intent);
                break;
        }
    }
}

class ViewHolder3 {
    protected SimpleDraweeView userPortrait;
    protected TextView username, nickname;
    protected Button follow;
}