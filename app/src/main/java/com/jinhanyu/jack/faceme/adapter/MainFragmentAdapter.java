package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.Comment;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;
import com.jinhanyu.jack.faceme.ui.CommentActivity;
import com.jinhanyu.jack.faceme.ui.LikesActivity;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class MainFragmentAdapter extends CommonAdapter<Status> implements View.OnClickListener{
    User me;
    private Status status;
//    User me = BmobUser.getCurrentUser(User.class);

    public MainFragmentAdapter(List<Status> data, Context context) {
        super(data, context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        me=new User();
        me.setObjectId("XQm2333J");
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
        status = data.get(position);
        viewHolder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobQuery<Status> query = new BmobQuery<>();
                query.addWhereRelatedTo("likes", new BmobPointer(me));
                query.findObjects(new FindListener<Status>() {
                    @Override
                    public void done(List<Status> list, BmobException e) {
                        BmobRelation br = new BmobRelation();
                        if (list.contains(status)) {
                            br.remove(status);
                            me.setLikes(br);
                            me.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        viewHolder.favoriteIcon.setImageResource(R.drawable.favorite_light);
                                    }
                                }
                            });
                            br.setObjects(null);
                            br.remove(me);
                            status.setLikes(br);
                            status.update();
                        } else {
                            br.add(status);
                            me.setLikes(br);
                            me.update(me.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        viewHolder.favoriteIcon.setImageResource(R.drawable.favorite_dark);
                                    }

                                }
                            });
                            br.setObjects(null);
                            br.add(me);
                            status.setLikes(br);
                            status.update();
                        }
                    }
                });
            }
        });
        viewHolder.favoriteNum.setOnClickListener(this);
        BmobQuery<User> query=new BmobQuery<>();
        query.addWhereRelatedTo("likes",new BmobPointer(status));
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
            if(e==null&&list.size()>0){
                viewHolder.favoriteNum.setText(list.size()+" 个赞");
            }
            }
        });
        viewHolder.userPortrait.setImageURI(Uri.parse(status.getAuthor().getPortrait()));
        viewHolder.username.setText(status.getAuthor().getUsername());
        viewHolder.postPhoto.setImageURI(Uri.parse(status.getPhoto()));
        viewHolder.commentIcon.setOnClickListener(this);
        viewHolder.commentNum.setOnClickListener(this);
        viewHolder.favoriteNum.setOnClickListener(this);
        viewHolder.textBy.setText(status.getAuthor().getUsername());
        viewHolder.text.setText(status.getText());
        viewHolder.postTime.setText(Utils.calculTime(status.getCreatedAt()));
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_status_comment|R.id.tv_status_commentNum:
                Intent intent=new Intent(context, CommentActivity.class);
                intent.putExtra("statusId",status.getObjectId());
                intent.putExtra("authorId",status.getAuthor().getObjectId());
                context.startActivity(intent);
                break;
            case R.id.iv_status_share:
                break;
            case R.id.tv_status_favoriteNum:
                Intent intent2=new Intent(context, LikesActivity.class);
                intent2.putExtra("statusId",status.getObjectId());
                intent2.putExtra("authorId",status.getAuthor().getObjectId());
                context.startActivity(intent2);
                break;

        }
    }
}

class ViewHolder {
    protected SimpleDraweeView userPortrait,postPhoto;
    protected TextView username, favoriteNum, textBy, text, commentNum, postTime;
    protected ImageView favoriteIcon, commentIcon, shareIcon;

}
