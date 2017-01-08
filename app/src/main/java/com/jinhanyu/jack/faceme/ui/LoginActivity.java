package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.CustomProgress;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.SelectableFaceMePopupWindow;
import com.jinhanyu.jack.faceme.entity.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class LoginActivity extends Activity {

    EditText et_username;
    EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        et_password = (EditText) findViewById(R.id.et_password);
        et_username = (EditText) findViewById(R.id.et_username);

    }

    public void login(View view) {
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomProgress.show(this,"正在登陆...");

        User.loginByAccount(username, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                CustomProgress.unshow();
                if (e == null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void forgetPassword(View view) {

        new SelectableFaceMePopupWindow(this)
                .setTitle("请选择")
                .addOption("电子邮箱找回密码", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LoginActivity.this, FindPasswordByEmailActivity.class));
                    }
                })
                .addOption("手机号码找回密码", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LoginActivity.this, FindPasswordByPhoneActivity.class));
                    }
                })
                .show(view);
    }

    public void register(View view) {
          startActivity(new Intent(this,RegisterByPhoneActivity.class));
    }
}
