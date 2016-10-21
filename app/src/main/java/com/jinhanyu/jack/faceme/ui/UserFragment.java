package com.jinhanyu.jack.faceme.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class UserFragment extends Fragment implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{
    private ImageView addFriend,settings;
    private TextView username,nickname,statusNum,followingNum,followersNum;
    private RadioGroup radioGroup;
    private Button editProfile;
    private SimpleDraweeView userPortrait;
    private GridView gridView;
    private GridViewAdapter adapter;
    private List<Status> list;
    private User me= Utils.getCurrentUser();
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
        radioGroup= (RadioGroup) view.findViewById(R.id.rg_userFragment);
        editProfile= (Button) view.findViewById(R.id.btn_userFragment_editProfile);
        userPortrait= (SimpleDraweeView) view.findViewById(R.id.sdv_userFragment_userPortrait);
        gridView= (GridView) view.findViewById(R.id.gv_userFragment_photos);

        addFriend.setOnClickListener(this);
        settings.setOnClickListener(this);
        followingNum.setOnClickListener(this);
        followersNum.setOnClickListener(this);
        editProfile.setOnClickListener(this);

        list=new ArrayList<>();
        adapter=new GridViewAdapter(list,getActivity());
        fillData();
        return view;
    }

    public void fillData(){
        userPortrait.setImageURI(Uri.parse(me.getPortrait()));
        username.setText(me.getUsername());
        nickname.setText(me.getNickname());
        statusNum.setText(me.getStatusesNum()+"");
        followersNum.setText(me.getFollowersNum()+"");
        followingNum.setText(me.getFollowingNum()+"");
        gridView.setAdapter(adapter);

        loadStatus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_userFragment_addContact:
                break;
            case R.id.iv_userFragment_settings:
                break;
            case R.id.tv_userFragment_followersNum:
                Intent intent=new Intent(getActivity(),LikesActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("type","followersNum");
                bundle.putString("userId",me.getObjectId());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.tv_userFragment_followingNum:
                Intent intent2=new Intent(getActivity(),LikesActivity.class);
                Bundle bundle2=new Bundle();
                bundle2.putString("type","followingNum");
                bundle2.putString("userId",me.getObjectId());
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
            case R.id.btn_userFragment_editProfile:
                break;

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_userFragment_gridView:
                break;
            case R.id.rb_userFragment_listView:
                break;
        }
    }

    public void loadStatus(){
        BmobQuery<Status> query=new BmobQuery<>();
        query.addWhereRelatedTo("statuses",new BmobPointer(me));
        query.include("author");
        query.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> data, BmobException e) {
              if(e==null){
                  list.addAll(data);
                  adapter.notifyDataSetChanged();
              }
            }
        });
    }
}
