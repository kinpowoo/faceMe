package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.LocationUtils;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.entity.Position;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.io.File;
import java.util.Arrays;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * Created by anzhuo on 2016/10/18.陈礼 拍照
 */
public class PostActivity extends Activity implements View.OnClickListener {
    //    private List<Status> list;
//    private BmobFile bmobFile;

    private ImageView iv_back;//返回
    private TextView tv_uploading;//发表
    private EditText et_content;//发表的内容
    private TextView word_number;//内容的字数限定（上限140）
    private ImageView who_look;//@谁，给谁看
    private ImageView face;//表情
    private ImageView location, surfaceview;//定位
    private TextView location_show;//具体位置
    private String picPath;
    private Position location1;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    location1 = (Position) msg.obj;
                    String positionText = location1.getCountry()+ " "+ location1.getProvince() + " " + location1.getCity();
                    location_show.setText(positionText);
                    break;
            }
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);

        //控件实例化
        surfaceview = (ImageView) findViewById(R.id.surfaceview);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_uploading = (TextView) findViewById(R.id.tv_uploading);
        et_content = (EditText) findViewById(R.id.et_content);
        word_number = (TextView) findViewById(R.id.word_number);
        who_look = (ImageView) findViewById(R.id.who_look);
        face = (ImageView) findViewById(R.id.face);
        location = (ImageView) findViewById(R.id.location);
        location_show = (TextView) findViewById(R.id.location_show);

        iv_back.setOnClickListener(this);
        tv_uploading.setOnClickListener(this);
        showPicture();

        new LocationUtils(this,handler).guide();
        et_content.addTextChangedListener(mTextWatcher);



    }

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            temp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            int length =  et_content.getText().toString().length();
            word_number.setText((100-length)+"");
            if (temp.length() > 100) {
                Toast.makeText(PostActivity.this,
                        "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT)
                        .show();
                 et_content.setText(et_content.getText().toString().substring(0,100));
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_uploading:
                final String content = et_content.getText().toString();//发表的内容
                final BmobFile bmobFile = new BmobFile(new File(picPath));
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        Status status =  new Status();
                        status.setAuthor(User.getCurrentUser(User.class));
                        status.setPhoto(bmobFile);
                        status.setText(content);
                        status.setTags(Arrays.asList("哈哈"));
                        status.setLocation(new BmobGeoPoint(location1.getLongitude(),location1.getLatitude()));
                        status.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(PostActivity.this,"发表失败",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                });

                break;


        }
    }

    public void showPicture() {
        picPath = getIntent().getStringExtra("pic");
        surfaceview.setImageBitmap(BitmapFactory.decodeFile(picPath));
    }
}
