package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.entity.DyqItem;

import java.util.List;

/**
 * Created by anzhuo on 2016/10/27.陈礼
 */
public class DyqAdapter extends CommonAdapter<DyqItem> {
    public DyqAdapter(List<DyqItem> data, Context context) {
        super(data, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.common_item, null);
            viewHolder.head = (ImageView) convertView.findViewById(R.id.head);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.intro = (TextView) convertView.findViewById(R.id.intro);
            viewHolder.focus = (TextView) convertView.findViewById(R.id.focus);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        DyqItem dyq_item = data.get(position);
        viewHolder.head.setImageURI(Uri.parse(dyq_item.getHead()));
        viewHolder.name.setText(dyq_item.getName());
        viewHolder.intro.setText(dyq_item.getIntro());
        viewHolder.focus.setText(dyq_item.getFocus());
        return convertView;
    }

    class ViewHolder{
        ImageView head;
        TextView name,intro,focus;
    }
}
