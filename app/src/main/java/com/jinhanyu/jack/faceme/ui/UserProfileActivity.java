package com.jinhanyu.jack.faceme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.adapter.GridViewAdapter;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by jianbo on 2016/10/20.
 */
public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{
    private ImageView back,option;
    private TextView username,nickname,statusNum,followingNum,followersNum;
    private RadioGroup radioGroup;
    private Button isFollowing;
    private SimpleDraweeView userPortrait;
    private GridView gridView;
    private GridViewAdapter adapter;
    private List<Status> list;
    private String userId;
    private User userIncoming;
    private User currentUser=Utils.getCurrentUser();
    private boolean following=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);

        back= (ImageView)findViewById(R.id.iv_userProfile_back);
        option= (ImageView)findViewById(R.id.iv_userProfile_option);
        username= (TextView)findViewById(R.id.tv_userProfile_username);
        nickname= (TextView)findViewById(R.id.tv_userProfile_nickname);
        statusNum= (TextView)findViewById(R.id.tv_userProfile_statusNUm);
        followersNum= (TextView)findViewById(R.id.tv_userProfile_followersNum);
        followingNum= (TextView)findViewById(R.id.tv_userProfile_followingNum);
        radioGroup= (RadioGroup)findViewById(R.id.rg_userProfile);
        isFollowing= (Button)findViewById(R.id.btn_userProfile_isFollowing);
        userPortrait= (SimpleDraweeView)findViewById(R.id.sdv_userProfile_userPortrait);
        gridView= (GridView)findViewById(R.id.gv_userProfile_photos);

        back.setOnClickListener(this);
        option.setOnClickListener(this);
        followingNum.setOnClickListener(this);
        followersNum.setOnClickListener(this);
        isFollowing.setOnClickListener(this);

        list=new ArrayList<>();
        adapter=new GridViewAdapter(list,this);

        userId=getIntent().getStringExtra("userId");
        if(userId!=null){
            BmobQuery<User> query=new BmobQuery<>();
            query.getObject(userId, new QueryListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if(e==null){
                        userIncoming=user;
                        if(user==currentUser){
                            fillData(currentUser);
                        }else {
                            fillData(user);
                        }
                    }
                }
            });
        }

    }

    public void fillData(User user){
        userPortrait.setImageURI(user.getPortrait().getUrl());
        username.setText(user.getUsername());
        nickname.setText(user.getNickname());
//        statusNum.setText(user.getStatusesNum()+"");
//        followersNum.setText(user.getFollowersNum()+"");
//        followingNum.setText(user.getFollowingNum()+"");
        gridView.setAdapter(adapter);

        if(user==currentUser){
            isFollowing.setText("编辑个人主页");
        }else {
           BmobQuery<User> query=new BmobQuery<>();
            query.addWhereRelatedTo("following",new BmobPointer(currentUser));
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                  for(User user1:list){
                      if(user1.getObjectId().equals(userIncoming.getObjectId())){
                          isFollowing.setText("取消关注");
                          following=true;
                      }
                  }

                }
            });

//            if (followingList != null) {
//                if (followingList.contains(userIncoming)) {
//                    isFollowing.setText("取消关注");
//                } else {
//                    isFollowing.setText("关注");
//                }
//            }
        }
        loadStatus(user);
    }

    public void loadStatus(User user){
        BmobQuery<Status> statusBmobQuery = new BmobQuery<>();
        statusBmobQuery.addWhereEqualTo("author",new BmobPointer(user));
        statusBmobQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> data, BmobException e) {
                 list.clear();
                 list.addAll(data);
                 adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_userProfile_gridView:
                break;
            case R.id.rb_userProfile_listView:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_userProfile_back:
                finish();
                break;
            case R.id.iv_userProfile_option:


                break;

            case R.id.tv_userProfile_followersNum:
                Intent intent=new Intent(this,LikesActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("type","followersNum");
                bundle.putString("userId",userIncoming.getObjectId());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.tv_userProfile_followingNum:
                Intent intent2=new Intent(this,LikesActivity.class);
                Bundle bundle2=new Bundle();
                bundle2.putString("type","followingNum");
                bundle2.putString("userId",userIncoming.getObjectId());
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
            case R.id.btn_userProfile_isFollowing:
                if(userIncoming==currentUser){
                    Intent intent1=new Intent(this,EditProfileActivity.class);
                    intent1.putExtra("userId",currentUser.getObjectId());
                    startActivity(intent1);
                }else {
                    if(following){
                        BmobRelation relation = new BmobRelation();
                        relation.remove(userIncoming);
                        currentUser.setFollowing(relation);
                        currentUser.update(currentUser.getObjectId(),new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                isFollowing.setText("关注");
                                following=false;
                            }
                        });
                    }else {
                        BmobRelation relation = new BmobRelation();
                        relation.add(userIncoming);
                        currentUser.setFollowing(relation);
                        currentUser.update(currentUser.getObjectId(),new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                isFollowing.setText("取消关注");
                                following=true;
                            }
                        });
                    }
                }
                break;
        }
    }

    public static void getStatuses(User user){
        BmobQuery<Status> statusBmobQuery = new BmobQuery<>();
        statusBmobQuery.addWhereEqualTo("author",new BmobPointer(user));
        statusBmobQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> list, BmobException e) {
                for (Status status : list) {
                    Log.i("text", status.getText());
                    Log.i("tags", status.getTags().toString());
                    Log.i("photo", status.getPhoto().getUrl());
                }
            }
        });
    }
}
