package com.jinhanyu.jack.faceme.tool;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by DeskTop29 on 2018/4/18.
 */

public class ConstantFunc {

    /**
     * 字符串非空判断
     */
    public static boolean isEmpty(Object str) {
        if (str == null || str.equals("") || str.equals("null") || ((String.valueOf(str)).trim().equals(""))) {
            return true;
        }

        return false;
    }


    public static void hint(String tag, String msg) {
        Log.i(tag, msg);
    }



    public static List<String> setToList(Set<String> sets) {
        List<String> times = new ArrayList<>();
        Iterator<String> iterator = sets.iterator();
        while (iterator.hasNext()) {
            String time = iterator.next();
            times.add(time);
        }
        //Collections.sort(times,new TaskInfoBeanDateComparator());
        return times;
    }


    public static Set<String> listToSet(List<String> lists) {
        Set<String> times = new HashSet<>();
        Iterator<String> iterator = lists.iterator();
        while (iterator.hasNext()) {
            String time = iterator.next();
            times.add(time);
        }
        //Collections.sort(times,new TaskInfoBeanDateComparator());
        return times;
    }


}
