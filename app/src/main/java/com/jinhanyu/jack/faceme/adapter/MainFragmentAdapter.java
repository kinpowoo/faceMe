package com.jinhanyu.jack.faceme.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.FaceMePopupMenu;
import com.jinhanyu.jack.faceme.MainApplication;
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
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
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
    LinearLayout.LayoutParams params;
    Activity activity;
    boolean isSelfIn;

    public MainFragmentAdapter(List<Status> data, Context context, Activity activity) {
        super(data, context);
        int width = ScreenUtils.getScreenWidth(context);
        params = new LinearLayout.LayoutParams(width, width);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View view, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.main_fragment_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.userPortrait = (SimpleDraweeView) view.findViewById(R.id.sdv_status_userPortrait);
            viewHolder.nickname = (TextView) view.findViewById(R.id.tv_status_nickname);
            viewHolder.postPhoto = (SimpleDraweeView) view.findViewById(R.id.iv_status_photo);
            viewHolder.favoriteIcon = (ImageView) view.findViewById(R.id.iv_status_favorite);
            viewHolder.commentIcon = (ImageView) view.findViewById(R.id.iv_status_comment);
            viewHolder.shareIcon = (ImageView) view.findViewById(R.id.iv_status_share);
            viewHolder.favoriteNum = (TextView) view.findViewById(R.id.tv_status_favoriteNum);
            viewHolder.text = (TextView) view.findViewById(R.id.tv_status_text);
            viewHolder.commentNum = (TextView) view.findViewById(R.id.tv_status_commentNum);
            viewHolder.postTime = (TextView) view.findViewById(R.id.tv_status_postTime);
            viewHolder.tag_one = (TextView) view.findViewById(R.id.tag_one);
            viewHolder.tag_two = (TextView) view.findViewById(R.id.tag_two);
            viewHolder.tag_three = (TextView) view.findViewById(R.id.tag_three);
            viewHolder.location = (TextView) view.findViewById(R.id.tv_location);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final Status status = data.get(position);


        if (!status.isFavoritedByMe2()) {
            viewHolder.favoriteIcon.setImageResource(R.drawable.favorite_light);
        } else {
            viewHolder.favoriteIcon.setImageResource(R.drawable.favorite_red);
        }


        viewHolder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.favoriteIcon.setEnabled(false);

                BmobRelation relation1 = new BmobRelation();
                if(status.isFavoritedByMe2()) {
                    relation1.remove(me);
                    status.setFavoritedByMe2(false);
                    status.increment("favoriteNum",-1);
                    viewHolder.favoriteIcon.setImageResource(R.drawable.favorite_light);
                    status.setFavoriteNum(status.getFavoriteNum()-1);
                }else {
                    relation1.add(me);
                    status.setFavoritedByMe2(true);
                    status.increment("favoriteNum");
                    viewHolder.favoriteIcon.setImageResource(R.drawable.favorite_red);
                    status.setFavoriteNum(status.getFavoriteNum()+1);
                }

                status.setLikes(relation1);
                status.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e == null) {
                            viewHolder.favoriteIcon.setEnabled(true);
                            viewHolder.favoriteNum.setText(status.getFavoriteNum() + "个赞");
                        }
                    }
                });

            }
        });

        if(status.getFavoriteNum() == null){
            status.setFavoriteNum(0);
        }
        viewHolder.favoriteNum.setText(status.getFavoriteNum() + " 个赞");

        BmobQuery<Comment> commentBmobQuery = new BmobQuery<>();
        commentBmobQuery.addWhereEqualTo("toStatus", new BmobPointer(status));
        commentBmobQuery.count(Comment.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                status.setCommentNum(integer);
                viewHolder.commentNum.setText(integer + " 条评论");
            }
        });


        viewHolder.postPhoto.setLayoutParams(params);
        viewHolder.postPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        viewHolder.userPortrait.setImageURI(status.getAuthor().getPortrait().getUrl());
        viewHolder.nickname.setText(status.getAuthor().getNickname());
        viewHolder.postPhoto.setImageURI(status.getPhoto().getUrl());
        viewHolder.text.setText(status.getText());
        if (status.getLocName() != null) {
            viewHolder.location.setText(status.getLocName());
        } else {
            viewHolder.location.setVisibility(View.GONE);
        }

        if (status.getTags() != null) {
            int tag_count = status.getTags().size();
            if (tag_count >0) {
                viewHolder.tag_one.setVisibility(View.VISIBLE);
                viewHolder.tag_one.setText(status.getTags().get(0));
            }
            if (tag_count >1) {
                viewHolder.tag_two.setVisibility(View.VISIBLE);
                viewHolder.tag_two.setText(status.getTags().get(1));
            }
            if (tag_count >2) {
                viewHolder.tag_three.setVisibility(View.VISIBLE);
                viewHolder.tag_three.setText(status.getTags().get(2));
            }
        }else {
            viewHolder.tag_one.setVisibility(View.VISIBLE);
            viewHolder.tag_one.setText("无");
            viewHolder.tag_two.setVisibility(View.INVISIBLE);
            viewHolder.tag_three.setVisibility(View.INVISIBLE);
        }


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
                MainApplication.showShareRemote(context, status.getPhoto().getUrl(), status.getText());
            }
        });
        viewHolder.nickname.setOnClickListener(new View.OnClickListener() {
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

        final View finalView = view;
        viewHolder.postPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FaceMePopupMenu popupMenu = new FaceMePopupMenu(activity);
                popupMenu.setOnConfirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.downPic(status.getPhoto().getUrl(), null);
                        Toast.makeText(context, "图片已保存到本地", Toast.LENGTH_SHORT).show();
                    }
                });
                popupMenu.show(finalView);
                return false;
            }
        });

        return view;
    }

    class ViewHolder {
        protected SimpleDraweeView userPortrait, postPhoto;
        protected TextView nickname, favoriteNum, text, commentNum,
                postTime, tag_one, tag_two, tag_three, location;
        protected ImageView favoriteIcon, commentIcon, shareIcon;

    }

}


