package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.jinhanyu.jack.faceme.R;

/**
 * Created by anzhuo on 2016/11/3.
 */

public class UserAgreementActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_agreement);
    }

    public void goBack(View view) {
        finish();
    }
}
