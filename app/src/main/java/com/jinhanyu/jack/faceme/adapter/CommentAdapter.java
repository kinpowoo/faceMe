package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.Comment;
import com.jinhanyu.jack.faceme.ui.UserProfileActivity;

import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by jianbo on 2016/10/20.
 */
public class CommentAdapter extends BaseSwipeAdapter{
    private Context context;
    private List<Comment> data;

    public CommentAdapter(Context context,List<Comment> list){
        this.context=context;
        this.data=list;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.comment_list_item, null);


    }

    @Override
    public void fillValues(final int position, final View view) {
        final ViewHolderForComment viewHold;
            viewHold = new ViewHolderForComment();
            viewHold.userPortrait = (SimpleDraweeView) view.findViewById(R.id.sdv_comment_item_userPortrait);
            viewHold.username = (TextView) view.findViewById(R.id.tv_comment_item_username);
            viewHold.commentContent = (TextView) view.findViewById(R.id.tv_comment_item_content);
            viewHold.postTime = (TextView) view.findViewById(R.id.tv_comment_item_postTime);
            viewHold.delete= (ImageView) view.findViewById(R.id.iv_comment_item_delete);
            viewHold.topLayer= (RelativeLayout) view.findViewById(R.id.rl_comment_item_topLayer);
            viewHold.atPeople= (ImageView) view.findViewById(R.id.iv_comment_item_atPeople);

        final Comment comment = data.get(position);


        viewHold.userPortrait.setImageURI(comment.getCommentor().getPortrait().getUrl());
        viewHold.username.setText(comment.getCommentor().getNickname());
        try {
            viewHold.postTime.setText(Utils.calculTime(comment.getCreatedAt()));
        } catch (ParseException e) {e.printStackTrace();

        }
        if(comment.getText().charAt(0)=='@'){
            Utils.setTVColor(comment.getText(),0,comment.getReplyToUser().getNickname().length()+1,
                    context.getResources().getColor(R.color.BlueViolet),viewHold.commentContent);
        }else {
            viewHold.commentContent.setText(comment.getText());
        }

        viewHold.atPeople.setTag("repost");
        if(comment.getCommentor().getNickname().equals(Utils.getCurrentUser().getNickname())) {
            viewHold.atPeople.setVisibility(View.GONE);
            viewHold.delete.setVisibility(View.VISIBLE);
        }else {
            viewHold.delete.setVisibility(View.GONE);
            viewHold.atPeople.setVisibility(View.VISIBLE);
        }

        viewHold.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHold.delete.setVisibility(View.GONE);  //点击删除按钮后，影藏按钮
                deleteItem(position);   //删除数据，加动画
            }
        });


        viewHold.userPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("userId", comment.getCommentor().getObjectId());
                    context.startActivity(intent);

            }
        });
        viewHold.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("userId", comment.getCommentor().getObjectId());
                    context.startActivity(intent);

            }
        });
        viewHold.commentContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String text=comment.getText();
                   Pattern pattern = Pattern.compile("@.+?\0\\s");
                   Matcher matcher = pattern.matcher(text);
                   if(matcher.find()){
                        context.startActivity(new Intent(context,UserProfileActivity.class)
                                .putExtra("userId",comment.getReplyToUser().getObjectId()));
                   }else {
                       Intent intent = new Intent(context, UserProfileActivity.class);
                       intent.putExtra("userId", comment.getCommentor().getObjectId());
                       context.startActivity(intent);
                   }

            }
        });


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


    public void deleteItem(final int position){

                Comment comment=new Comment();
                comment.delete(data.get(position).getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        data.remove(position);  //把数据源里面相应数据删除
                        notifyDataSetChanged();
                    }
                });


    }


    class ViewHolderForComment {
        protected SimpleDraweeView userPortrait;
        protected TextView username, commentContent, postTime;
        protected ImageView delete,atPeople;
        protected RelativeLayout topLayer;
    }
}

