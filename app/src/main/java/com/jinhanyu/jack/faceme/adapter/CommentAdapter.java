package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.Comment;
import com.jinhanyu.jack.faceme.ui.UserProfileActivity;

import java.text.ParseException;
import java.util.List;

/**
 * Created by jianbo on 2016/10/20.
 */
public class CommentAdapter extends CommonAdapter<Comment>{

    public CommentAdapter(List<Comment> data, Context context) {
        super(data, context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolderForComment viewHold;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.comment_list_item, null);
            viewHold = new ViewHolderForComment();
            viewHold.userPortrait = (SimpleDraweeView) view.findViewById(R.id.sdv_comment_item_userPortrait);
            viewHold.username = (TextView) view.findViewById(R.id.tv_comment_item_username);
            viewHold.commentContent = (TextView) view.findViewById(R.id.tv_comment_item_content);
            viewHold.postTime = (TextView) view.findViewById(R.id.tv_comment_item_postTime);
            view.setTag(viewHold);
        } else {
            viewHold = (ViewHolderForComment) view.getTag();
        }
       final Comment comment = data.get(position);
        viewHold.userPortrait.setImageURI(comment.getCommentor().getPortrait().getUrl());
        viewHold.username.setText(comment.getCommentor().getUsername());
        try {
            viewHold.postTime.setText(Utils.calculTime(comment.getCreatedAt()));
        } catch (ParseException e) {e.printStackTrace();

        }
        viewHold.commentContent.setText(comment.getText());


        viewHold.userPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,UserProfileActivity.class);
                intent.putExtra("userId", comment.getCommentor().getObjectId());
                context.startActivity(intent);
            }
        });
        viewHold.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,UserProfileActivity.class);
                intent.putExtra("userId", comment.getCommentor().getObjectId());
                context.startActivity(intent);
            }
        });
        viewHold.commentContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,UserProfileActivity.class);
                intent.putExtra("userId", comment.getCommentor().getObjectId());
                context.startActivity(intent);
            }
        });
        return view;
    }


    class ViewHolderForComment {
        protected SimpleDraweeView userPortrait;
        protected TextView username, commentContent, postTime;

    }
}

