package com.jinhanyu.jack.faceme;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anzhuo on 2016/11/4.
 */

public class SelectableFaceMePopupWindow {


    private TextView tv_title;
    private PopupWindow popupWindow;
    private Activity activity;
    private LinearLayout ll_options;


    public SelectableFaceMePopupWindow(Activity activity) {
        this.activity = activity;
        View contentView = LayoutInflater.from(activity).inflate(R.layout.select_pop, null);
        popupWindow = new PopupWindow(contentView, ScreenUtils.getScreenWidth(activity)*5/6, ViewGroup.LayoutParams.WRAP_CONTENT);

        tv_title = (TextView) contentView.findViewById(R.id.tv_title);
        ll_options = (LinearLayout) contentView.findViewById(R.id.ll_options);

    }


    public SelectableFaceMePopupWindow setTitle(CharSequence title) {
        tv_title.setText(title);
        return this;
    }

    public SelectableFaceMePopupWindow addOptionDanger(String option, final View.OnClickListener callback) {
        View view = LayoutInflater.from(activity).inflate(R.layout.select_pop_item, null);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(option);
        textView.setTextColor(Color.RED);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(v);
                popupWindow.dismiss();
            }
        });
        ll_options.addView(view);
        return this;
    }

    public SelectableFaceMePopupWindow addOption(String option) {
        View view = LayoutInflater.from(activity).inflate(R.layout.select_pop_item, null);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(option);
        ll_options.addView(view);
        return this;
    }

    public SelectableFaceMePopupWindow addOption(String option, final View.OnClickListener callback) {
        View view = LayoutInflater.from(activity).inflate(R.layout.select_pop_item, null);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(option);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(v);
                popupWindow.dismiss();
            }
        });
        ll_options.addView(view);
        return this;
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    public void show(View view) {
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        popupWindow.setAnimationStyle(R.style.FaceMe_PopupWindow_menu);
        backgroundAlpha(0.5f);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }


}
