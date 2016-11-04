package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.FaceMePopupWindow;
import com.jinhanyu.jack.faceme.R;

/**
 * Created by anzhuo on 2016/11/3.
 */

public class RegisterByPhoneActivity extends Activity {

    EditText et_phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_by_phone);
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);

    }

    public void goToRegisterByEmail(View view) {
        startActivity(new Intent(this, RegisterByEmailActivity.class));
    }

    public void registerByPhone(View view) {
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
//                User user = new User();
//                user.setUsername(phone_number);
//                user.setPassword("123456");
//                user.setMobilePhoneNumber(phone_number);
//                user.signUp(new SaveListener<User>() {
//                    @Override
//                    public void done(User user, BmobException e) {
//                    }
//                });
                    }
                });


    }

    public void goBack(View view) {
        finish();
    }

    public void goToUserAgreement(View view) {
        startActivity(new Intent(this, UserAgreementActivity.class));
    }
}
