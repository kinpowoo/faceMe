package com.jinhanyu.jack.faceme.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.FaceMePopupMenu;
import com.jinhanyu.jack.faceme.MainApplication;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.ScreenUtils;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.Comment;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.text.ParseException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by jianbo on 2016/10/21.
 */
public class SingleStatusActivity extends AppCompatActivity implements View.OnClickListener,View.OnLongClickListener{
    private ImageView back,option,favoriteIcon,commentIcon,shareIcon;
    private SimpleDraweeView userPortrait,statusPhoto;
    private TextView favoriteNum,textBy,text,commentNum,postTime,nickname,location;
    private String statusId;
    private Status status;
    private View menuView;
    private TextView delete,edit,cancel,share,tag_one,tag_two,tag_three;
    private User currentUser=Utils.getCurrentUser();
    private PopupWindow popupWindow;
    private Float alpha=1.0f;
    private LinearLayout.LayoutParams params;

    private Handler mHandler=new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(SingleStatusActivity.this,"下载成功",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
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
        int width= ScreenUtils.getScreenWidth(this);
        params=new LinearLayout.LayoutParams(width,width);

        back= (ImageView) findViewById(R.id.iv_single_status_back);
        option= (ImageView) findViewById(R.id.iv_single_status_option);
        favoriteIcon= (ImageView) findViewById(R.id.iv_single_status_favorite);
        commentIcon= (ImageView) findViewById(R.id.iv_single_status_comment);
        shareIcon= (ImageView) findViewById(R.id.iv_single_status_share);
        userPortrait= (SimpleDraweeView) findViewById(R.id.sdv_single_status_userPortrait);
        statusPhoto= (SimpleDraweeView) findViewById(R.id.iv_single_status_photo);
        tag_one= (TextView) findViewById(R.id.tag_one);
        tag_two= (TextView) findViewById(R.id.tag_two);
        tag_three= (TextView) findViewById(R.id.tag_three);
        statusPhoto.setLayoutParams(params);
        statusPhoto.setScaleType(ImageView.ScaleType.FIT_XY);

        favoriteNum= (TextView) findViewById(R.id.tv_single_status_favoriteNum);
        textBy= (TextView) findViewById(R.id.tv_single_status_textBy);
        text= (TextView) findViewById(R.id.tv_single_status_text);
        commentNum= (TextView) findViewById(R.id.tv_single_status_commentNum);
        postTime= (TextView) findViewById(R.id.tv_single_status_postTime);
        nickname= (TextView) findViewById(R.id.tv_single_status_username);
        location= (TextView) findViewById(R.id.tv_single_status_location);

        menuView= LayoutInflater.from(this).inflate(R.layout.single_status_option_menu,null);
        delete= (TextView) menuView.findViewById(R.id.tv_single_status_option_menu_delete);
        edit= (TextView) menuView.findViewById(R.id.tv_single_status_option_menu_edit);
        share= (TextView) menuView.findViewById(R.id.tv_single_status_option_menu_share);
        cancel= (TextView) menuView.findViewById(R.id.tv_single_status_option_menu_cancel);


        userPortrait.setOnClickListener(this);
        nickname.setOnClickListener(this);
        back.setOnClickListener(this);
        option.setOnClickListener(this);
        favoriteIcon.setOnClickListener(this);
        commentIcon.setOnClickListener(this);
        shareIcon.setOnClickListener(this);
        favoriteNum.setOnClickListener(this);
        text.setOnClickListener(this);
        commentNum.setOnClickListener(this);
        statusPhoto.setOnLongClickListener(this);

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

    public void fillData(final Status st){
        userPortrait.setImageURI(st.getAuthor().getPortrait().getUrl());
        nickname.setText(st.getAuthor().getNickname());
        statusPhoto.setImageURI(st.getPhoto().getUrl());

        BmobQuery<Status> statusQuery = new BmobQuery<>();
        BmobQuery<User> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId",st.getObjectId());
        statusQuery.addWhereMatchesQuery("likes", "_User", innerQuery);
        statusQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> list, BmobException e) {
              for(Status status1:list){
                  if(status1.getObjectId().equals(st.getObjectId())){
                      status.setFavoritedByMe2(true);
                  }
              }
                if(status.isFavoritedByMe2()){
                    favoriteIcon.setImageResource(R.drawable.favorite_red);
                }else {
                    favoriteIcon.setImageResource(R.drawable.favorite_light);
                }
            }
        });




            BmobQuery<User> userBmobQuery = new BmobQuery<>();
            userBmobQuery.addWhereRelatedTo("likes", new BmobPointer(st));
            userBmobQuery.count(User.class, new CountListener() {
                @Override
                public void done(Integer num, BmobException e) {
                    Log.i("favoriteNum",num+"");
                    st.setFavoriteNum(num);
                    favoriteNum.setText(num + " 个赞");
                }
            });


            BmobQuery<Comment> commentBmobQuery = new BmobQuery<>();
            commentBmobQuery.addWhereEqualTo("toStatus", new BmobPointer(st));
            commentBmobQuery.count(Comment.class, new CountListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    st.setCommentNum(integer);
                   commentNum.setText(integer+" 条评论");
                }
            });

        int tag_count = st.getTags().size() > 3 ? 3 : st.getTags().size();
        tag_one.setVisibility(View.INVISIBLE);
        tag_two.setVisibility(View.INVISIBLE);
        tag_three.setVisibility(View.INVISIBLE);
        if(tag_count>0){
           tag_one.setVisibility(View.VISIBLE);
           tag_one.setText(st.getTags().get(0));
        }
        if(tag_count>1){
            tag_two.setVisibility(View.VISIBLE);
            tag_two.setText(st.getTags().get(1));
        }
        if(tag_count>2){
            tag_three.setVisibility(View.VISIBLE);
            tag_three.setText(st.getTags().get(2));
        }


        textBy.setText(st.getAuthor().getNickname()+": ");
        text.setText(st.getText());
        if(st.getLocName()!=null) {
            location.setVisibility(View.VISIBLE);
            location.setText(st.getLocName());
        }

        try {
            postTime.setText(Utils.calculTime(st.getCreatedAt()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_single_status_comment:
                Intent intent=new Intent(this, CommentActivity.class);
                intent.putExtra("statusId",status.getObjectId());
                startActivity(intent);
                break;
            case R.id.tv_single_status_commentNum:
                Intent intent2=new Intent(this, CommentActivity.class);
                intent2.putExtra("statusId",status.getObjectId());
                startActivity(intent2);
                break;
            case R.id.tv_single_status_text:
                Intent intent3=new Intent(this, CommentActivity.class);
                intent3.putExtra("statusId",status.getObjectId());
                startActivity(intent3);
                break;
            case R.id.iv_single_status_share:
                MainApplication.showShareRemote(this,status.getPhoto().getUrl(),status.getText());
                break;
            case R.id.tv_single_status_favoriteNum:
                Intent intent4=new Intent(this, LikesActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("type","status");
                bundle.putString("statusId",statusId);
                intent4.putExtras(bundle);
                startActivity(intent4);
                break;
            case R.id.iv_single_status_back:
                finish();
                break;
            case R.id.iv_single_status_favorite:
                BmobRelation relation = new BmobRelation();
                favoriteIcon.setEnabled(false);
                if (status.isFavoritedByMe2()) {
                    //取消收藏
                    relation.remove(currentUser);
                    status.setLikes(relation);
                    status.setFavoriteNum(status.getFavoriteNum()-1);
                    status.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            status.setFavoritedByMe2(false);
                            favoriteNum.setText(status.getFavoriteNum()+"个赞");
                            favoriteIcon.setImageResource(R.drawable.favorite_light);
                            favoriteIcon.setEnabled(true);
                        }
                    });
                } else {
                    //添加收藏
                    relation.add(currentUser);
                    status.setLikes(relation);
                    status.setFavoriteNum(status.getFavoriteNum()+1);
                    status.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            status.setFavoritedByMe2(true);
                            favoriteNum.setText(status.getFavoriteNum()+"个赞");
                            favoriteIcon.setImageResource(R.drawable.favorite_red);
                            favoriteIcon.setEnabled(true);
                        }
                    });
                }

                break;
            case R.id.sdv_single_status_userPortrait:
                Intent intent1=new Intent(this,UserProfileActivity.class);
                intent1.putExtra("userId",status.getAuthor().getObjectId());
                startActivity(intent1);
                break;
            case R.id.tv_single_status_username:
                Intent intent5=new Intent(this,UserProfileActivity.class);
                intent5.putExtra("userId",status.getAuthor().getObjectId());
                startActivity(intent5);
                break;
            case R.id.iv_single_status_option:
                if(status.getAuthor().getObjectId().equals(Utils.getCurrentUser().getObjectId())) {
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
                                            msg.what = 2;
                                            alpha += 0.01f;
                                            msg.obj = alpha;
                                            mHandler.sendMessage(msg);
                                        }
                                    }

                                }).start();
                            }
                        });
                    }

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
                                    msg.what = 2;
                                    //每次减少0.01，精度越高，变暗的效果越流畅
                                    alpha -= 0.01f;
                                    msg.obj = alpha;
                                    mHandler.sendMessage(msg);
                                }
                            }

                        }).start();
                    }else {
                 downPic(option);
                }
//                }
                break;
            case R.id.tv_single_status_option_menu_delete:
                    dialog=new AlertDialog.Builder(this);
                    dialog.setTitle("确定删除?");
                    dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            status.delete(statusId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
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
                Intent intent6=new Intent(this,EditStatusActivity.class);
                intent6.putExtra("statusId",status.getObjectId());
                startActivity(intent6);
                finish();
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

    public void downPic(View v){
        FaceMePopupMenu popupMenu=new FaceMePopupMenu(this);
        popupMenu.show(v);
        popupMenu.setOnConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.downPic(status.getPhoto().getUrl(),mHandler);

            }
        });
    }


    @Override
    public boolean onLongClick(View v) {
        downPic(statusPhoto);
        return false;
    }

}
