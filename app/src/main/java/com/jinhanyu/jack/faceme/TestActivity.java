package com.jinhanyu.jack.faceme;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

/**
 * Created by anzhuo on 2016/10/26.
 */
public class TestActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test);
    }

    public void onekeyshare(View v){
         MainApplication.showShare(this);
    }

    public void loginbyqq(View view) {
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.setPlatformActionListener(new PlatformActionListener() {
            public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
            //用户资源都保存到res
            //通过打印res数据看看有哪些数据是你想要的
                if (action == Platform.ACTION_USER_INFOR) {
                    PlatformDb platDB = platform.getDb();//获取数平台数据DB
                    //通过DB获取各种数据
                    String userId = platDB.getUserId();
                    String userGender = platDB.getUserGender();
                    String photoUrl = platDB.getUserIcon();
                    String username = platDB.getUserName();

                    Log.i("info",userGender+","+photoUrl+","+username+","+userId);

                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        qq.showUser(null);//授权并获取用户信息

    }
}
