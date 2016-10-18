package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.jinhanyu.jack.faceme.entity.SingleFavoriteItem;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class FavoriteFragmentAdapter extends CommonAdapter<SingleFavoriteItem> {
    public FavoriteFragmentAdapter(List<SingleFavoriteItem> data, Context context) {
        super(data, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
