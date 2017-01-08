package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.entity.SingleFavoriteItem;

import java.util.List;

/**
 * Created by anzhuo on 2016/10/18.陈礼
 */
public class FlowFragmentAdapter extends CommonAdapter<SingleFavoriteItem> {

    public FlowFragmentAdapter(List<SingleFavoriteItem> data, Context context) {
        super(data, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         ViewHolder viewHolder;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.flow_item, null);
            viewHolder.picture = (SimpleDraweeView) convertView.findViewById(R.id.picture);
            viewHolder.picture_test = (TextView) convertView.findViewById(R.id.picture_text);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        SingleFavoriteItem singleFavoriteItem = data.get(position);
        viewHolder.picture_test.setText(singleFavoriteItem.getPicture_text());
        viewHolder.picture.setImageURI(singleFavoriteItem.getPicture());
        return convertView;
    }

    class ViewHolder{
        SimpleDraweeView picture;
        TextView picture_test;
    }

}
