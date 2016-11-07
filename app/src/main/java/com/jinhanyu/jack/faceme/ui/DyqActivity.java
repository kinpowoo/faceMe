package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
    private ListView common_listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        icon = (TextView) findViewById(R.id.tv_icon);
        common_listView = (ListView) findViewById(R.id.common_listView);

        iv_back.setOnClickListener(this);

        BmobQuery<User> bmobQuery = new BmobQuery<>();







        adapter = new DyqAdapter(mlist, DyqActivity.this);
        common_listView.setAdapter(adapter);

        //设置标题
        icon.setText(getIntent().getStringExtra("icon"));
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
