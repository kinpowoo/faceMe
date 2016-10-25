package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.R;

/**
 * Created by anzhuo on 2016/10/25.
 */
public class NearbyActivity extends Activity implements View.OnClickListener {
        private ImageView iv_back;

    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(NearbyActivity.this,"missing topics!",Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_activity);

        iv_back = (ImageView) findViewById(R.id.iv_back);

        iv_back.setOnClickListener(this);

        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

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
