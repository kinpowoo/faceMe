package com.jinhanyu.jack.faceme;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by anzhuo on 2016/10/19.陈礼(下拉刷新)
 */
public class Ptr_refresh extends FrameLayout implements PtrUIHandler {
    private ImageView iv_icon;
    private TextView tv_hint;
    private AnimationDrawable animationDrawable;


    public Ptr_refresh(Context context) {
        this(context, null);
    }

    public Ptr_refresh(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Ptr_refresh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    //控件初始化
    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.ptr_refresh, this);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        tv_hint = (TextView) view.findViewById(R.id.tv_hint);
        animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.ptr_animation);
    }


    @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        iv_icon.setImageResource(R.mipmap.z_arrow_down);
        tv_hint.setText("下拉刷新");
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        iv_icon.setImageDrawable(animationDrawable);
        animationDrawable.start();
        tv_hint.setText("正在加载...");
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        iv_icon.setImageDrawable(animationDrawable);
        animationDrawable.stop();
        tv_hint.setText("加载完成");
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        int otr = frame.getOffsetToRefresh();
        int current = ptrIndicator.getCurrentPosY();
        int last = ptrIndicator.getLastPosY();
        if (current < otr && last >= otr) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                tv_hint.setText("下拉刷新");
                iv_icon.setImageResource(R.mipmap.z_arrow_down);
            }
        } else if (current > otr && last <= otr) {
            tv_hint.setText("松开刷新");
            iv_icon.setImageResource(R.mipmap.z_arrow_up);
        }
    }

}
