package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.jinhanyu.jack.faceme.R;


/**
 * Created by anzhuo on 2016/10/18.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private RadioButton rb_mainActivity_mainFragment;
    private RadioButton rb_mainActivity_flowFragment;
    private Button rb_mainActivity_postFragment;
    private RadioButton rb_mainActivity_favoriteFragment;
    private RadioButton rb_mainActivity_userFragment;
    private MainFragment mainFragment;
    private FlowFragment flowFragment;
    private FavoriteFragment favoriteFragment;
    private UserFragment userFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        rb_mainActivity_mainFragment = (RadioButton) findViewById(R.id.rb_mainActivity_mainFragment);
        rb_mainActivity_flowFragment = (RadioButton) findViewById(R.id.rb_mainActivity_flowFragment);
        rb_mainActivity_postFragment = (Button) findViewById(R.id.rb_mainActivity_postFragment);
        rb_mainActivity_favoriteFragment = (RadioButton) findViewById(R.id.rb_mainActivity_favoriteFragment);
        rb_mainActivity_userFragment = (RadioButton) findViewById(R.id.rb_mainActivity_userFragment);


        rb_mainActivity_mainFragment.setOnClickListener(this);
        rb_mainActivity_flowFragment.setOnClickListener(this);
        rb_mainActivity_postFragment.setOnClickListener(this);
        rb_mainActivity_favoriteFragment.setOnClickListener(this);
        rb_mainActivity_userFragment.setOnClickListener(this);
        ShowFragment(0);
    }

    private void ShowFragment(int i) {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        switch (i) {
            case 0:
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                    transaction.add(R.id.ll_mainActivity, mainFragment);
                } else {
                    transaction.show(mainFragment);
                }
                break;
            case 1:
                if (flowFragment == null) {
                    flowFragment = new FlowFragment();
                    transaction.add(R.id.ll_mainActivity, flowFragment);
                } else {
                    transaction.show(flowFragment);
                }
                break;
            case 2:
                if (favoriteFragment == null) {
                    favoriteFragment = new FavoriteFragment();
                    transaction.add(R.id.ll_mainActivity, favoriteFragment);
                } else {
                    transaction.show(favoriteFragment);
                }
                break;
            case 3:
                if (userFragment == null) {
                    userFragment = new UserFragment();
                    transaction.add(R.id.ll_mainActivity, userFragment);
                } else {
                    transaction.show(userFragment);
                }
                break;
        }
        transaction.commit();
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        if (mainFragment != null) {
            transaction.hide(mainFragment);
        }
        if (flowFragment != null) {
            transaction.hide(flowFragment);
        }
        if (favoriteFragment != null) {
            transaction.hide(favoriteFragment);
        }
        if (userFragment != null) {
            transaction.hide(userFragment);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_mainActivity_mainFragment:
                ShowFragment(0);
                break;
            case R.id.rb_mainActivity_flowFragment:
                ShowFragment(1);
                break;
            case R.id.rb_mainActivity_postFragment:
                gototakephoto();
                break;
            case R.id.rb_mainActivity_favoriteFragment:
                ShowFragment(2);
                break;
            case R.id.rb_mainActivity_userFragment:
                ShowFragment(3);
                break;
        }
    }


    //调用手机摄像头
    public void gototakephoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    //重写onActivityResult方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && requestCode == Activity.RESULT_OK) {

        }
    }
}
