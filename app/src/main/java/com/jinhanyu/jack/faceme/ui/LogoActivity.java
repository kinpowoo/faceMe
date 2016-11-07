package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.entity.User;

/**
 * Created by anzhuo on 2016/11/7.
 */

public class LogoActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.logo);
        final boolean isFirstLaunch = getSharedPreferences("logo",MODE_PRIVATE).getBoolean("first",true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isFirstLaunch){
                    startActivity(new Intent(LogoActivity.this,SplshActivity.class));
                }else{
                    if(User.getCurrentUser(User.class)==null){
                        startActivity(new Intent(LogoActivity.this,LoginActivity.class));
                    }else{
                        startActivity(new Intent(LogoActivity.this,MainActivity.class));
                    }
                }
                finish();
            }
        },2000);


    }
}
