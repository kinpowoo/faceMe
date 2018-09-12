package com.jinhanyu.jack.faceme.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.jinhanyu.jack.faceme.FaceMePopupWindow;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.User;

import cn.bmob.v3.BmobUser;

import static com.jinhanyu.jack.faceme.R.id.tv_settings_logOut;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView back;
    private TextView editProfile,changePass,clearCache,logOut,feedback,about;
    private Drawable drawable;
    private Drawable leftDrawable,rightDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        leftDrawable=getResources().getDrawable(R.drawable.cache);
        leftDrawable.setBounds(0, 0,leftDrawable.getMinimumWidth(),leftDrawable.getMinimumHeight());
        rightDrawable=getResources().getDrawable(R.drawable.enter);
        rightDrawable.setBounds(0,0,rightDrawable.getMinimumWidth(),rightDrawable.getMinimumHeight());
        back= (ImageView) findViewById(R.id.iv_settings_back);
        editProfile= (TextView) findViewById(R.id.tv_settings_editProfile);
        changePass= (TextView) findViewById(R.id.tv_settings_changePass);
        logOut= (TextView) findViewById(tv_settings_logOut);
        clearCache= (TextView) findViewById(R.id.tv_settings_clearCache);
        feedback= (TextView) findViewById(R.id.tv_settings_feedback);
        about= (TextView) findViewById(R.id.tv_settings_about);

        showCacheSize();

        back.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        changePass.setOnClickListener(this);
        logOut.setOnClickListener(this);
        clearCache.setOnClickListener(this);
        feedback.setOnClickListener(this);
        about.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_settings_back:
                finish();
                break;
            case R.id.tv_settings_editProfile:
                Intent intent=new Intent(this,EditProfileActivity.class);
                startActivity(intent);
            break;
            case R.id.tv_settings_changePass:
                Intent intent2=new Intent(this,ResetPassActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_settings_clearCache:
                drawable = getResources().getDrawable(R.drawable.ptr_animation);
                drawable.setBounds(0, 0,50,50); //设置边界
                clearCache.setCompoundDrawables(leftDrawable,null,drawable,null);//画在右边
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                imagePipeline.clearCaches();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        showCacheSize();
                        clearCache.setCompoundDrawables(leftDrawable,null,rightDrawable,null);
                    }
                }, 2000);
                break;

            case R.id.tv_settings_logOut:
                final FaceMePopupWindow popupWindow=new FaceMePopupWindow(this);
                popupWindow.setTitle("是否退出登录？");
                popupWindow.setOnConfirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User.logOut();   //清除缓存用户对象
//                        BmobUser currentUser = User.getCurrentUser(); // 现在的currentUser是null了
                        Intent intent1=new Intent(SettingActivity.this,LoginActivity.class);
                        startActivity(intent1);
                    }
                });
                popupWindow.show(logOut);
                break;
            case R.id.tv_settings_feedback:
                startActivity(new Intent(this,FeedBackActivity.class));
                break;
            case R.id.tv_settings_about:
                startActivity(new Intent(this,AboutActivity.class));
                break;
        }
    }

    private void showCacheSize(){
        long cacheSize = Fresco.getImagePipelineFactory().getMainFileCache().getSize();
        if(cacheSize<=0){
            clearCache.setText("0.0B");
        }else{
            float cacheSizeTemp1 = Utils.changToTwoDecimal(Math.round(cacheSize / 1024));
            float cacheSizeTemp2 = Utils.changToTwoDecimal(Math.round((cacheSize/1024)/1024));
            if(cacheSizeTemp1<1){
                clearCache.setText(cacheSize+"B");
            }else if(((cacheSizeTemp1>=1)&&(cacheSizeTemp2<1))){
                clearCache.setText(cacheSizeTemp1+"KB");
            }else if(cacheSizeTemp2>=1){
                clearCache.setText(cacheSizeTemp2+"MB");
            }
        }
    }
}
