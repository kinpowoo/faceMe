package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.Ptr_refresh;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.User;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by anzhuo on 2016/10/25.陈礼
 */
public class AddFriend extends Activity implements View.OnClickListener {
    private ImageView iv_back,iv_search;
    private Ptr_refresh refresh;//刷新类
    private in.srain.cube.views.ptr.PtrFrameLayout iv_frame;
    private EditText seart_name;
    private LinearLayout focus_show, fans_show, add_friend, start_stand, interest_recommend, nearby_person;
    private TextView focus_num, fans_num;
    private User userIncoming=Utils.getCurrentUser();
    private String userId;
    private User user= Utils.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        iv_frame = (PtrFrameLayout) findViewById(R.id.iv_ptrFrame);
        seart_name = (EditText) findViewById(R.id.search_name);
        focus_num = (TextView) findViewById(R.id.focus_num);
        fans_num = (TextView) findViewById(R.id.fans_num);
        focus_show = (LinearLayout) findViewById(R.id.focus_show);
        fans_show = (LinearLayout) findViewById(R.id.fans_show);
        add_friend = (LinearLayout) findViewById(R.id.add_friend);
        start_stand = (LinearLayout) findViewById(R.id.start_stand);
        interest_recommend = (LinearLayout) findViewById(R.id.interest_recommend);
        nearby_person = (LinearLayout) findViewById(R.id.nearby_person);

        refresh = new Ptr_refresh(this);
        iv_back.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        focus_show.setOnClickListener(this);
        fans_show.setOnClickListener(this);
        add_friend.setOnClickListener(this);
        start_stand.setOnClickListener(this);
        interest_recommend.setOnClickListener(this);
        nearby_person.setOnClickListener(this);

        iv_frame.setHeaderView(refresh);
        iv_frame.addPtrUIHandler(refresh);

        iv_frame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                iv_frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_frame.refreshComplete();
                        focus_num.setText("500");
                        fans_num.setText("9800000");

                        //刷新代码写在这
                    }
                }, 1500);
            }
        });

        //粉丝数量
        BmobQuery<User> followerQuery = new BmobQuery<>();
        BmobQuery<User> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId", user.getObjectId());
        followerQuery.addWhereMatchesQuery("following", "_User", innerQuery);
        followerQuery.count(User.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                user.setFollowerNum(integer);
                fans_num.setText(integer + "");
            }
        });


        //关注数量
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("following", new BmobPointer(user));
        query.count(User.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                user.setFollowingNum(integer);
                focus_num.setText(integer + "");
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back://返回
               finish();
                break;
            case R.id.iv_search:

                break;
            case R.id.focus_show://关注
                Intent intent2=new Intent(this,LikesActivity.class);
                Bundle bundle2=new Bundle();
                bundle2.putString("type","followingNum");
                bundle2.putString("userId",userIncoming.getObjectId());
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
            case R.id.fans_show://粉丝
                Intent intent=new Intent(this,LikesActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("type","followersNum");
                bundle.putString("userId",userIncoming.getObjectId());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.add_friend://添加好友
                startActivity(new Intent(AddFriend.this,AddFriendNum.class));
                break;
            case R.id.interest_recommend://兴趣推荐
                startActivity(new Intent(AddFriend.this,DyqActivity.class).putExtra("icon","兴趣推荐"));
                break;
            case R.id.nearby_person://附近的人
                startActivity(new Intent(AddFriend.this,DyqActivity.class).putExtra("icon","附近的人"));
                break;
            case R.id.start_stand://星客站
                startActivity(new Intent(AddFriend.this,DyqActivity.class).putExtra("icon","星客站"));
                break;
            case R.id.search_name:

                break;
        }
    }

}
