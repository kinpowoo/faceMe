package com.jinhanyu.jack.faceme;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by anzhuo on 2016/11/4.
 */

public class FaceMePopupWindow{

    private View.OnClickListener onConfirmListener;
    private View.OnClickListener onCancelListener;
    private TextView tv_title;
    private TextView tv_message;
    private LinearLayout ll_message;
    private PopupWindow popupWindow;
    private Activity activity;
    private View bt_confirm;
    private View bt_cancel;

    public FaceMePopupWindow setOnConfirmListener(View.OnClickListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
        return this;
    }
    public FaceMePopupWindow setOnCancelListener(View.OnClickListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return this;
    }

    public FaceMePopupWindow(Activity activity) {
        this.activity  =  activity;
        View contentView = LayoutInflater.from(activity).inflate(R.layout.popw_util,null);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        bt_confirm =  contentView.findViewById(R.id.tv_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("FaceMePopupWindow","bt_confirm clicked");
                if(onConfirmListener!=null){
                    onConfirmListener.onClick(v);
                }
                popupWindow.dismiss();
            }
        });
        bt_cancel =  contentView.findViewById(R.id.tv_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("FaceMePopupWindow","bt_cancel clicked");
                if(onCancelListener!=null){
                    onCancelListener.onClick(v);
                }
                popupWindow.dismiss();
            }
        });
        tv_title = (TextView) contentView.findViewById(R.id.tv_title);
        tv_message = (TextView) contentView.findViewById(R.id.tv_message);
        ll_message = (LinearLayout) contentView.findViewById(R.id.ll_message);

    }


    public FaceMePopupWindow setTitle(CharSequence title){
        tv_title.setText(title);
        return this;
    }

    public FaceMePopupWindow setMessage(CharSequence message){
        tv_message.setText(message);
        return this;
    }

    public FaceMePopupWindow hideConfirmButton(){
        bt_confirm.setVisibility(View.GONE);
        return this;
    }
    public FaceMePopupWindow hideCancelButton(){
        bt_cancel.setVisibility(View.GONE);
        return this;
    }

    private void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp =activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    public void setOutsideTouchable(boolean flag){
        popupWindow.setOutsideTouchable(flag);
        if(flag) {
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
    }
    public void show(View view){
        if(TextUtils.isEmpty(tv_message.getText().toString())){
            ll_message.setVisibility(View.GONE);
        }
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        popupWindow.setAnimationStyle(R.style.FaceMe_PopupWindow_menu);
        backgroundAlpha(0.5f);
        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
    }

    public void dismiss(){
        popupWindow.dismiss();
    }

}
