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
    private static List<Status> statusList;

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

   //动态计算嵌套在scrollview里的listview的高度
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if(listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    //得到当前用户的收藏列表
    public static List<Status> getCurrentUserLikes(){
        BmobQuery<Status> query=new BmobQuery<>();
        query.addWhereRelatedTo("likes",new BmobPointer(currentUser));
        query.include("author");
        query.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> list, BmobException e) {
                  if(e==null){
                      statusList=list;

                  }
            }
        });
        return statusList;
    }

}
