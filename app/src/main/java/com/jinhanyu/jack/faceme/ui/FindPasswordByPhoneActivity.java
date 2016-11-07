package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.FaceMePopupWindow;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.TickTimer;
import com.jinhanyu.jack.faceme.entity.User;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by anzhuo on 2016/11/4.
 */

public class FindPasswordByPhoneActivity extends Activity {


    EditText et_phone_number;
    EditText et_password;
    EditText et_verify_code;
    TextView bt_get_verify_code;
    String verify_code;
    String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.findpasswordbyphone);
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        et_password = (EditText) findViewById(R.id.et_password);
        et_verify_code = (EditText) findViewById(R.id.et_verify_code);
        bt_get_verify_code = (TextView) findViewById(R.id.bt_get_verify_code);

    }

    public void goBack(View view) {
        finish();
    }

    public void reset(View view) {
        if(TextUtils.isEmpty(phone_number)){
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        String password  = et_password.getText().toString().trim();
        String verify_code = et_verify_code.getText().toString().trim();

        if(TextUtils.isEmpty(verify_code)){
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }else if(!verify_code.equals(this.verify_code)){
            Toast.makeText(this, "验证码错误，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "请输入新密码（密码为6-16个字符的数字或字母）", Toast.LENGTH_SHORT).show();
            return;
        }else if(!password.matches("^[a-zA-Z0-9_?/.]{6,16}$")){
            Toast.makeText(this, "非法密码（密码为6-16个字符的数字或字母）", Toast.LENGTH_SHORT).show();
            return;
        }

        User.resetPasswordBySMSCode(verify_code,password, new UpdateListener() {
            @Override
            public void done(BmobException ex) {
                if(ex==null){
                    Log.i("smile", "密码重置成功");
                }else{
                    Log.i("smile", "重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                }
            }
        });


    }

    public void getVerifyCode(View view) {
        final String phone_number = et_phone_number.getText().toString().trim();
        if (TextUtils.isEmpty(phone_number)) {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phone_number.matches("\\d{11}")) {
            Toast.makeText(this, "非法手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        new FaceMePopupWindow(this)
                .setTitle(phone_number)
                .setMessage("验证码将会以短信形式发送至此号码。如需更改号码请点击取消")
                .setOnConfirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //发送验证短信
                        et_verify_code.setEnabled(false);
                        BmobSMS.requestSMSCode(phone_number,"test", new QueryListener<Integer>() {
                            @Override
                            public void done(Integer smsId,BmobException ex) {
                                if(ex==null){//验证码发送成功
                                    Log.i("smile", "短信id："+smsId);//用于后续的查询本次短信发送状态
                                    verify_code = smsId+"";
                                    FindPasswordByPhoneActivity.this.phone_number = phone_number;
                                    et_verify_code.setEnabled(true);
                                    Toast.makeText(FindPasswordByPhoneActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                                    new TickTimer(bt_get_verify_code,60){
                                        @Override
                                        protected void onTimeEnd() {
                                            super.onTimeEnd();
                                            et_verify_code.setEnabled(true);
                                            et_verify_code.setText("重发");
                                        }
                                    }.startTick();
                                }
                            }
                        });
                    }
                });
    }
}
