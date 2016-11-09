package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jinhanyu.jack.faceme.R;

/**
 * Created by anzhuo on 2016/11/9.
 */

public class AboutActivity extends Activity {
    private ImageView  back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        back= (ImageView) findViewById(R.id.iv_about_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
