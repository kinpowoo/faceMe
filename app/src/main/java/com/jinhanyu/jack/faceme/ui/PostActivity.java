package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.entity.Status;

import java.util.List;


/**
 * Created by anzhuo on 2016/10/18.陈礼 拍照
 */
public class PostActivity extends Activity implements View.OnClickListener {
      private List<Status> list;

      private ImageView iv_back;//返回
      private TextView tv_uploading;//发表
      private EditText et_content;//发表的内容
      private TextView word_number;//内容的字数限定（上限140）
      private ImageView who_look;//@谁，给谁看
      private ImageView face;//表情
      private ImageView location,surfaceview;//定位
      private TextView location_show;//具体位置

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.post_activity);

        //控件实例化
        surfaceview = (ImageView)findViewById(R.id.surfaceview);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_uploading = (TextView)findViewById(R.id.tv_uploading);
        et_content = (EditText) findViewById(R.id.et_content);
        word_number = (TextView) findViewById(R.id.word_number);
        who_look = (ImageView)findViewById(R.id.who_look);
        face = (ImageView) findViewById(R.id.face);
        location = (ImageView)findViewById(R.id.location);
        location_show = (TextView)findViewById(R.id.location_show);

        iv_back.setOnClickListener(this);
        tv_uploading.setOnClickListener(this);


    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_uploading:
                String conment = et_content.getText().toString();//发表的内容

                break;
        }
    }
}
