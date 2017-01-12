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
import com.jinhanyu.jack.faceme.entity.FriendLikeItem;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;
import com.jinhanyu.jack.faceme.ui.SingleStatusActivity;

import java.util.List;

/**
 * Created by anzhuo on 2016/11/9.
 */

public class FavoriteItemAdapter extends CommonAdapter<FriendLikeItem> {


    public FavoriteItemAdapter(List<FriendLikeItem> data, Context context) {
        super(data, context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHold viewHold;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.favorite_list_item,null);
            viewHold=new ViewHold();
            viewHold.statusPhoto= (SimpleDraweeView) view.findViewById(R.id.sdv_favorite_item_statusPhoto);
            viewHold.username= (TextView) view.findViewById(R.id.tv_favorite_item_username);
            viewHold.userPortrait= (SimpleDraweeView) view.findViewById(R.id.sdv_favorite_item_userPortrait);
            view.setTag(viewHold);
        }else {
            viewHold= (ViewHold) view.getTag();
        }
        final FriendLikeItem item=data.get(position);

        viewHold.userPortrait.setImageURI(item.getFriend().getPortrait().getUrl());
        if(item.getStatus().getAuthor().getObjectId().equals(Utils.getCurrentUser().getObjectId())){
            viewHold.username.setText(item.getFriend().getUsername()+" 赞了你的");
        }else{
            viewHold.username.setText(item.getFriend().getUsername()+" 赞了“"+item.getStatus().getAuthor().getUsername()+"”的");
        }

        viewHold.statusPhoto.setImageURI(item.getStatus().getPhoto().getUrl());
        viewHold.statusPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SingleStatusActivity.class).
                        putExtra("statusId",item.getStatus().getObjectId()));
            }
        });

        return view;
    }

    class ViewHold{
        protected SimpleDraweeView userPortrait,statusPhoto;
        protected TextView username;
    }
}
