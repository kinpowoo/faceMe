package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.entity.Status;

import java.util.List;

/**
 * Created by anzhuo on 2016/11/9.
 */

public class FavoriteItemAdapter extends CommonAdapter<Status> {


    public FavoriteItemAdapter(List<Status> data, Context context) {
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
        final Status status=data.get(position);

        return view;
    }

    class ViewHold{
        protected SimpleDraweeView userPortrait,statusPhoto;
        protected TextView username;
    }
}
