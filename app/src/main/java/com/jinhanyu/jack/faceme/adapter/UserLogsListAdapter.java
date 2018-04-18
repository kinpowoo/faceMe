package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.jinhanyu.jack.faceme.R;

import java.util.List;

/**
 * Created by DeskTop29 on 2018/4/12.
 */

public class UserLogsListAdapter extends BaseAdapter {

    List<String> users;
    Context mContext;
    LayoutInflater mInflater;


    public UserLogsListAdapter(List<String> source, Context context){
        this.users = source;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return users==null?0: users.size();
    }

    @Override
    public Object getItem(int position) {
        return users==null?null: users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.login_record_list_item,null);
        }
        final TextView userName = (TextView) convertView.findViewById(R.id.phone_number);
        LinearLayout delete = (LinearLayout) convertView.findViewById(R.id.delete);

        if(users.size()>0) {
            userName.setText(users.get(position));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    users.remove(pos);
                    notifyDataSetChanged();
                }
            });
        }

        return convertView;
    }



    public List<String> getList(){
        return users;
    }
}
