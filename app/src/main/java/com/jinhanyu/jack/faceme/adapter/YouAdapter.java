package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jinhanyu.jack.faceme.entity.Favorite;

import java.util.List;

/**
 * Created by anzhuo on 2016/10/26.
 */
public class YouAdapter extends CommonAdapter<Favorite>{

    public YouAdapter(List<Favorite> data, Context context) {
        super(data, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
