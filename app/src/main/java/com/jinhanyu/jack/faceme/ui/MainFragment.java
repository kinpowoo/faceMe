package com.jinhanyu.jack.faceme.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jinhanyu.jack.faceme.Ptr_refresh;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.adapter.MainFragmentAdapter;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class MainFragment extends Fragment {
    private ListView listView;
    private List<Status> list;
    private MainFragmentAdapter adapter;
    private PtrFrameLayout ptrFrameLayout;
    private Ptr_refresh ptr_refresh;
//    private User me= BmobUser.getCurrentUser(User.class);

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_fragment,null);
        listView= (ListView) view.findViewById(R.id.lv_mainFragment);
        ptrFrameLayout= (PtrFrameLayout) view.findViewById(R.id.iv_mainFragment_ptrFrame);
        ptr_refresh=new Ptr_refresh(getActivity());
        list=new ArrayList<>();
        adapter=new MainFragmentAdapter(list,getActivity());
        listView.setAdapter(adapter);


        ptrFrameLayout.setHeaderView(ptr_refresh);
        ptrFrameLayout.addPtrUIHandler(ptr_refresh);
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ptrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                        ptrFrameLayout.refreshComplete();
                    }
                },2000);
            }
        });
        loadData();
        return view;
    }

    public void loadData(){
        final BmobQuery<Status> query=new BmobQuery<>();
        User me=new User();
        me.setObjectId("XQm2333J");
        query.addWhereRelatedTo("statuses",new BmobPointer(me));
        query.include("author");
        query.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> data, BmobException e) {
            list.addAll(data);
            }
        });

        BmobQuery<User> query1=new BmobQuery<>();
        query1.addWhereRelatedTo("following",new BmobPointer(me));
        query1.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> followings, BmobException e) {
            for (User user : followings){
                    BmobQuery<Status> query3=new BmobQuery<>();
                    query3.include("author");
                    query3.addWhereRelatedTo("statuses",new BmobPointer(user));
                    query3.findObjects(new FindListener<Status>() {
                        @Override
                        public void done(List<Status> data2, BmobException e) {
                              list.addAll(data2);
                        }
                    });

                }
            }
        });

        Collections.sort(list);
        adapter.notifyDataSetChanged();
    }
}
