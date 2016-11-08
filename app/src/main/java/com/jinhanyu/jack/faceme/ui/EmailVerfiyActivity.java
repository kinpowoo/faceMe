package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.ClearEditText;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by anzhuo on 2016/11/3.
 */

public class EmailVerfiyActivity extends Activity implements View.OnClickListener{
    private ImageView back;
    private TextView commit;
    private ClearEditText emailAddress;
    private User currentUser= Utils.getCurrentUser();
    private String emailRecieve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verify);
        back= (ImageView) findViewById(R.id.iv_email_verify_back);
        commit= (TextView) findViewById(R.id.tv_email_verify_commit);
        emailAddress= (ClearEditText) findViewById(R.id.cet_email_verify_emailAddress);

        back.setOnClickListener(this);
        commit.setOnClickListener(this);

        emailRecieve=getIntent().getStringExtra("email");
        if(emailRecieve!=null){
            emailAddress.setText(emailRecieve);
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_email_verify_back:
                finish();
                break;
            case R.id.tv_email_verify_commit:
                final String email=emailAddress.getText().toString();
                if(emailRecieve.equals(email)){
                    if(currentUser.getEmailVerified()){
                        finish();
                    }else {
                        BmobUser.requestEmailVerify(emailRecieve, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                Toast.makeText(EmailVerfiyActivity.this, "请求验证邮件成功，请到"
                                        + emailRecieve + "邮箱中进行激活。",Toast.LENGTH_SHORT).show();
                              setResult(RESULT_OK,new Intent().putExtra("email",emailAddress.getText().toString()));
                              finish();
                            }
                        });
                    }
                } else{
                    if(!Pattern.matches(".*?@.*?\\.com",email)){
                        Toast.makeText(this,"邮件格式错误",Toast.LENGTH_SHORT).show();
                    }else{
                        BmobUser bmobUser =new BmobUser();
                        bmobUser.setEmail(email);
                        bmobUser.update(currentUser.getObjectId(),new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                  if(e==null){
                                      BmobUser.requestEmailVerify(email, new UpdateListener() {
                                          @Override
                                          public void done(BmobException e) {
                                              if(e==null){
                                                  Toast.makeText(EmailVerfiyActivity.this, "请求验证邮件成功，请到"
                                                          + email + "邮箱中进行激活。",Toast.LENGTH_SHORT).show();
                                                  setResult(RESULT_OK,new Intent().putExtra("email",emailAddress.getText().toString()));
                                                  finish();
                                              }else {
                                                  Toast.makeText(EmailVerfiyActivity.this, "请求验证邮件失败，请重新提交",Toast.LENGTH_SHORT).show();
                                              }
                                          }
                                      });

                                  }
                            }
                        });


                    }

                }

        }
    }
}
