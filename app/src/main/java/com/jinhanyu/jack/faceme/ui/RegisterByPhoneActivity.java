package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.CustomProgress;
import com.jinhanyu.jack.faceme.FaceMePopupWindow;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.TickTimer;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by anzhuo on 2016/11/3.
 */

public class RegisterByPhoneActivity extends Activity {

    EditText et_phone_number;
    EditText et_password;
    EditText et_password_again;
    EditText et_verify_code;
    TextView bt_get_verify_code;
    String phone_number;
    String password;

    List<String> coutries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_by_phone);
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password_again = (EditText) findViewById(R.id.et_password_again);
        et_verify_code = (EditText) findViewById(R.id.et_verify_code);
        bt_get_verify_code = (TextView) findViewById(R.id.bt_get_verify_code);

        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(final int event, final int result, final Object data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            //回调完成
                            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                                //提交验证码成功
                                Log.i("smile", "验证通过");
                                User user = new User();
                                user.setUsername(phone_number);
                                user.setPassword(password);
                                user.setMobilePhoneNumber(phone_number);
                                user.signUp(new SaveListener<User>() {
                                    @Override
                                    public void done(User user, BmobException e) {
                                        CustomProgress.unshow();
                                        if (e == null) {
                                            Toast.makeText(RegisterByPhoneActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterByPhoneActivity.this, RegisterExtraActivity.class));
                                        } else {
                                            Toast.makeText(RegisterByPhoneActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                //获取验证码成功
                                et_verify_code.setEnabled(true);
                                CustomProgress.unshow();
                                Toast.makeText(RegisterByPhoneActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
                                new TickTimer(bt_get_verify_code, 60) {
                                    @Override
                                    protected void onTimeEnd() {
                                        super.onTimeEnd();
                                        bt_get_verify_code.setEnabled(true);
                                        bt_get_verify_code.setText("重发");
                                    }
                                }.startTick();
                            } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                                //返回支持发送验证码的国家列表
                                Log.i("countries",data.toString());
                            }
                        } else {
                            ((Throwable) data).printStackTrace();
                            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                                Toast.makeText(RegisterByPhoneActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                            } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                CustomProgress.unshow();
                                bt_get_verify_code.setEnabled(true);
                                Toast.makeText(RegisterByPhoneActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

    }

    public void goToRegisterByEmail(View view) {
        startActivity(new Intent(this, RegisterByEmailActivity.class));
    }

    public void registerByPhone(View view) {
        if (TextUtils.isEmpty(phone_number)) {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        password = et_password.getText().toString().trim();
        String password_again = et_password_again.getText().toString().trim();
        String verify_code = et_verify_code.getText().toString().trim();


        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码（密码为6-16个字符的数字或字母）", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password.matches("^[a-zA-Z0-9_?/.]{6,16}$")) {
            Toast.makeText(this, "非法密码（密码为6-16个字符的数字或字母）", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password_again)) {
            Toast.makeText(this, "再次输入密码（密码为6-16个字符的数字或字母）", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password_again.equals(password)) {
            Toast.makeText(this, "确认密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(verify_code)) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomProgress.show(this, "正在注册...");

        SMSSDK.submitVerificationCode("+86", phone_number, verify_code);


    }

    public void goBack(View view) {
        finish();
    }

    public void goToUserAgreement(View view) {
        startActivity(new Intent(this, UserAgreementActivity.class));
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

        this.phone_number = phone_number;

        new FaceMePopupWindow(this)
                .setTitle(phone_number)
                .setMessage("验证码将会以短信形式发送至此号码。如需更改号码请点击取消")
                .setOnConfirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //发送验证短信
                        bt_get_verify_code.setEnabled(false);
                        CustomProgress.show(RegisterByPhoneActivity.this, "正在发送...");


                        SMSSDK.getVerificationCode("+86", phone_number);
                    }
                }).show(view);
    }




}
