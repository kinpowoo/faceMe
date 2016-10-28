package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.R;

import java.util.List;

/**
 * Created by anzhuo on 2016/10/28.
 */
public class TagAdapter extends CommonAdapter<String> {

    public TagAdapter(List<String> data, Context context) {
        super(data, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.search, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String tag = data.get(position);
        viewHolder.title.setText(tag);
        return convertView;
    }
    class ViewHolder{
        TextView title;
    }
}

