package com.jinhanyu.jack.faceme.aidl;

import java.util.Date;

/**
 * Created by DeskTop29 on 2018/10/17.
 */

public interface StatusInterface {
    abstract void updateStatus(int indexPath);
    abstract void deleteStatus(int indexPath);
    abstract void addStatus(Date createDate);
}
