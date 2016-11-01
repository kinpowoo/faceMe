package com.jinhanyu.jack.faceme.ui;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.ClearEditText;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by anzhuo on 2016/10/18.
 */
public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView cancel;
    private TextView commit,changePortrait,gender,email,phone,username;
    private SimpleDraweeView userPortrait;
    private ClearEditText nickname;
    private Float alpha=1.0f;
    private PopupWindow popupWindow;
    private View chooseGenderView;
    private TextView male,female,chooseGenderCancel;
    private View photoSourceView;
    private TextView camera,photoLibrary,photoSourceCancel;
    private User currentUser= Utils.getCurrentUser();

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    backgroundAlpha((float)msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);
        cancel= (ImageView) findViewById(R.id.tv_edit_profile_cancel);
        commit= (TextView) findViewById(R.id.tv_edit_profile_commit);
        username= (TextView) findViewById(R.id.tv_edit_profile_username);
        changePortrait= (TextView) findViewById(R.id.tv_edit_profile_changePortrait);
        nickname= (ClearEditText) findViewById(R.id.cet_edit_profile_nickname);
        email= (TextView) findViewById(R.id.tv_edit_profile_email);
        phone= (TextView) findViewById(R.id.tv_edit_profile_phone);
        gender= (TextView) findViewById(R.id.tv_edit_profile_gender);
        userPortrait= (SimpleDraweeView) findViewById(R.id.sdv_edit_profile_userPortrait);

        userPortrait.setImageURI(currentUser.getPortrait().getUrl());
        username.setText(currentUser.getUsername());
        nickname.setText(currentUser.getNickname());
        gender.setText(currentUser.getGender());

        cancel.setOnClickListener(this);
        commit.setOnClickListener(this);
        changePortrait.setOnClickListener(this);
        email.setOnClickListener(this);
        phone.setOnClickListener(this);
        gender.setOnClickListener(this);

        chooseGenderView= LayoutInflater.from(this).inflate(R.layout.choose_gender_menu,null);
        male= (TextView) chooseGenderView.findViewById(R.id.tv_choose_gender_male);
        female= (TextView) chooseGenderView.findViewById(R.id.tv_choose_gender_female);
        chooseGenderCancel=(TextView) chooseGenderView.findViewById(R.id.tv_choose_gender_cancel);
        male.setOnClickListener(this);
        female.setOnClickListener(this);
        chooseGenderCancel.setOnClickListener(this);

        photoSourceView=LayoutInflater.from(this).inflate(R.layout.choose_photo_source,null);
        camera= (TextView) photoSourceView.findViewById(R.id.tv_choose_photo_source_camera);
        photoLibrary= (TextView) photoSourceView.findViewById(R.id.tv_choose_photo_source_photoLibrary);
        photoSourceCancel= (TextView) photoSourceView.findViewById(R.id.tv_choose_photo_source_cancel);
        camera.setOnClickListener(this);
        photoLibrary.setOnClickListener(this);
        photoSourceCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit_profile_cancel:
                finish();
                break;
            case R.id.tv_edit_profile_commit:

//                currentUser.setPortrait();   //   这里没上传怎么得到url,待解决
                currentUser.setNickname(nickname.getText().toString());
                currentUser.setGender(gender.getText().toString());
                currentUser.setEmail(email.getText().toString());
                currentUser.setMobilePhoneNumber(phone.getText().toString());
                currentUser.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                    if(e==null){
                        finish();
                    }
                    }
                });
                break;

            case R.id.tv_edit_profile_changePortrait:
                if (popupWindow != null && popupWindow.isShowing()) {
                    return;
                } else {
                    showWindow(photoSourceView,changePortrait);
                }
                break;


            case R.id.tv_edit_profile_email:
                break;
            case R.id.tv_edit_profile_phone:
                break;

            case R.id.tv_edit_profile_gender:
                if (popupWindow != null && popupWindow.isShowing()) {
                    return;
                } else {
                    showWindow(chooseGenderView,gender);
                }
                break;
            case R.id.tv_choose_gender_male:
                popupWindow.dismiss();
                gender.setText("男");
                break;
            case R.id.tv_choose_gender_female:
                popupWindow.dismiss();
                gender.setText("女");
                break;
            case R.id.tv_choose_gender_cancel:
                popupWindow.dismiss();
                break;
            case R.id.tv_choose_photo_source_camera:
                break;
            case R.id.tv_choose_photo_source_photoLibrary:
                break;
            case R.id.tv_choose_photo_source_cancel:
                popupWindow.dismiss();
                break;
        }
    }

    public void showWindow(View targetView,View locationView){
        popupWindow = new PopupWindow(targetView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //添加弹出、弹入的动画
        popupWindow.setAnimationStyle(R.style.PopupWindow_menu);
        int[] location = new int[2];
        locationView.getLocationOnScreen(location);
        popupWindow.showAtLocation(locationView, Gravity.LEFT | Gravity.BOTTOM, 0, -location[1]);
        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //此处while的条件alpha不能<= 否则会出现黑屏
                        while (alpha < 1f) {
                            try {
                                Thread.sleep(4);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Message msg = mHandler.obtainMessage();
                            msg.what = 1;
                            alpha += 0.01f;
                            msg.obj = alpha;
                            mHandler.sendMessage(msg);
                        }
                    }

                }).start();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (alpha > 0.5f) {
                    try {
                        //4是根据弹出动画时间和减少的透明度计算
                        Thread.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    //每次减少0.01，精度越高，变暗的效果越流畅
                    alpha -= 0.01f;
                    msg.obj = alpha;
                    mHandler.sendMessage(msg);
                }
            }

        }).start();
    }



    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
}
