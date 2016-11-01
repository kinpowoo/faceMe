package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.R;

/**
 * Created by anzhuo on 2016/10/25.陈礼
 */
public class NearbyActivity extends Activity implements View.OnClickListener {
    private ImageView iv_back;
    private ImageView NearbyActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_activity);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        NearbyActivity = (ImageView) findViewById(R.id.NearbyActivity);

        iv_back.setOnClickListener(this);
        NearbyActivity.setOnClickListener(this);
        Toast.makeText(NearbyActivity.this, "missing topics!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.NearbyActivity:
                startActivity(new Intent(NearbyActivity.this, WonderfulActivities.class));
                break;
        }
    }
}
