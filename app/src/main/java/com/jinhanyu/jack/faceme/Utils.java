package com.jinhanyu.jack.faceme;

import com.jinhanyu.jack.faceme.entity.Status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by jianbo on 2016/10/19.
 */
public class Utils {

    public static String calculTime(String time){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date= null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long postTime=date.getTime();
        long now=System.currentTimeMillis();
        long diff=(now-postTime)/1000;
        if(diff<60){
            return "发布于"+diff+"秒前";
        }else if(diff<60*60){
            return "发布于"+Math.floor(diff/60)+"分钟前";
        }else if(diff<3600*24){
            return "发布于"+Math.floor(diff/3600)+"小时前";
        }else if(diff<3600*24*365){
            return "发布于"+date.getMonth()+1+"-"+date.getDate();
        }else {
            return "发布于"+date.getYear()+"-"+date.getMonth()+1+"-"+date.getDate();
        }
    }


}
