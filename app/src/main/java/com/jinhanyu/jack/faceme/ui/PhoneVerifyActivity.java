package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.ClearEditText;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;


import java.util.regex.Pattern;


/**
 * Created by anzhuo on 2016/11/3.
 */

public class PhoneVerifyActivity extends Activity implements View.OnClickListener{
    private ImageView back;
    private TextView next;
    private ClearEditText phoneNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_verify);
        back= (ImageView) findViewById(R.id.iv_phone_verify_back);
        next= (TextView) findViewById(R.id.tv_phone_verify_next);
        phoneNum= (ClearEditText) findViewById(R.id.cet_phone_verify_phoneNum);

        back.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_phone_verify_back:
                finish();
                break;
            case R.id.tv_phone_verify_next:
                String phone=phoneNum.getText().toString();
                if(phone==null||phone.equals("")){
                    Toast.makeText(this,"手机号不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    if(!Pattern.matches("\\d{11}",phone)){
                        Toast.makeText(this,"手机号必须为11位",Toast.LENGTH_SHORT).show();
                    }else {
                        if (phone.equals(Utils.getCurrentUser().getMobilePhoneNumber())) {
                            next.setEnabled(false);
                            Toast.makeText(this, "手机号已验证", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(this, PhoneAuthCodeActivity.class);
                            intent.putExtra("phoneNum", phone);
                            startActivity(intent);
                        }
                    }
                }
        }
    }
}
