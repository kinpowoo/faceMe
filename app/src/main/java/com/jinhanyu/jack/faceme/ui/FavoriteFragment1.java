package com.jinhanyu.jack.faceme.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.adapter.FavoriteItemAdapter;
import com.jinhanyu.jack.faceme.entity.FriendLikeItem;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by anzhuo on 2016/10/21.
 */
public class FavoriteFragment1 extends Fragment {
    private ListView listView;
    private FavoriteItemAdapter adapter;
    public  List<FriendLikeItem> items;



    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_fragment1, null);
        listView= (ListView) view.findViewById(R.id.listview);

        items= Collections.synchronizedList(new ArrayList<FriendLikeItem>());
        adapter = new FavoriteItemAdapter(items,getActivity());
        listView.setAdapter(adapter);
        changeFragment();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Log.i("items",items.toString());
                adapter.notifyDataSetChanged();
            }
        },5000);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void changeFragment(){
        switch (getArguments().getInt("location")){
            case 0:
                getFriendsLikes();
                break;
            case 1:
                getLikeMes();
                break;
        }
    }


    public  void getLikeMes() {
        //子查询之二： 查询自己
        BmobQuery<User> selfQuery = new BmobQuery<>();
        selfQuery.addWhereEqualTo("objectId", User.getCurrentUser(User.class).getObjectId());
        //最终查询
        BmobQuery<Status> statusQuery = new BmobQuery<>();
        statusQuery.addWhereMatchesQuery("author", "_User", selfQuery);
        statusQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> list, BmobException e) {
                for (final Status status : list) {
                    final BmobQuery<User> likesQuery = new BmobQuery<>();
                    likesQuery.addWhereRelatedTo("likes", new BmobPointer(status));
                    likesQuery.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> likeMeUsers, BmobException e) {
                            for(User likeMeUser: likeMeUsers){
                                FriendLikeItem item = new FriendLikeItem(likeMeUser,status);
                                items.add(item);
                                Log.i("item1", item.toString());
                            }
                        }
                    });
                }

            }
        });
    }




    public  void getFriendsLikes() {

        //子查询之一： 查询朋友
        final BmobQuery<User> followingQuery = new BmobQuery<>();
        followingQuery.addWhereRelatedTo("following", new BmobPointer(User.getCurrentUser(User.class)));
        followingQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(final List<User> friendList, BmobException e) {
                //查询微博
                BmobQuery<Status> statusQuery = new BmobQuery<>();
                statusQuery.include("author");
                statusQuery.addWhereMatchesQuery("likes", "_User", followingQuery);
                statusQuery.findObjects(new FindListener<Status>() {
                    @Override
                    public void done(final List<Status> list, BmobException e) {
                        for (final Status status : list) {
                            final BmobQuery<User> likesQuery = new BmobQuery<>();
                            likesQuery.addWhereRelatedTo("likes", new BmobPointer(status));
                            likesQuery.findObjects(new FindListener<User>() {
                                @Override
                                public void done(List<User> likesUsers, BmobException e) {
                                    for (User friend : friendList) {
                                        for (User likesUser : likesUsers) {
                                            if (friend.getNickname().equals(likesUser.getNickname())) {
                                                FriendLikeItem item = new FriendLikeItem(friend, status);
                                                items.add(item);
                                                Log.i("item", item.toString());
                                            }
                                        }
                                    }
                                }
                            });
                        }

                    }
                });
            }
        });
    }

}
