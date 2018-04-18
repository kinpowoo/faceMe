package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.ClearEditText;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.regex.Pattern;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by anzhuo on 2016/11/3.
 */

public class PhoneAuthCodeActivity extends Activity implements View.OnClickListener,TextWatcher{
    private ImageView back;
    private TextView commit,timerClock,backToChange;
    private ClearEditText authorityCode;
    private String phoneNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_authority_code);
        back= (ImageView) findViewById(R.id.iv_authority_back);
        commit= (TextView) findViewById(R.id.tv_authority_commit);
        timerClock= (TextView) findViewById(R.id.tv_authority_notify);
        backToChange= (TextView) findViewById(R.id.tv_authority_backToChange);
        authorityCode= (ClearEditText) findViewById(R.id.cet_authority_code);

        back.setOnClickListener(this);
        commit.setOnClickListener(this);
        timerClock.setOnClickListener(this);
        authorityCode.addTextChangedListener(this);
        backToChange.setOnClickListener(this);

        phoneNum=getIntent().getStringExtra("phoneNum");
        sendSMSCode();
    }


    public void sendSMSCode() {
        timerClock.setText("正在发送验证码");
        BmobSMS.requestSMSCode(this, phoneNum, "手机号码登陆模板", new RequestSMSCodeListener() {
            @Override
            public void done(Integer smsId, BmobException ex) {
                // TODO Auto-generated method stub
                if (ex == null) {// 验证码发送成功
                 timerClock.setText("请填写验证码");
                } else {   //如果验证码发送错误，可停止计时
                    timerClock.setText("验证码发送失败");
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_authority_back:
                finish();
                break;
            case R.id.tv_authority_commit:
                String code=authorityCode.getText().toString();
                BmobSMS.verifySmsCode(this, phoneNum, code, new VerifySMSCodeListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            User user =new User();
                            user.setMobilePhoneNumber(phoneNum);
                            user.setMobilePhoneNumberVerified(true);
                            user.update(Utils.getCurrentUser().getObjectId(), new UpdateListener() {
                                @Override
                                public void done(cn.bmob.v3.exception.BmobException e) {
                                    Intent intent=new Intent(PhoneAuthCodeActivity.this,EditProfileActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }else {
                            timerClock.setText("验证失败，请重新请求验证码");
                        }
                    }
                });

                break;
            case R.id.tv_authority_backToChange:
                finish();
                break;
            case R.id.tv_authority_notify:
                sendSMSCode();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        commit.setEnabled(false);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.length()>0&&!s.equals("")){
            if(Pattern.matches("\\d{6}",s)){
                commit.setEnabled(true);
            }else {
                commit.setEnabled(false);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

}



