package com.jinhanyu.jack.faceme.cutsom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.GridView;

/**
 * Created by DeskTop29 on 2018/4/18.
 */

public class NoScrollGridView extends GridView {
    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
