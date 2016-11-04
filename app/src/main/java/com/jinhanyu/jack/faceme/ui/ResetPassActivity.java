package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;

import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by anzhuo on 2016/11/4.
 */

public class ResetPassActivity extends Activity implements View.OnClickListener{
    private ImageView back;
    private EditText old,newPass,again;
    private TextView submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_activity);
        back= (ImageView) findViewById(R.id.iv_changePass_back);
        submit= (TextView) findViewById(R.id.tv_changePass_submit);
        old= (EditText) findViewById(R.id.et_changePass_old);
        newPass= (EditText) findViewById(R.id.et_changePass_new);
        again= (EditText) findViewById(R.id.et_changePass_again);

        back.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_changePass_back:
                finish();
                break;
            case R.id.tv_changePass_submit:
                String oldPasswd=old.getText().toString();
                String newPasswd=newPass.getText().toString();
                String againPasswd=again.getText().toString();
                if(newPasswd.equals(againPasswd)) {
                    if(Pattern.matches("[a-zA-Z0-9_?./$]{6,16}",newPasswd)){
                        BmobUser.updateCurrentUserPassword(oldPasswd, newPasswd, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(ResetPassActivity.this,"可以用新密码进行登录",Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(ResetPassActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(ResetPassActivity.this,"原密码输入错误",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(this,"请输入规范的密码",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this,"两次密码输入不一致,\n请重新输入",Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
}
