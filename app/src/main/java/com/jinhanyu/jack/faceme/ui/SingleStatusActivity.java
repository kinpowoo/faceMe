package com.jinhanyu.jack.faceme.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteBatchListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by jianbo on 2016/10/21.
 */
public class SingleStatusActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView back,refresh,option,favoriteIcon,commentIcon,shareIcon;
    private SimpleDraweeView userPortrait,statusPhoto;
    private TextView favoriteNum,textBy,text,commentNum,postTime,username;
    private String statusId;
    private Status status;
    private List<Status> statusList;
    private View menuView;
    private TextView delete,edit,cancel,share;
    private User currentUser=Utils.getCurrentUser();
    private PopupWindow popupWindow;
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
    private AlertDialog.Builder dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_status_activity);
        back= (ImageView) findViewById(R.id.iv_single_status_back);
        refresh= (ImageView) findViewById(R.id.iv_single_status_refresh);
        option= (ImageView) findViewById(R.id.iv_single_status_option);
        favoriteIcon= (ImageView) findViewById(R.id.iv_single_status_favorite);
        commentIcon= (ImageView) findViewById(R.id.iv_single_status_comment);
        shareIcon= (ImageView) findViewById(R.id.iv_single_status_share);
        userPortrait= (SimpleDraweeView) findViewById(R.id.sdv_single_status_userPortrait);
        statusPhoto= (SimpleDraweeView) findViewById(R.id.iv_single_status_photo);
        favoriteNum= (TextView) findViewById(R.id.tv_single_status_favoriteNum);
        textBy= (TextView) findViewById(R.id.tv_single_status_textBy);
        text= (TextView) findViewById(R.id.tv_single_status_text);
        commentNum= (TextView) findViewById(R.id.tv_single_status_commentNum);
        postTime= (TextView) findViewById(R.id.tv_single_status_postTime);
        username= (TextView) findViewById(R.id.tv_single_status_username);

        menuView= LayoutInflater.from(this).inflate(R.layout.single_status_option_menu,null);
        delete= (TextView) menuView.findViewById(R.id.tv_single_status_option_menu_delete);
        edit= (TextView) menuView.findViewById(R.id.tv_single_status_option_menu_edit);
        share= (TextView) menuView.findViewById(R.id.tv_single_status_option_menu_share);
        cancel= (TextView) menuView.findViewById(R.id.tv_single_status_option_menu_cancel);


        userPortrait.setOnClickListener(this);
        username.setOnClickListener(this);
        back.setOnClickListener(this);
        refresh.setOnClickListener(this);
        option.setOnClickListener(this);
        favoriteIcon.setOnClickListener(this);
        commentIcon.setOnClickListener(this);
        shareIcon.setOnClickListener(this);
        favoriteNum.setOnClickListener(this);
        text.setOnClickListener(this);
        commentNum.setOnClickListener(this);

        delete.setOnClickListener(this);
        edit.setOnClickListener(this);
        share.setOnClickListener(this);
        cancel.setOnClickListener(this);

        statusId=getIntent().getStringExtra("statusId");
        if(statusId!=null){
            BmobQuery<Status> query=new BmobQuery<>();
            query.include("author");
            query.getObject(statusId, new QueryListener<Status>() {
                @Override
                public void done(Status st, BmobException e) {
                       if(e==null){
                           status=st;
                           fillData(st);
                       }
                }
            });
        }

    }

    public void fillData(Status st){
        userPortrait.setImageURI(Uri.parse(st.getAuthor().getPortrait()));
        username.setText(st.getAuthor().getUsername());
        statusPhoto.setImageURI(Uri.parse(st.getPhoto()));
        statusList= Utils.getCurrentUserLikes();
        if(statusList!=null){
            if(statusList.contains(status)){
                favoriteIcon.setImageResource(R.drawable.favorite_red);
            }else {
                favoriteIcon.setImageResource(R.drawable.favorite_light);
            }
        }
        favoriteNum.setText(st.getLikesNum()+"个赞");
        textBy.setText(st.getAuthor().getUsername());
        text.setText(st.getText());
        commentNum.setText("查看所有"+st.getCommentsNum()+"条评论");
        postTime.setText(Utils.calculTime(st.getCreatedAt()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_single_status_comment
                    |R.id.tv_single_status_commentNum
                    |R.id.tv_single_status_text:
                Intent intent=new Intent(this, CommentActivity.class);
                intent.putExtra("statusId",status.getObjectId());
                startActivity(intent);
                break;
            case R.id.iv_single_status_share:
                break;
            case R.id.tv_single_status_favoriteNum:
                Intent intent2=new Intent(this, LikesActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("type","status");
                bundle.putString("statusId",statusId);
                intent2.putExtras(bundle);
                startActivity(intent2);
                break;
            case R.id.iv_single_status_back:
                finish();
                break;
            case R.id.iv_single_status_favorite:
                if(statusList!=null){
                    BmobRelation relation=new BmobRelation();
                    if(statusList.contains(status)){
                        relation.remove(status);
                        currentUser.setLikes(relation);
                        currentUser.increment("likesNum",-1);
                        currentUser.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    favoriteIcon.setImageResource(R.drawable.favorite_light);
                                }
                            }
                        });

                        relation.setObjects(null);
                        relation.remove(currentUser);
                        status.setLikes(relation);
                        status.increment("likesNum",-1);
                        status.update();
                    }else {
                        relation.add(status);
                        currentUser.setLikes(relation);
                        currentUser.increment("likesNum",1);
                        currentUser.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    favoriteIcon.setImageResource(R.drawable.favorite_red);
                                }
                            }
                        });

                        relation.setObjects(null);
                        relation.add(currentUser);
                        status.setLikes(relation);
                        status.increment("likesNum",1);
                        status.update();
                    }
                }
                break;
            case R.id.sdv_single_status_userPortrait:
                Intent intent1=new Intent(this,UserProfileActivity.class);
                intent1.putExtra("userId",status.getAuthor().getObjectId());
                startActivity(intent1);
                break;
            case R.id.tv_single_status_username:
                Intent intent3=new Intent(this,UserProfileActivity.class);
                intent3.putExtra("userId",status.getAuthor().getObjectId());
                startActivity(intent3);
                break;
            case R.id.iv_single_status_option:
