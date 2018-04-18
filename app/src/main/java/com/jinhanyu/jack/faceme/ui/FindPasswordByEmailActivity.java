package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.entity.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by anzhuo on 2016/11/4.
 */

public class FindPasswordByEmailActivity extends Activity {

    EditText et_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.findpasswordbyemail);
        et_email = (EditText) findViewById(R.id.et_email);
    }

    public void goBack(View view) {
        finish();
    }

    public void send(View view) {
        final String email = et_email.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "请输入邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }else if(!email.matches("^([a-zA-Z0-9_\\.\\-])+@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$")){
            Toast.makeText(this, "无效邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }

        User.resetPasswordByEmail(email, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(FindPasswordByEmailActivity.this, "重置密码请求成功，请到" + email + "邮箱进行密码重置操作", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(FindPasswordByEmailActivity.this, "失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
