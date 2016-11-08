package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.adapter.DyqAdapter;
import com.jinhanyu.jack.faceme.entity.DyqItem;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;

/**
 * Created by anzhuo on 2016/10/27.陈礼
 */
public class DyqActivity extends Activity implements View.OnClickListener {
    private ImageView iv_back;//返回
    private TextView icon;//标题
    List<DyqItem> mlist;
    DyqAdapter adapter;//适配器
    DyqItem dyqItem;//数据源
    private GridView gv;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        icon = (TextView) findViewById(R.id.tv_icon);
        gv = (GridView) findViewById(R.id.gv);

        iv_back.setOnClickListener(this);

        BmobQuery<User> bmobQuery = new BmobQuery<>();







        type =getIntent().getStringExtra("icon");
        switch (type) {
            case "星客站":
                icon.setText("星客站");


                break;
            case "兴趣推荐":
                icon.setText("兴趣推荐");


                break;
            case "附近的人":
                icon.setText("附近的人");

                break;
        }
        adapter = new DyqAdapter(mlist, DyqActivity.this);
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
}
