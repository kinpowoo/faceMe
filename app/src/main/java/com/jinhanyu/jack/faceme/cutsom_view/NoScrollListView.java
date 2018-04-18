package com.jinhanyu.jack.faceme.cutsom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by DeskTop29 on 2017/11/29.
 */

public class NoScrollListView extends ListView {
        public NoScrollListView(Context context, AttributeSet attrs) {
            super(context,attrs);
        }
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
            int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, mExpandSpec);
        }
}
