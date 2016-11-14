package com.jinhanyu.jack.faceme.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.MainApplication;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.adapter.GridViewAdapter;
import com.jinhanyu.jack.faceme.adapter.MainFragmentAdapter;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class UserFragment extends Fragment implements View.OnClickListener,RadioGroup.OnCheckedChangeListener,AbsListView.OnScrollListener{
    private ImageView addFriend,settings;
    private TextView username,nickname,statusNum,followingNum,followersNum;
    private LinearLayout followingParent,followerParent;
    private RadioGroup radioGroup;
    private Button editProfile;
    private SimpleDraweeView userPortrait;
    private GridView gridView;
    private ProgressBar bar;
    private ListView listView;
    private GridViewAdapter adapter;
    private MainFragmentAdapter listAdapter;
    private List<Status> list;
    private LinearLayout header;
    private User me= Utils.getCurrentUser();
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                bar.setVisibility(View.GONE);
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
        gridView= (GridView) view.findViewById(R.id.gv_userFragment_photos);
        listView= (ListView) view.findViewById(R.id.lv_userFragment_photos);
        header= (LinearLayout) view.findViewById(R.id.ll_header);




        bar= (ProgressBar) view.findViewById(R.id.pb_userFragment);

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
        return view;
    }

    public void fillData(){
        userPortrait.setImageURI(me.getPortrait().getUrl());
        username.setText(me.getUsername());
        nickname.setText(me.getNickname());

            BmobQuery<User> followerQuery=new BmobQuery<>();
            BmobQuery<User> innerQuery=new BmobQuery<>();
            innerQuery.addWhereEqualTo("objectId",me.getObjectId());
            followerQuery.addWhereMatchesQuery("following","_User",innerQuery);
            followerQuery.count(User.class, new CountListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    me.setFollowerNum(integer);
                    followersNum.setText(integer+"");
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


            BmobQuery<Status> statusBmobQuery = new BmobQuery<>();
            statusBmobQuery.addWhereEqualTo("author",new BmobPointer(me));
            statusBmobQuery.count(Status.class, new CountListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    statusNum.setText(integer+"");
                }
            });



        loadStatus(1);
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
                loadStatus(1);
                break;
            case R.id.rb_userFragment_listView:
                gridView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                listView.setOnScrollListener(this);
                loadStatus(2);
                break;
        }
    }

    public void loadStatus(final int type){
        BmobQuery<Status> query=new BmobQuery<>();
        query.addWhereEqualTo("author",new BmobPointer(me));
        query.include("author");
        query.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> data, BmobException e) {
              if(e==null){
                  list.clear();
                  list.addAll(data);
                  handler.sendEmptyMessage(1);
                  if(type==1){
                      adapter.notifyDataSetChanged();
                  }else {
                      listAdapter.notifyDataSetChanged();
                  }

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
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem==0){
            header.setVisibility(View.VISIBLE);
        }else{
            header.setVisibility(View.GONE);
        }
    }

}
