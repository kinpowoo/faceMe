package com.jinhanyu.jack.faceme;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.bmob.v3.Bmob;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.smssdk.SMSSDK;
import okhttp3.OkHttpClient;

import java.security.cert.X509Certificate;
import java.util.zip.ZipException;

/**
 * Created by anzhuo on 2016/10/19.
 */
public class MainApplication extends MultiDexApplication {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(getApplicationContext(), "5d2d5b54dd7f058e10d6a2792f7f1eb8");
        mContext = this;


        OkHttpClient okHttpClient=getUnsafeOkHttpClient();  // build on your own
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, okHttpClient)
                .build();

        Fresco.initialize(this,config);

        ShareSDK.initSDK(this);
        oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("FaceMe");


        //Mob 短信验证码
        SMSSDK.initSDK(this, "18c6f00007aee", "f25f6453f8ecd8b10795865b2e77b770");

    }


    public static Context getContext() {
        return mContext;
    }


    private static OnekeyShare oks;

    public static void showShareRemote(Context context, String photoPath, String text) {
        // 启动分享GUI
        oks.setText(text);
        oks.setImagePath(null);
        oks.setImageUrl(photoPath);//确保SDcard下面存在此张图片
        oks.show(context);
    }


    public static void showShare(Context context, String photoPath, String text) {
        // 启动分享GUI
        oks.setText(text);
        oks.setImageUrl(null);
        oks.setImagePath(photoPath);//确保SDcard下面存在此张图片
        oks.show(context);
    }



    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }

                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }
            }};


            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    }).build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
