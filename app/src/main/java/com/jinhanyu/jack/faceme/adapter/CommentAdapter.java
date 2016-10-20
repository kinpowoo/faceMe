package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.Comment;
import com.jinhanyu.jack.faceme.ui.UserFragment;

import java.util.List;

/**
 * Created by jianbo on 2016/10/20.
 */
public class CommentAdapter extends CommonAdapter<Comment> implements View.OnClickListener {
    private Comment comment;

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
        comment = data.get(position);
        viewHold.userPortrait.setImageURI(Uri.parse(comment.getCommentor().getPortrait()));
        viewHold.username.setText(comment.getCommentor().getUsername());
        viewHold.postTime.setText(Utils.calculTime(comment.getCreatedAt()));
        viewHold.commentContent.setText(comment.getText());

        viewHold.userPortrait.setOnClickListener(this);
        viewHold.username.setOnClickListener(this);
        viewHold.commentContent.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sdv_comment_item_userPortrait
                    | R.id.tv_comment_item_username
                    | R.id.tv_comment_item_content:
                Intent intent = new Intent(context, UserFragment.class);
                intent.putExtra("commentorId", comment.getCommentor().getObjectId());
                context.startActivity(intent);
                break;
        }
    }
}

class ViewHolderForComment {
    protected SimpleDraweeView userPortrait;
    protected TextView username, commentContent, postTime;

}