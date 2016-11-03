package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.VerifyCodeView;
import com.jinhanyu.jack.faceme.entity.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by anzhuo on 2016/11/3.
 */
public class RegisterByEmailActivity extends Activity{
    VerifyCodeView verifyCodeView;
    EditText et_email;
    EditText et_password;
    EditText et_password_again;
    EditText et_verify_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_by_email);
        verifyCodeView  = (VerifyCodeView) findViewById(R.id.tv_verify_code);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password_again  = (EditText) findViewById(R.id.et_password_again);
        et_verify_code = (EditText) findViewById(R.id.et_verify_code);

    }

    public void refreshVerifyCode(View view) {
        verifyCodeView.genRandomCode();
    }

    public void registerByEmail(View view) {
        String email = et_email.getText().toString().trim();
        String password  = et_password.getText().toString().trim();
        String password_again = et_password_again.getText().toString().trim();
        String verify_code = et_verify_code.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "请输入邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }else if(!email.matches("^([a-zA-Z0-9_\\.\\-])+@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$")){
            Toast.makeText(this, "无效邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "请输入密码（密码为6-16个字符的数字或字母）", Toast.LENGTH_SHORT).show();
            return;
        }else if(!password.matches("^[a-zA-Z0-9]{6,16}$")){
            Toast.makeText(this, "非法密码（密码为6-16个字符的数字或字母）", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password_again)){
            Toast.makeText(this, "再次输入密码（密码为6-16个字符的数字或字母）", Toast.LENGTH_SHORT).show();
            return;
        }else if(!password_again.equals(password)){
            Toast.makeText(this, "确认密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(verify_code)){
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }else if(!verify_code.equals(verifyCodeView.getRandomCode())){
            Toast.makeText(this, "验证码错误，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }


        User user = new User();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {

            }
        });

    }

    public void goBack(View view) {
        finish();
    }

    public void goToUserAgreement(View view) {
        startActivity(new Intent(this,UserAgreementActivity.class));
    }
}
