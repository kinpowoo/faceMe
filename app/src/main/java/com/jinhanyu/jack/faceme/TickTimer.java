package com.jinhanyu.jack.faceme;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anzhuo on 2016/9/20.
 */
public class TickTimer extends Timer {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (totalTime == 0) {     //时间到还未投票就直接谁都不投，发消息给服务器
                cancel();
                onTimeEnd();
            } else {
                timeLabel.setText(totalTime-- + "s后重新获取");
            }
        }
    };

    protected void onTimeEnd() {

    }


    private int totalTime;
    private TextView timeLabel;

    public TickTimer(TextView timeLabel, int totalTime) {
        this.totalTime = totalTime;
        this.timeLabel = timeLabel;
    }


    public void startTick() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        schedule(task, 0, 1000);
    }
}
