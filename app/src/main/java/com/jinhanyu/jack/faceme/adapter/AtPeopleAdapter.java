package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.List;

/**
 * Created by anzhuo on 2016/11/7.
 */

public class AtPeopleAdapter extends CommonAdapter<User> {


    public AtPeopleAdapter(List<User> data, Context context) {
        super(data, context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHold viewHold;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.at_people_list_item,null);
            viewHold=new ViewHold();
            viewHold.portrait= (SimpleDraweeView) view.findViewById(R.id.sdv_at_people_item_portrait);
            viewHold.username= (TextView) view.findViewById(R.id.tv_at_people_item_username);
            viewHold.nickname= (TextView) view.findViewById(R.id.tv_at_people_item_nickname);
            view.setTag(viewHold);
        }else {
            viewHold= (ViewHold) view.getTag();
        }
        final User user=data.get(position);

        viewHold.portrait.setImageURI(user.getPortrait().getUrl());
        viewHold.username.setText(user.getUsername());
        viewHold.nickname.setText(user.getNickname());
        return view;
    }

    class ViewHold{
        protected SimpleDraweeView portrait;
        protected TextView username,nickname;
    }
}
