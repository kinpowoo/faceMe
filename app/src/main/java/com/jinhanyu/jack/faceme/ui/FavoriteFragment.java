package com.jinhanyu.jack.faceme.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.jinhanyu.jack.faceme.Ptr_refresh;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.adapter.FavoriteFragmentAdapter;
import com.jinhanyu.jack.faceme.entity.SingleFavoriteItem;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;


/**
 * Created by anzhuo on 2016/10/18.陈礼
 */
public class
FavoriteFragment extends Fragment {
    private SingleFavoriteItem singleFavoriteItem;//数据源
    private FavoriteFragmentAdapter adapter;//适配器
    private GridView gv;
    private List<SingleFavoriteItem> list;
     in.srain.cube.views.ptr.PtrFrameLayout iv_frame;
     Ptr_refresh ptr_refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_fragment, null);

           //控件初始化
        gv = (GridView) view.findViewById(R.id.gv);
        iv_frame = (PtrFrameLayout) view.findViewById(R.id.iv_ptrFrame);
        list = new ArrayList<>();
        ptr_refresh = new Ptr_refresh(getActivity());

          AddPicture_test();//文字


        adapter = new FavoriteFragmentAdapter(list,getActivity());
        gv.setAdapter(adapter);


        //下拉刷新
        iv_frame.setHeaderView(ptr_refresh);
        iv_frame.addPtrUIHandler(ptr_refresh);
        iv_frame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                iv_frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      iv_frame.refreshComplete();
                    }
                },2000);
            }
        });
        return view;
    }


      //添加固定图片文字
    public void AddPicture_test(){
        String [] test={"人气","星动态","小电影","旅行","美女","美食","小宝贝","附近动态"};
        for (int a=0;a<8 ;a++){
            singleFavoriteItem = new SingleFavoriteItem();
            singleFavoriteItem.setPicture_text(test[a]);
            singleFavoriteItem.setPicture("res://com.jinhanyu.jack.faceme/"+R.mipmap.test_1);
            list.add(singleFavoriteItem);
        }

    }
}
