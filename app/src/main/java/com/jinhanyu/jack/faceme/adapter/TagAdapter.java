package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.ui.CustomTag;

import java.util.List;

/**
 * Created by anzhuo on 2016/10/28.
 */
public class TagAdapter extends CommonAdapter<String> {

    public TagAdapter(List<String> data, Context context) {
        super(data, context);
    }

    boolean isEditing = false;

    public void enterEditMode(){
        isEditing = true;
        notifyDataSetChanged();
    }

    public void exitEditMode(){
        isEditing = false;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.search, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.delete = convertView.findViewById(R.id.delete);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String tag = data.get(position);
        viewHolder.title.setText(tag);

        if(isEditing){
            viewHolder.delete.setVisibility(View.VISIBLE);
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CustomTag)context).removeTag(tag);
                }
            });
        }else{
            viewHolder.delete.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
    class ViewHolder{
        TextView title;
        View delete;
    }
}

