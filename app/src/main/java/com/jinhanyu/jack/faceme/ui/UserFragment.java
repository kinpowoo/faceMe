package com.jinhanyu.jack.faceme.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaSync;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.BaseFragment;
import com.jinhanyu.jack.faceme.MainApplication;
import com.jinhanyu.jack.faceme.Ptr_refresh;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.adapter.GridViewAdapter;
import com.jinhanyu.jack.faceme.adapter.MainFragmentAdapter;
import com.jinhanyu.jack.faceme.aidl.StatusInterface;
import com.jinhanyu.jack.faceme.comparator.StatusComparator;
import com.jinhanyu.jack.faceme.cutsom_view.NoScrollGridView;
import com.jinhanyu.jack.faceme.cutsom_view.NoScrollListView;
import com.jinhanyu.jack.faceme.cutsom_view.VerticalLoading;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class UserFragment extends BaseFragment implements View.OnClickListener,
                RadioGroup.OnCheckedChangeListener,StatusInterface{
    private ImageView addFriend,settings;
    private TextView username,nickname,statusNum,followingNum,followersNum;
    private LinearLayout followingParent,followerParent;
    private RadioGroup radioGroup;
    private Button editProfile;
    private SimpleDraweeView userPortrait;
    private NoScrollGridView gridView;
    private PtrFrameLayout ptrFrameLayout;
    private Ptr_refresh ptr_refresh;

    private NoScrollListView listView;
    private GridViewAdapter adapter;
    private MainFragmentAdapter listAdapter;
    private List<Status> list;
    private LinearLayout header;
    private User me= Utils.getCurrentUser();

    private VerticalLoading loading;


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(loading!=null&&loading.isShowing()){
                        loading.dismiss();
                    }
                    if(ptrFrameLayout!=null){
                        ptrFrameLayout.refreshComplete();
                    }
                    changeView(0);
                    break;
                case 2:
                    if(loading!=null&&loading.isShowing()){
                        loading.dismiss();
                    }
                    if(ptrFrameLayout!=null){
                        ptrFrameLayout.refreshComplete();
                    }
                    Toast.makeText(getActivity(),"拉取数据失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.user_fragment,null);
        addFriend= (ImageView) view.findViewById(R.id.iv_userFragment_addContact);
        settings= (ImageView) view.findViewById(R.id.iv_userFragment_settings);
        username= (TextView) view.findViewById(R.id.tv_userFragment_username);
        nickname= (TextView) view.findViewById(R.id.tv_userFragment_nickname);
        statusNum= (TextView) view.findViewById(R.id.tv_userFragment_statusNUm);
        followersNum= (TextView) view.findViewById(R.id.tv_userFragment_followersNum);
        followingNum= (TextView) view.findViewById(R.id.tv_userFragment_followingNum);
        followerParent= (LinearLayout) view.findViewById(R.id.ll_userFragment_followerNum);
        followingParent= (LinearLayout) view.findViewById(R.id.ll_userFragment_followingNum);
        radioGroup= (RadioGroup) view.findViewById(R.id.rg_userFragment);
        editProfile= (Button) view.findViewById(R.id.btn_userFragment_editProfile);
        userPortrait= (SimpleDraweeView) view.findViewById(R.id.sdv_userFragment_userPortrait);
        gridView= (NoScrollGridView) view.findViewById(R.id.gv_userFragment_photos);
        listView= (NoScrollListView) view.findViewById(R.id.lv_userFragment_photos);
        header= (LinearLayout) view.findViewById(R.id.ll_header);

        ptrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.refresh_layout);
        ptr_refresh=new Ptr_refresh(getActivity());
        ptrFrameLayout.setHeaderView(ptr_refresh);
        ptrFrameLayout.addPtrUIHandler(ptr_refresh);
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ptrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadStatus();
                    }
                },200);
            }
        });


        loading = new VerticalLoading(getActivity(),"加载中...");


        addFriend.setOnClickListener(this);
        settings.setOnClickListener(this);
        followingParent.setOnClickListener(this);
        followerParent.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);

        list=new ArrayList<>();
        adapter=new GridViewAdapter(list,getActivity());

        gridView.setAdapter(adapter);
        listAdapter=new MainFragmentAdapter(list,getActivity(),getActivity());
        listView.setAdapter(listAdapter);


        fillData();

        if(loading!=null&&!loading.isShowing()){
            loading.show();
        }
        loadStatus();

        //注册广播


        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void fillData(){
        userPortrait.setImageURI(me.getPortrait().getUrl());
        username.setText(me.getUsername());
        nickname.setText(me.getNickname());
        followersNum.setText(me.getFollowerNum()+"");
        followingNum.setText(me.getFollowingNum()+"");

        /**
            BmobQuery<User> followerQuery=new BmobQuery<>();
            BmobQuery<User> innerQuery=new BmobQuery<>();
            innerQuery.addWhereEqualTo("objectId",me.getObjectId());
            followerQuery.addWhereMatchesQuery("following","_User",innerQuery);
            followerQuery.count(User.class, new CountListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    me.setFollowerNum(integer);

                }
            });

            BmobQuery<User> query=new BmobQuery<>();
            query.addWhereRelatedTo("following",new BmobPointer(me));
            query.count(User.class, new CountListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    me.setFollowingNum(integer);
                    followingNum.setText(integer+"");
                }
            });
         */

            BmobQuery<Status> statusBmobQuery = new BmobQuery<>();
            statusBmobQuery.addWhereEqualTo("author",new BmobPointer(me));
            statusBmobQuery.count(Status.class, new CountListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    statusNum.setText(integer+"");
                }
            });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_userFragment_addContact:
                Intent intent3=new Intent(getActivity(),AddFriendNum.class);
                startActivity(intent3);
                break;
            case R.id.iv_userFragment_settings:
                Intent intent4=new Intent(getActivity(),SettingActivity.class);
                startActivity(intent4);
                break;
            case R.id.ll_userFragment_followerNum:
                Intent intent=new Intent(getActivity(),LikesActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("type","followersNum");
                bundle.putString("userId",me.getObjectId());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.ll_userFragment_followingNum:
                Intent intent2=new Intent(getActivity(),LikesActivity.class);
                Bundle bundle2=new Bundle();
                bundle2.putString("type","followingNum");
                bundle2.putString("userId",me.getObjectId());
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
            case R.id.btn_userFragment_editProfile:
                Intent intent1=new Intent(getActivity(),EditProfileActivity.class);
                startActivity(intent1);
                break;

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_userFragment_gridView:
                listView.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
                changeView(1);
                break;
            case R.id.rb_userFragment_listView:
                gridView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                changeView(2);
                break;
        }
    }


    public void changeView(int type){
        if(type==0){
            adapter.notifyDataSetChanged();
        }else {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void loadStatus(){
        BmobQuery<Status> query=new BmobQuery<>();
        query.addWhereEqualTo("author",new BmobPointer(me));
        query.include("author");
        query.order("-createdAt");
        query.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> data, BmobException e) {
              if(e==null){
                  list.clear();
                  list.addAll(data);
                  //Collections.reverse(list);
                  handler.sendEmptyMessage(1);
              }else {
                  handler.sendEmptyMessage(2);
              }
            }
        });
    }

    @Override
    public void onResume() {
        fillData();
        super.onResume();
    }


    @Override
    public void updateStatus(final int indexPath) {
    }

    @Override
    public void deleteStatus(int indexPath) {
        list.remove(indexPath);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addStatus(Date createDate) {
        BmobQuery<Status> query = new BmobQuery<>("Status");
        query.include("author");
        query.addWhereGreaterThan("createdAt",new BmobDate(createDate));
        query.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> resList, BmobException e) {
                list.addAll(0,resList);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
