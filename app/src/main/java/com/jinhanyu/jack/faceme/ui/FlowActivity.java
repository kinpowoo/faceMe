package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.adapter.GridViewAdapter;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by anzhuo on 2016/10/27.陈礼
 */
public class FlowActivity extends Activity implements View.OnClickListener {
    private ImageView iv_back;//返回
    private TextView icon;//标题
    private GridViewAdapter adapter;//适配器
    private GridView gv;
    private String type;;
    private List<Status> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        icon = (TextView) findViewById(R.id.tv_icon);
        gv = (GridView) findViewById(R.id.gv);

        iv_back.setOnClickListener(this);
        list = new ArrayList<>();

        type =getIntent().getStringExtra("icon");
        switch (type) {
            case "明星":
                icon.setText("星客站");
                getPic("明星");
                break;
        }
        adapter = new GridViewAdapter(list, FlowActivity.this);
        gv.setAdapter(adapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    //加载图片
    public void getPic(String tags){
        BmobQuery<Status> statusBmobQuery = new BmobQuery<>();
        statusBmobQuery.addWhereEqualTo("tags",tags);
        statusBmobQuery.include("author");
        statusBmobQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> data, BmobException e) {
                list.addAll(data);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
