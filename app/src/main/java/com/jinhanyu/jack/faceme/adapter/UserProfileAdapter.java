package com.jinhanyu.jack.faceme.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.jinhanyu.jack.faceme.entity.Status;

import java.util.List;

/**
 * Created by jianbo on 2016/10/20.
 */
public class UserProfileAdapter extends CommonAdapter<Status> {
    public UserProfileAdapter(List<Status> data, Context context) {
        super(data, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
