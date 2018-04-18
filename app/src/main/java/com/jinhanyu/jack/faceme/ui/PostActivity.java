package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.CustomProgress;
import com.jinhanyu.jack.faceme.LocationUtils;
import com.jinhanyu.jack.faceme.MainApplication;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.cutsom_view.NoScrollGridView;
import com.jinhanyu.jack.faceme.entity.Position;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * Created by anzhuo on 2016/10/18.陈礼 拍照
 */
public class PostActivity extends Activity implements View.OnClickListener {
    private static final int MGQ = 12;
    private ImageView iv_back;//返回
    private TextView tv_uploading;//发表
    private EditText et_content;//发表的内容
    private TextView word_number;//内容的字数限定（上限140）
    private ImageView surfaceview;//定位
    private TextView location_show;//具体位置
    private GridLayout gl;
    private String picPath;
    private Position location = null;
    private CheckedTextView tv_FaceMe, tv_travel, tv_baby, tv_nearby, tv_start, tv_boy, tv_belle, tv_food, custom_type_one, custom_type_two;
    private ImageView tv_add;
    private int tag_count = 0;//计数器
    List<String> tags  = new ArrayList<>();;
    private  int typ=0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(PostActivity.this,"定位结果已更新",Toast.LENGTH_LONG).show();
                    location = (Position) msg.obj;
                    if(location.getStreet()!=null) {
                        String positionText = location.getDistrict() + "." + location.getStreet() + "." + location.getAoiName();
                        location_show.setText(positionText);
                    }else {
                        location_show.setText(location.getProvince()+"."+location.getCity());
                    }

                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);

        LocationUtils.getInstance().guide(handler);

        //控件实例化
        surfaceview = (ImageView) findViewById(R.id.surfaceview);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_uploading = (TextView) findViewById(R.id.tv_uploading);
        et_content = (EditText) findViewById(R.id.et_content);
        word_number = (TextView) findViewById(R.id.word_number);
        location_show = (TextView) findViewById(R.id.location_show);
        gl = (GridLayout) findViewById(R.id.gl);
        tv_FaceMe = (CheckedTextView) findViewById(R.id.tv_FaceMe);//FaceMe
        tv_travel = (CheckedTextView) findViewById(R.id.tv_travel);//旅行
        tv_baby = (CheckedTextView) findViewById(R.id.tv_baby);//小宝贝
        tv_nearby = (CheckedTextView) findViewById(R.id.tv_nearby);//附近
        tv_start = (CheckedTextView) findViewById(R.id.tv_start);//明星
        tv_boy = (CheckedTextView) findViewById(R.id.tv_boy);//帅哥
        tv_belle = (CheckedTextView) findViewById(R.id.tv_belle);//美女
        tv_food = (CheckedTextView) findViewById(R.id.tv_food);//美食
        custom_type_one = (CheckedTextView) findViewById(R.id.custom_type_one);//自定义1
        custom_type_two = (CheckedTextView) findViewById(R.id.custom_type_two);//自定义2

        tv_FaceMe.setOnClickListener(onClickListener);
        tv_travel.setOnClickListener(onClickListener);
        tv_baby.setOnClickListener(onClickListener);
        tv_nearby.setOnClickListener(onClickListener);
        tv_start.setOnClickListener(onClickListener);
        tv_boy.setOnClickListener(onClickListener);
        tv_belle.setOnClickListener(onClickListener);
        tv_food.setOnClickListener(onClickListener);


        tv_add = (ImageView) findViewById(R.id.tv_add);//添加自定义标签


        iv_back.setOnClickListener(this);
        tv_uploading.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        showPicture();

        et_content.addTextChangedListener(mTextWatcher);


    }




    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckedTextView checkedTextView = (CheckedTextView) v;
            if (checkedTextView.isChecked()) {
                tag_count--;
                checkedTextView.setChecked(false);
            } else {
                if (tag_count >= 3) {
                    Toast.makeText(PostActivity.this, "最多只能添加3个标签", Toast.LENGTH_SHORT).show();
                    return;
                }
                tag_count++;
                checkedTextView.setChecked(true);
            }
        }
    };

    private List<String> getTags() {
        if (tv_FaceMe.isChecked())
            tags.add("自拍");
        if (tv_travel.isChecked())
            tags.add("旅行");
        if (tv_baby.isChecked())
            tags.add("宝贝");
        if (tv_start.isChecked())
            tags.add("明星");
        if (tv_boy.isChecked())
            tags.add("帅哥");
        if (tv_belle.isChecked())
            tags.add("美女");
        if (tv_food.isChecked())
            tags.add("美食");
        if (tv_nearby.isChecked())
            tags.add("风景");

        return tags;

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
            int length = et_content.getText().toString().length();
            word_number.setText((100 - length) + "");
            if (temp.length() > 100) {
                Toast.makeText(PostActivity.this,
                        "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT)
                        .show();
                et_content.setText(et_content.getText().toString().substring(0, 100));
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
                if(location==null){
                    LocationUtils.getInstance().guide(handler);
                    Toast.makeText(this,"还未收到定位结果",Toast.LENGTH_LONG).show();
                    return;
                }

                final String content = et_content.getText().toString();//发表的内容
                final BmobFile bmobFile = new BmobFile(new File(picPath));
                CustomProgress.show(this, "正在发表...");
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        Status status = new Status();
                        status.setAuthor(User.getCurrentUser(User.class));
                        status.setPhoto(bmobFile);
                        status.setText(content);
                        if(location.getStreet()!=null){
                            status.setLocName(location.getDistrict()+location.getStreet()+location.getAoiName());
                        }else {
                            status.setLocName(location.getProvince()+location.getCity());
                        }
                        List<String> tt = getTags();
                        status.setTags(tt);
                        status.setLocation(new BmobGeoPoint(location.getLongitude(), location.getLatitude()));
                        status.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                CustomProgress.unshow();
                                if (e == null) {
                                    finish();
                                } else {
                                    Toast.makeText(PostActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                });

                break;
            case R.id.tv_add:
                if(custom_tag_count>=2){
                    Toast.makeText(PostActivity.this, "只能添加两个自定义标签", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tag_count>=3){
                    Toast.makeText(PostActivity.this, "最多添加三个标签", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivityForResult(new Intent(PostActivity.this, CustomTag.class), MGQ);
                break;
        }

    }


    public void showPicture() {
        picPath = getIntent().getStringExtra("pic");

        Log.i("tag","传过来的图片地址为空:"+picPath);
        Bitmap b = BitmapFactory.decodeFile(picPath);
        if(b==null){
            Log.i("tag","bitmap对象为空:");
        }
        surfaceview.setImageBitmap(b);
    }

    private int custom_tag_count = 0;

    /*添加自定义标签*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MGQ && resultCode == RESULT_OK) {
            if (tag_count <= 3) {
                String cn=custom_type_one.getText().toString().trim();
                String cm=custom_type_two.getText().toString().trim();
                if (TextUtils.isEmpty(cn)) {
                    custom_type_one.setText(data.getStringExtra("content"));
                    custom_type_one.setVisibility(View.VISIBLE);
                    tags.add(custom_type_one.getText().toString());
                    custom_tag_count++;
                    tag_count++;
                } else if(TextUtils.isEmpty(cm)){
                    custom_type_two.setText(data.getStringExtra("content"));
                    custom_type_two.setVisibility(View.VISIBLE);
                    tags.add(custom_type_two.getText().toString());
                    custom_tag_count++;
                    tag_count++;
                }else{

                }
            } else {
                Toast.makeText(PostActivity.this, "只能添加两个自定义标签", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void share(View view) {
        MainApplication.showShare(this,picPath,et_content.getText().toString());
    }
}