//                if(status.getAuthor()==Utils.getCurrentUser()) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        return;
                    } else {
                        popupWindow = new PopupWindow(menuView, ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        //点击空白处时，隐藏掉pop窗口
                        popupWindow.setFocusable(true);
                        popupWindow.setBackgroundDrawable(new BitmapDrawable());
                        //添加弹出、弹入的动画
                        popupWindow.setAnimationStyle(R.style.PopupWindow_menu);
                        int[] location = new int[2];
                        option.getLocationOnScreen(location);
                        popupWindow.showAtLocation(option, Gravity.LEFT | Gravity.BOTTOM, 0, -location[1]);
                        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
                        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                popupWindow.dismiss();
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
//                }
                break;
            case R.id.iv_single_status_refresh:
                fillData(status);
                break;
            case R.id.tv_single_status_option_menu_delete:
                    dialog=new AlertDialog.Builder(this);
                    dialog.setTitle("确定删除?");
                    dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          if(statusList.contains(status)){
                             currentUser.increment("likesNum",-1);
                             currentUser.update();
                          }
                            status.delete(statusId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                    status.getAuthor().increment("statusesNum",-1);
                                    status.getAuthor().update();
                                        finish();
                                    }
                                }
                            });
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                dialog.show();
                break;

            case R.id.tv_single_status_option_menu_cancel:
                popupWindow.dismiss();
                break;
            case R.id.tv_single_status_option_menu_edit:
                Intent intent4=new Intent(this,EditStatusActivity.class);
                intent4.putExtra("statusId",status.getObjectId());
                startActivity(intent4);
                break;
        }
    }

    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
}
