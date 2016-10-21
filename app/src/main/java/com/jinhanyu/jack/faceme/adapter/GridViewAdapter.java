package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.ui.SingleStatusActivity;

import java.util.List;

/**
 * Created by jianbo on 2016/10/21.
 */
public class GridViewAdapter extends CommonAdapter<Status>{
    private Status status;

    public GridViewAdapter(List<Status> data, Context context) {
        super(data, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder4 viewHold;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.user_fragment_gridview_item,null);
            viewHold=new ViewHolder4();
            viewHold.statusPhoto= (SimpleDraweeView) convertView.findViewById(R.id.sdv_userFragment_gridview_item);
            convertView.setTag(viewHold);
        }else {
            viewHold= (ViewHolder4) convertView.getTag();
        }
        status=data.get(position);
        viewHold.statusPhoto.setImageURI(Uri.parse(status.getPhoto()));
        viewHold.statusPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, SingleStatusActivity.class);
                intent.putExtra("statusId",status.getObjectId());
                context.startActivity(intent);
            }
        });
        return convertView;
    }
}
class ViewHolder4{
 protected SimpleDraweeView statusPhoto;
}
