package com.jinhanyu.jack.faceme.cutsom_view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.R;


/**
 * Created by DeskTop29 on 2017/11/22.
 */

public class VerticalLoading {
    ObjectAnimator animator;
    AlertDialog alertDialog;
    private TextView notifyMsg;
    private ImageView loading;

    String message;
    Context context;


    public VerticalLoading(Context context){
        this.context = context;
        alertDialog = new AlertDialog.Builder(context).create();
    }


    public VerticalLoading(Context context, String notifyMessage) {
        alertDialog = new AlertDialog.Builder(context).create();
        this.message = notifyMessage;
    }


    private void startAnim(final View v){
        animator= ObjectAnimator.ofFloat(v,"rotation",360.0f);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(2000);
            animator.setRepeatCount(-1);
            animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
    }

    private void stopAnim(){
        if(animator!=null&&animator.isRunning()){
            if(context!=null) {
                Looper looper = context.getMainLooper();
                looper.prepareMainLooper();
                animator.cancel();
                looper.loop();
            }
        }
    }


    public boolean isShowing(){
        if(alertDialog!=null) {
            return alertDialog.isShowing();
        }
        return false;
    }

    public VerticalLoading setMessage(CharSequence msg){
        message = msg.toString();
        return this;
    }


    public void show(){
        if(alertDialog!=null){
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setContentView(R.layout.vertical_loading_dialog);
            window.setBackgroundDrawable(new ColorDrawable());  //消除window默认的阴影
            window.setGravity(Gravity.CENTER);
            window.setLayout(240, LinearLayout.LayoutParams.WRAP_CONTENT);
            notifyMsg = (TextView) window.findViewById(R.id.alert_message);
            notifyMsg.setTextColor(Color.BLACK);
            notifyMsg.setText(message);
            loading = (ImageView) window.findViewById(R.id.logo);
            alertDialog.setCancelable(false);
            startAnim(loading);
        }
    }


    public void setOutsideTouchable(boolean flag){
        alertDialog.setCanceledOnTouchOutside(flag);
    }


    public void dismiss(){
        if(alertDialog!=null&&alertDialog.isShowing()) {
            alertDialog.dismiss();
            stopAnim();
        }
    }

}
