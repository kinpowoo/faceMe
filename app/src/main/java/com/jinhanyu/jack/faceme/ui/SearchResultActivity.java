package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.Ptr_refresh;
import com.jinhanyu.jack.faceme.R;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by anzhuo on 2016/10/28.陈礼
 */
public class SearchResultActivity extends Activity implements View.OnClickListener{
    private ImageView iv_back;//返回
    private TextView tv_icon;//搜索的标题
    private in.srain.cube.views.ptr.PtrFrameLayout iv_ptrFrame;//下拉刷新
    private GridView gv;//九宫格图片显示
    private Ptr_refresh refresh;//下拉刷新类对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        //控件初始化
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_icon = (TextView) findViewById(R.id.tv_icon);
        iv_ptrFrame = (PtrFrameLayout) findViewById(R.id.iv_ptrFrame);
        gv = (GridView) findViewById(R.id.gv);
        refresh = new Ptr_refresh(SearchResultActivity.this);

        //控件的监听
        iv_back.setOnClickListener(this);

        //设置标题
        tv_icon.setText(getIntent().getStringExtra("search_text"));

        //下拉刷新，从新加载内容
        iv_ptrFrame.setHeaderView(refresh);
        iv_ptrFrame.addPtrUIHandler(refresh);
        iv_ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                iv_ptrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {




                        iv_ptrFrame.refreshComplete();
                    }
                }, 2000);
            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                  finish();
                break;
        }
    }
}
