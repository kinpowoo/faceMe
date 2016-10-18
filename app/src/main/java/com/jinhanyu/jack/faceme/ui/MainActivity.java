package com.jinhanyu.jack.faceme.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.SingleFragment;


/**
 * Created by anzhuo on 2016/10/18.
 */
public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    private RadioGroup radioGroup;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Fragment mCurrentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mCurrentFragment=SingleFragment.getMainFragment();
        radioGroup= (RadioGroup) findViewById(R.id.rg_mainActivity);
        radioGroup.setOnCheckedChangeListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        fragmentManager=getSupportFragmentManager();
        transaction=fragmentManager.beginTransaction();
        transaction.add(R.id.ll_mainActivity,SingleFragment.getMainFragment());
        transaction.commit();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        fragmentManager=getSupportFragmentManager();
        transaction=fragmentManager.beginTransaction();
        switch (checkedId){
            case R.id.rb_mainActivity_mainFragment:
                transaction.replace(R.id.ll_mainActivity,SingleFragment.getMainFragment());
                break;
            case R.id.rb_mainActivity_flowFragment:
                transaction.replace(R.id.ll_mainActivity,SingleFragment.getFlowFragment());
                break;
            case R.id.rb_mainActivity_postFragment:
                transaction.replace(R.id.ll_mainActivity,SingleFragment.getPostFragment());
                break;
            case R.id.rb_mainActivity_favoriteFragment:
                transaction.replace(R.id.ll_mainActivity,SingleFragment.getFavoriteFragment());
                break;
            case R.id.rb_mainActivity_userFragment:
                transaction.replace(R.id.ll_mainActivity,SingleFragment.getUserFragment());
                break;
        }
        transaction.commit();
    }

}
