package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.ScreenUtils;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.Comment;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;
import com.jinhanyu.jack.faceme.ui.CommentActivity;
import com.jinhanyu.jack.faceme.ui.LikesActivity;
import com.jinhanyu.jack.faceme.ui.UserProfileActivity;

import java.text.ParseException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class MainFragmentAdapter extends CommonAdapter<Status> {
    User me = User.getCurrentUser(User.class);

    public MainFragmentAdapter(List<Status> data, Context context) {
        super(data, context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.main_fragment_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.userPortrait = (SimpleDraweeView) view.findViewById(R.id.sdv_status_userPortrait);
            viewHolder.username = (TextView) view.findViewById(R.id.tv_status_username);
            viewHolder.postPhoto = (SimpleDraweeView) view.findViewById(R.id.iv_status_photo);
            viewHolder.favoriteIcon = (ImageView) view.findViewById(R.id.iv_status_favorite);
            viewHolder.commentIcon = (ImageView) view.findViewById(R.id.iv_status_comment);
            viewHolder.shareIcon = (ImageView) view.findViewById(R.id.iv_status_share);
            viewHolder.favoriteNum = (TextView) view.findViewById(R.id.tv_status_favoriteNum);
            viewHolder.textBy = (TextView) view.findViewById(R.id.tv_status_textBy);
            viewHolder.text = (TextView) view.findViewById(R.id.tv_status_text);
            viewHolder.commentNum = (TextView) view.findViewById(R.id.tv_status_commentNum);
            viewHolder.postTime = (TextView) view.findViewById(R.id.tv_status_postTime);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final Status status = data.get(position);

        if (status.isFavoritedByMe()) {
            viewHolder.favoriteIcon.setImageResource(R.drawable.favorite_red);
        } else {
            viewHolder.favoriteIcon.setImageResource(R.drawable.favorite_light);
        }
        viewHolder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobRelation relation = new BmobRelation();
                viewHolder.favoriteIcon.setEnabled(false);
                if (status.isFavoritedByMe()) {
                    //取消收藏
                    relation.remove(me);
                    status.setLikes(relation);
                    status.setFavoritedByMe(false);
                    status.setFavoriteNum(status.getFavoriteNum()-1);
                    status.update(status.getObjectId(),new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            viewHolder.favoriteIcon.setImageResource(R.drawable.favorite_light);
                            viewHolder.favoriteIcon.setEnabled(true);
                            viewHolder.favoriteNum.setText(status.getFavoriteNum()+"个赞");
                        }
                    });
                } else {
                    //添加收藏
                    relation.add(me);
                    status.setLikes(relation);
                    status.setFavoritedByMe(true);
                    status.setFavoriteNum(status.getFavoriteNum()+1);
                    status.update(status.getObjectId(),new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            viewHolder.favoriteIcon.setImageResource(R.drawable.favorite_red);
                            viewHolder.favoriteIcon.setEnabled(true);
                            viewHolder.favoriteNum.setText(status.getFavoriteNum()+"个赞");
                        }
                    });
                }
            }
        });

            BmobQuery<User> userBmobQuery = new BmobQuery<>();
            userBmobQuery.addWhereRelatedTo("likes", new BmobPointer(status));
            userBmobQuery.count(User.class, new CountListener() {
                @Override
                public void done(Integer num, BmobException e) {
                    Log.i("favoriteNum", num + "");
                    status.setFavoriteNum(num);
                    viewHolder.favoriteNum.setText(num + " 个赞");
                }
            });


            BmobQuery<Comment> commentBmobQuery = new BmobQuery<>();
            commentBmobQuery.addWhereEqualTo("toStatus", new BmobPointer(status));
            commentBmobQuery.count(Comment.class, new CountListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    status.setCommentNum(integer);
                    viewHolder.commentNum.setText(integer + " 条评论");
                }
            });


        viewHolder.postPhoto.setMaxWidth(ScreenUtils.getScreenWidth(context));
        viewHolder.postPhoto.setMinimumWidth(ScreenUtils.getScreenWidth(context));
        viewHolder.userPortrait.setImageURI(status.getAuthor().getPortrait().getUrl());
        viewHolder.username.setText(status.getAuthor().getUsername());
        viewHolder.postPhoto.setImageURI(status.getPhoto().getUrl());
        viewHolder.textBy.setText(status.getAuthor().getUsername()+": ");
        viewHolder.text.setText(status.getText());
        try {
            viewHolder.postTime.setText(Utils.calculTime(status.getCreatedAt()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.commentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("statusId", status.getObjectId());
                context.startActivity(intent);
            }
        });
        viewHolder.commentNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("statusId", status.getObjectId());
                context.startActivity(intent);
            }
        });
        viewHolder.favoriteNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(context, LikesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("type", "status");
                bundle.putString("statusId", status.getObjectId());
                intent2.putExtras(bundle);
                context.startActivity(intent2);
            }
        });
        viewHolder.shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, UserProfileActivity.class);
                intent1.putExtra("userId", status.getAuthor().getObjectId());
                context.startActivity(intent1);
            }
        });
        viewHolder.userPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, UserProfileActivity.class);
                intent1.putExtra("userId", status.getAuthor().getObjectId());
                context.startActivity(intent1);
            }
        });
        return view;
    }
    class ViewHolder {
        protected SimpleDraweeView userPortrait, postPhoto;
        protected TextView username, favoriteNum, textBy, text, commentNum, postTime;
        protected ImageView favoriteIcon, commentIcon, shareIcon;

    }

}


