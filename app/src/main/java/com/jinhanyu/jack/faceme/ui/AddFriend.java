package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jinhanyu.jack.faceme.Ptr_refresh;
import com.jinhanyu.jack.faceme.R;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by anzhuo on 2016/10/25.
 */
public class AddFriend extends Activity implements View.OnClickListener{
    private ImageView iv_back;
    private Ptr_refresh refresh;//刷新类
    private in.srain.cube.views.ptr.PtrFrameLayout iv_frame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_frame = (PtrFrameLayout) findViewById(R.id.iv_ptrFrame);

        refresh = new Ptr_refresh(this);
        iv_frame.setHeaderView(refresh);
        iv_frame.addPtrUIHandler(refresh);

        iv_frame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                iv_frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       iv_frame.refreshComplete();


                        //刷新代码写在这
                    }
                }, 1500);
            }
        });


        iv_back.setOnClickListener(this);

    }

















    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
