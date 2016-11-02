package com.jinhanyu.jack.faceme;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by jianbo on 2016/10/19.
 */
public class Utils {
    private static User currentUser = User.getCurrentUser(User.class);

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
                return "发布于" + date.getMonth() + 1 + "-" + date.getDate();
            } else {
                return "发布于" + date.getYear() + "-" + date.getMonth() + 1 + "-" + date.getDate();
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



}
