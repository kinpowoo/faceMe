package com.jinhanyu.jack.faceme.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
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
    private User currentUser=Utils.getCurrentUser();

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
            case R.id.sdv_single_status_userPortrait
                    |R.id.tv_single_status_username:
                Intent intent1=new Intent(this,UserProfileActivity.class);
                intent1.putExtra("userId",status.getAuthor().getObjectId());
                startActivity(intent1);
                break;
            case R.id.iv_single_status_option:
                break;
            case R.id.iv_single_status_refresh:
                fillData(status);
                break;

        }
    }
}
