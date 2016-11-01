package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jinhanyu.jack.faceme.R;

/**
 * Created by anzhuo on 2016/10/26.陈礼
 */
public class WonderfulActivities extends Activity implements View.OnClickListener{
       private ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wonderful_activities);

        iv_back = (ImageView) findViewById(R.id.iv_back);

        iv_back.setOnClickListener(this);
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
