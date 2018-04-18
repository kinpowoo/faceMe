package com.jinhanyu.jack.faceme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anzhuo on 2016/11/1.
 */

public class GuideActivity extends AppCompatActivity {
    private ViewPager flipper;
    private TextView title;
    private RadioGroup rg;
    private Animation animation;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_activity);

        getSharedPreferences("logo",MODE_APPEND).edit().putBoolean("first",false).commit();
        animation = AnimationUtils.loadAnimation(GuideActivity.this, R.anim.animationset);
        flipper = (ViewPager) this.findViewById(R.id.ViewFlipper1);
        rg = (RadioGroup) findViewById(R.id.rg);
        title = (TextView) findViewById(R.id.title);
        final List<ImageView> viewList = new ArrayList<>();
        viewList.add(addImageView(R.mipmap.init_page1));
        viewList.add(addImageView(R.mipmap.init_page2));

        flipper.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewList.get(position));
            }
        });


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                title.setVisibility(View.INVISIBLE);
                title.clearAnimation();
                animation.cancel();
                switch (checkedId) {
                    case R.id.rb0:
                        flipper.setCurrentItem(0);
                        break;
                    case R.id.rb1:
                        flipper.setCurrentItem(1);
                        title.setVisibility(View.VISIBLE);
                        title.startAnimation(animation);
                        break;

                }
            }
        });
        flipper.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                title.setVisibility(View.INVISIBLE);
                title.clearAnimation();
                switch (position) {
                    case 0:
                        rg.check(R.id.rb0);
                        break;
                    case 1:
                        rg.check(R.id.rb1);
                        title.setVisibility(View.VISIBLE);
                        title.startAnimation(animation);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TranslateAnimation translateAnimation = new TranslateAnimation(Animation.START_ON_FIRST_FRAME, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.START_ON_FIRST_FRAME, 0f,
                Animation.RELATIVE_TO_SELF, 1f);//第一个参数是动画最初效果（1f为100%不透明）
                translateAnimation.setDuration(3000);//设置渐变时间 单位是毫秒
                translateAnimation.setInterpolator(new BounceInterpolator());//差值器
                translateAnimation.setFillAfter(true);//设置变化后是否为最终展示（不设置的话会变回最初样子）
                title.startAnimation(translateAnimation);//变化开始
                try {
                    Thread.sleep(1200);
                    startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }

            }
        });

    }

    private ImageView addImageView(int id) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ImageView iv = new ImageView(this);
        iv.setLayoutParams(layoutParams);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setImageResource(id);
        return iv;
    }


}
