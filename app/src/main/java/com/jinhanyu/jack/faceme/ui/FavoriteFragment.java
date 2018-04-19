package com.jinhanyu.jack.faceme.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.BaseFragment;
import com.jinhanyu.jack.faceme.MainApplication;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.adapter.FavoriteItemAdapter;
import com.jinhanyu.jack.faceme.adapter.FavoritePagerAdapter;
import com.jinhanyu.jack.faceme.cutsom_view.VerticalLoading;
import com.jinhanyu.jack.faceme.entity.FriendLikeItem;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;
import com.jinhanyu.jack.faceme.tool.ConstantFunc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by anzhuo on 2016/10/18.
 */
public class FavoriteFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener{

    ListView friendsStatus;
    ListView aboutMeStatus;
    RadioGroup radioGroup;
    TextView noStatus;


    List<FriendLikeItem> friendStatusList = new ArrayList<>();
    List<FriendLikeItem> aboutMeStatusList = new ArrayList<>();
    private FavoriteItemAdapter friendAdapter;
    private FavoriteItemAdapter aboutMeAdapter;

    private VerticalLoading loading;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    if (loading != null && loading.isShowing()) {
                        loading.dismiss();
                    }
                    if(aboutMeStatusList.size()>0) {
                        friendsStatus.setVisibility(View.GONE);
                        aboutMeStatus.setVisibility(View.VISIBLE);
                        aboutMeAdapter.updateAdapter(aboutMeStatusList);
                    }else {
                        if(noStatus.getVisibility()==View.GONE){
                            friendsStatus.setVisibility(View.GONE);
                            aboutMeStatus.setVisibility(View.GONE);
                            noStatus.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case 4:
                    if (loading != null && loading.isShowing()) {
                        loading.dismiss();
                    }
                    Toast.makeText(getActivity(), "没有相关动态", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    if (loading != null && loading.isShowing()) {
                        loading.dismiss();
                    }
                    if(friendStatusList.size()>0){
                        aboutMeStatus.setVisibility(View.GONE);
                        friendsStatus.setVisibility(View.VISIBLE);
                        friendAdapter.updateAdapter(friendStatusList);
                    }else {
                        if(noStatus.getVisibility()==View.GONE){
                            friendsStatus.setVisibility(View.GONE);
                            aboutMeStatus.setVisibility(View.GONE);
                            noStatus.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case 6:
                    if (loading != null && loading.isShowing()) {
                        loading.dismiss();
                    }
                    Toast.makeText(getActivity(), "没有相关动态", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_fragment, null);
        friendsStatus = (ListView) view.findViewById(R.id.friends_status);
        aboutMeStatus = (ListView) view.findViewById(R.id.about_me_status);
        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        noStatus = (TextView) view.findViewById(R.id.no_status_notice);
        radioGroup.setOnCheckedChangeListener(this);

        loading = new VerticalLoading(getActivity(), "加载中...");

        radioGroup.check(R.id.friend_tab);

        friendAdapter = new FavoriteItemAdapter(friendStatusList, getActivity());
        friendsStatus.setAdapter(friendAdapter);

        aboutMeAdapter = new FavoriteItemAdapter(aboutMeStatusList, getActivity());
        aboutMeStatus.setAdapter(aboutMeAdapter);




        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }


    public void getLikeMes() {
        ConstantFunc.hint("fav","getLikesMe被 执行");
        if (loading != null && !loading.isShowing()) {
            loading.show();
        }
        aboutMeStatusList.clear();
        //子查询之二： 查询自己
        BmobQuery<User> selfQuery = new BmobQuery<>();
        selfQuery.addWhereEqualTo("objectId", User.getCurrentUser(User.class).getObjectId());
        //最终查询
        BmobQuery<Status> statusQuery = new BmobQuery<>();
        statusQuery.addWhereMatchesQuery("author", "_User", selfQuery);
        statusQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(final List<Status> list, BmobException e) {
                if (list != null && list.size() > 0) {
                    for (final Status status : list) {
                        final BmobQuery<User> likesQuery = new BmobQuery<>();
                        likesQuery.addWhereRelatedTo("likes", new BmobPointer(status));
                        likesQuery.setLimit(5);
                        likesQuery.findObjects(new FindListener<User>() {
                            @Override
                            public void done(List<User> likeMeUsers, BmobException e) {
                                if(likeMeUsers!=null&&likeMeUsers.size()>0) {
                                    for (User likeMeUser : likeMeUsers) {
                                        FriendLikeItem item = new FriendLikeItem(likeMeUser, status);
                                        aboutMeStatusList.add(item);
                                        //Log.i("item1", item.toString());
                                    }
                                }
                            }
                        });
                    }
                    handler.sendEmptyMessage(3);
                } else {
                handler.sendEmptyMessage(4);
            }

            }
        });
    }


    public void getFriendsLikes() {

        ConstantFunc.hint("fav","getFriendsLikes被 执行");

        if (loading != null && !loading.isShowing()) {
            loading.show();
        }
        friendStatusList.clear();

        //子查询之一： 查询朋友
        final BmobQuery<User> followingQuery = new BmobQuery<>();
        followingQuery.addWhereRelatedTo("following", new BmobPointer(User.getCurrentUser(User.class)));
        followingQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(final List<User> friendList, BmobException e) {
                if(friendList!=null&&friendList.size()>0) {
                    for (final User friend : friendList) {
                        //查询微博
                        BmobQuery<User> friendQuery = new BmobQuery<User>();
                        friendQuery.addWhereEqualTo("objectId", friend.getObjectId());
                        BmobQuery<Status> statusQuery = new BmobQuery<>();
                        statusQuery.include("author");
                        statusQuery.addWhereMatchesQuery("likes", "_User", friendQuery);
                        statusQuery.setLimit(5);
                        statusQuery.findObjects(new FindListener<Status>() {
                            @Override
                            public void done(List<Status> list, BmobException e) {
                                if (list != null && list.size() > 0) {
                                    for (Status status : list) {
                                        FriendLikeItem item = new FriendLikeItem(friend, status);
                                        friendStatusList.add(item);
                                    }
                                }
                            }
                        });

                    }

                    handler.sendEmptyMessage(5);
                }else {
                    handler.sendEmptyMessage(6);
                }

            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.friend_tab:
                if(friendStatusList.size()==0){
                    getFriendsLikes();
                }else {
                    aboutMeStatus.setVisibility(View.GONE);
                    friendsStatus.setVisibility(View.VISIBLE);
                    friendAdapter.updateAdapter(friendStatusList);
                }
                break;
            case R.id.me_tab:
                if(aboutMeStatusList.size()==0){
                    getLikeMes();
                }else {
                    friendsStatus.setVisibility(View.GONE);
                    aboutMeStatus.setVisibility(View.VISIBLE);
                    aboutMeAdapter.updateAdapter(aboutMeStatusList);
                }

                break;
        }
    }
}
