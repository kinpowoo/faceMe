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
import com.jinhanyu.jack.faceme.Ptr_refresh;
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
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;


/**
 * Created by anzhuo on 2016/10/18.
 */
public class FavoriteFragment extends BaseFragment{

    ListView aboutMeStatus;
    TextView noStatus;
    List<FriendLikeItem> aboutMeStatusList = new ArrayList<>();
    private FavoriteItemAdapter aboutMeAdapter;

    private VerticalLoading loading;
    private PtrFrameLayout ptrFrameLayout;
    private Ptr_refresh ptr_refresh;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    if (loading != null && loading.isShowing()) {
                        loading.dismiss();
                    }
                    if(aboutMeStatusList.size()>0) {
                        noStatus.setVisibility(View.GONE);
                    }else {
                        noStatus.setVisibility(View.VISIBLE);
                    }
                    aboutMeAdapter.updateAdapter(aboutMeStatusList);
                    if(ptrFrameLayout!=null){
                        ptrFrameLayout.refreshComplete();
                    }
                    break;
                case 4:
                    if (loading != null && loading.isShowing()) {
                        loading.dismiss();
                    }

                    noStatus.setVisibility(View.VISIBLE);
                    if(ptrFrameLayout!=null){
                        ptrFrameLayout.refreshComplete();
                    }
                    Toast.makeText(getActivity(), "没有相关动态", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_fragment, null);
        aboutMeStatus = (ListView) view.findViewById(R.id.about_me_status);
        noStatus = (TextView) view.findViewById(R.id.no_status_notice);

        loading = new VerticalLoading(getActivity(), "加载中...");

        aboutMeAdapter = new FavoriteItemAdapter(aboutMeStatusList, getActivity());
        aboutMeStatus.setAdapter(aboutMeAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        getLikeMes();

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
                        getLikeMes();
                    }
                },200);
            }
        });
    }




    public void getFriendsLikes() {

        ConstantFunc.hint("fav","getFriendsLikes被 执行");

        if (loading != null && !loading.isShowing()) {
            loading.show();
        }
        aboutMeStatusList.clear();

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
                                        aboutMeStatusList.add(item);
                                    }
                                }
                            }
                        });

                    }

                    handler.sendEmptyMessage(3);
                }else {
                    handler.sendEmptyMessage(4);
                }

            }
        });
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
                    for (int i=0;i<list.size();i++) {
                        final Status status = list.get(i);
                        final BmobQuery<User> likesQuery = new BmobQuery<>();
                        final int index = i;
                        likesQuery.addWhereRelatedTo("likes", new BmobPointer(status));
                        likesQuery.setLimit(5);
                        likesQuery.findObjects(new FindListener<User>() {
                            @Override
                            public void done(List<User> likeMeUsers, BmobException e) {
                                if(likeMeUsers!=null&&likeMeUsers.size()>0) {
                                    for (User likeMeUser : likeMeUsers) {
                                        FriendLikeItem item = new FriendLikeItem(likeMeUser, status);
                                        aboutMeStatusList.add(item);
                                    }
                                }
                                if(index == list.size()-1) {
                                    Log.i("item1", "about me size:" + aboutMeStatusList.size());
                                    handler.sendEmptyMessage(3);
                                }
                            }
                        });
                    }
                } else {
                handler.sendEmptyMessage(4);
            }
            }
        });
    }

}
