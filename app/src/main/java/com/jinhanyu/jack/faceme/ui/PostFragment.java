package com.jinhanyu.jack.faceme.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jinhanyu.jack.faceme.R;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class PostFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.post_fragment,null);
        return view;
    }
}
