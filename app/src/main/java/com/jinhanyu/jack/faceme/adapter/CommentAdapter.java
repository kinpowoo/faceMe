package com.jinhanyu.jack.faceme.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.ScreenUtils;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.Comment;
import com.jinhanyu.jack.faceme.ui.UserProfileActivity;

import java.text.ParseException;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by jianbo on 2016/10/20.
 */
public class CommentAdapter extends CommonAdapter<Comment>{
    private float downX;
    private float afterMoveX;
    private ImageView deleteButton;
    private View itemView;
    private Animation animation;
    private int START_OFF=0;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            View topLayer= (View) msg.obj;
            int dis=msg.what;
            topLayer.layout(START_OFF-dis,topLayer.getTop(),
                    ScreenUtils.getScreenWidth(context)-dis,topLayer.getBottom());
            itemView.invalidate();
        }
    };
    public CommentAdapter(List<Comment> data, Context context) {
        super(data, context);
        animation= AnimationUtils.loadAnimation(context, R.anim.push_out);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolderForComment viewHold;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.comment_list_item, null);
            viewHold = new ViewHolderForComment();
            viewHold.userPortrait = (SimpleDraweeView) view.findViewById(R.id.sdv_comment_item_userPortrait);
            viewHold.username = (TextView) view.findViewById(R.id.tv_comment_item_username);
            viewHold.commentContent = (TextView) view.findViewById(R.id.tv_comment_item_content);
            viewHold.postTime = (TextView) view.findViewById(R.id.tv_comment_item_postTime);
            viewHold.delete= (ImageView) view.findViewById(R.id.iv_comment_item_delete);
            viewHold.topLayer= (RelativeLayout) view.findViewById(R.id.rl_comment_item_topLayer);
            view.setTag(viewHold);
        } else {
            viewHold = (ViewHolderForComment) view.getTag();
        }
       final Comment comment = data.get(position);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.i("sha","down监听被触发");
                        downX=event.getX();
                        afterMoveX=event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        final ViewHolderForComment holder = (ViewHolderForComment) v.getTag();
                        RelativeLayout top=holder.topLayer;
                        Log.i("sha","move监听被触发");
                        deleteButton=holder.delete;
                        itemView = v; // 得到itemView，在上面加动画
                        holder.delete.setEnabled(false);
                        afterMoveX=event.getX();
                        int dis=(int)(downX-afterMoveX);
                        if(dis>150||dis<-30){
                         if(dis>150){
                             top.layout(START_OFF-160,top.getTop(),
                                    ScreenUtils.getScreenWidth(context)-160,top.getBottom());
                            holder.delete.setEnabled(true);
                         }else {
                             top.layout(START_OFF,top.getTop(),
                                   ScreenUtils.getScreenWidth(context),top.getBottom());
                             holder.delete.setEnabled(false);
                         }

                        }else {
                            Message message=new Message();
                            message.obj=top;
                            message.what=dis;
                            handler.sendMessage(message);
                        }

//                        if(dis>30){
//                            top.layout(START_OFF-160,top.getTop(),
//                                    ScreenUtils.getScreenWidth(context)-160,top.getBottom());
//                            holder.delete.setEnabled(true);
//                        }
//                        if(dis<-30){
//                            top.layout(START_OFF,top.getTop(),
//                                    ScreenUtils.getScreenWidth(context),top.getBottom());
//                            holder.delete.setEnabled(false);
//                        }
                        return true;
                }
                return false;
            }
        });

        viewHold.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    deleteButton.setVisibility(View.GONE);  //点击删除按钮后，影藏按钮
                    deleteItem(itemView, position);   //删除数据，加动画

            }
        });



        viewHold.userPortrait.setImageURI(comment.getCommentor().getPortrait().getUrl());
        viewHold.username.setText(comment.getCommentor().getUsername());
        try {
            viewHold.postTime.setText(Utils.calculTime(comment.getCreatedAt()));
        } catch (ParseException e) {e.printStackTrace();

        }
        viewHold.commentContent.setText(comment.getText());


        viewHold.userPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (afterMoveX == downX) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("userId", comment.getCommentor().getObjectId());
                    context.startActivity(intent);
                }
            }
        });
        viewHold.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (afterMoveX == downX) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("userId", comment.getCommentor().getObjectId());
                    context.startActivity(intent);
                }
            }
        });
        viewHold.commentContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (afterMoveX == downX) {
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.putExtra("userId", comment.getCommentor().getObjectId());
                    context.startActivity(intent);
                }
            }
        });
        return view;
    }

    public void deleteItem(View v,final int position){
        v.startAnimation(animation);  //给view设置动画
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) { //动画执行完毕
                Comment comment=new Comment();
                comment.delete(data.get(position).getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        data.remove(position);  //把数据源里面相应数据删除
                        notifyDataSetChanged();
                    }
                });

            }
        });


    }
    private void doAnimation(final View v, final float dis){
        ValueAnimator animator = ValueAnimator.ofFloat(0,dis);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  //添加监听器addUpdateListener(AnimatorUpdateListener listener)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curValue = (float)animation.getAnimatedValue();      //得到当前运动位置的值,如果是ofInt(),强转成int,如果是float,强转成flaot
                v.layout((int)(curValue+dis),v.getHeight(),v.getWidth(),v.getHeight());  //通过tv的layout函数来永久改变位置
//                layout(left(左边起始位置),top(上边起始位置),right,bottom);
            }
        });
        animator.start();
        v.invalidate();
        if(-(dis)>45){
            animator.cancel();
        }

    }

    class ViewHolderForComment {
        protected SimpleDraweeView userPortrait;
        protected TextView username, commentContent, postTime;
        protected ImageView delete;
        protected RelativeLayout topLayer;
    }
}

