package com.jinhanyu.jack.faceme.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.FaceMePopupWindow;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.entity.User;

import cn.bmob.v3.BmobUser;

import static com.jinhanyu.jack.faceme.R.id.tv_settings_logOut;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView back;
    private TextView editProfile,changePass,logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        back= (ImageView) findViewById(R.id.iv_settings_back);
        editProfile= (TextView) findViewById(R.id.tv_settings_editProfile);
        changePass= (TextView) findViewById(R.id.tv_settings_changePass);
        logOut= (TextView) findViewById(tv_settings_logOut);

        back.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        changePass.setOnClickListener(this);
        logOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_settings_back:
                finish();
                break;
            case R.id.tv_settings_editProfile:
                Intent intent=new Intent(this,EditProfileActivity.class);
                startActivity(intent);
            break;
            case R.id.tv_settings_changePass:
                Intent intent2=new Intent(this,ResetPassActivity.class);
                startActivity(intent2);
                break;
            case tv_settings_logOut:
                final FaceMePopupWindow popupWindow=new FaceMePopupWindow(this);
                popupWindow.setTitle("是否退出登录？");
                popupWindow.setOnConfirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User.logOut();   //清除缓存用户对象
                        BmobUser currentUser = User.getCurrentUser(); // 现在的currentUser是null了
                        Intent intent1=new Intent(SettingActivity.this,LoginActivity.class);
                        startActivity(intent1);
                    }
                });
                popupWindow.show(logOut);
                break;
        }
    }
}
