package com.jinhanyu.jack.faceme;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by anzhuo on 2016/11/8.
 */

public class FaceMePopupMenu extends PopupWindow {
    private View.OnClickListener onConfirmListener;
    private View.OnClickListener onCancelListener;
    private PopupWindow popupWindow;
    private Activity activity;
    private Float alpha=1.0f;

    private Handler mHandler=new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    backgroundAlpha((float)msg.obj);
                    break;
            }
        }
    };

    public FaceMePopupMenu setOnConfirmListener(View.OnClickListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;   //如果设置确定监听，就实例化确定监听对象
        return this;
    }
    public FaceMePopupMenu setOnCancelListener(View.OnClickListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return this;
    }

    public FaceMePopupMenu(Activity activity) {
        this.activity  =  activity;
        View contentView = LayoutInflater.from(activity).inflate(R.layout.photo_download,null);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        View bt_confirm =  contentView.findViewById(R.id.tv_download_photo_save);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("FaceMePopupWindow","bt_confirm clicked");
                if(onConfirmListener!=null){
                    onConfirmListener.onClick(v);            //如果不为空，调用确定监听对象的.onClick（）方法
                }
                popupWindow.dismiss();
            }
        });
        View bt_cancel =  contentView.findViewById(R.id.tv_download_photo_cancel);
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
    }
    private void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp =activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    public void show(View view){
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //添加弹出、弹入的动画
        popupWindow.setAnimationStyle(R.style.PopupWindow_menu);
        popupWindow.showAtLocation(view,Gravity.BOTTOM,0,0);
        /**
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.BOTTOM,0,-location[1]);
         */
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //此处while的条件alpha不能<= 否则会出现黑屏
                        while (alpha < 1f) {
                            try {
                                Thread.sleep(4);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Message msg = mHandler.obtainMessage();
                            msg.what = 1;
                            alpha += 0.01f;
                            msg.obj = alpha;
                            mHandler.sendMessage(msg);
                        }
                    }

                }).start();
            }
        });
        popupWindow.setAnimationStyle(R.style.FaceMe_PopupWindow_menu);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (alpha > 0.5f) {
                    try {
                        //4是根据弹出动画时间和减少的透明度计算
                        Thread.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    //每次减少0.01，精度越高，变暗的效果越流畅
                    alpha -= 0.01f;
                    msg.obj = alpha;
                    mHandler.sendMessage(msg);
                }
            }

        }).start();
    }


}
