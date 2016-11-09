package com.jinhanyu.jack.faceme;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.entity.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jianbo on 2016/10/19.
 */
public class Utils {
    private static User currentUser = User.getCurrentUser(User.class);
    private static ExecutorService threadPool= Executors.newFixedThreadPool(1);

    public static String calculTime(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(time);
            long postTime = date.getTime();
            long now = System.currentTimeMillis();
            long diff = (now - postTime) / 1000;
            if (diff < 60) {
                return "发布于" + diff + "秒前";
            } else if (diff < 60 * 60) {
                return "发布于" + ((int)(diff / 60)) + "分钟前";
            } else if (diff < 3600 * 24) {
                return "发布于" + ((int)(diff / 3600)) + "小时前";
            } else if (diff < 3600 * 24 * 365) {
                return "发布于" + (date.getMonth() + 1) + "-" + date.getDate();
            } else {
                return "发布于" + date.getYear() + "-" + (date.getMonth() + 1)+ "-" + date.getDate();
            }
    }



    public static Date parseDate(String time){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date= null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static User getCurrentUser(){
        return currentUser;
    }

    public static void setETColor(String str ,int start ,int end , int color , EditText tv){
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        builder.setSpan(new ForegroundColorSpan(color),start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(builder);
    }
    public static void setTVColor(String str ,int start ,int end , int color , TextView tv){
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        builder.setSpan(new ForegroundColorSpan(color),start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(builder);
    }

    public static float changToTwoDecimal(float in) {
        DecimalFormat df = new DecimalFormat("0.0");
        String out = df.format(in);
        float result = Float.parseFloat(out);
        return result;
    }


    public static void downPic(final String url, final Handler handler){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL picUrl=new URL(url);
                    HttpURLConnection conn=(HttpURLConnection)picUrl.openConnection();
                    if(conn.getResponseCode()==200) {
                        InputStream in = conn.getInputStream();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        int length;
                        byte[] buff = new byte[1024 * 2];
                        while ((length = in.read(buff)) != -1) {
                            outputStream.write(buff, 0, length);
                        }
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");//获取当前时间，进一步转化为字符串
                        String fileName = format.format(new Date());
                        File dest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download");
                        if (!dest.exists()) {
                            dest.mkdirs();
                        }
                        File loc=new File(dest, fileName + ".jpg");
                        OutputStream out = new FileOutputStream(loc);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.toByteArray().length);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        Message message=new Message();
                        handler.obtainMessage(1,loc.getAbsolutePath());
                        message.sendToTarget();
                    }
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
