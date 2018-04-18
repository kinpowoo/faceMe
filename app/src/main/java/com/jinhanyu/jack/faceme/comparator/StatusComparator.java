package com.jinhanyu.jack.faceme.comparator;

import android.text.TextUtils;

import com.jinhanyu.jack.faceme.entity.Status;

import java.util.Comparator;

/**
 * Created by DeskTop29 on 2018/4/18.
 */

public class StatusComparator implements Comparator<Status> {
    @Override
    public int compare(Status o1, Status o2) {
        if(o1==null||o2==null){
            return 0;
        }
        if(!TextUtils.isEmpty(o1.getUpdatedAt())
                &&!TextUtils.isEmpty(o2.getUpdatedAt())) {
            return Sort.singleSort(o1.getUpdatedAt(), o2.getUpdatedAt());
        }

        return 0;
    }
}
