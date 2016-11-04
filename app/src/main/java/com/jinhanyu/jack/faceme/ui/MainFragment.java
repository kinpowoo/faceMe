package com.jinhanyu.jack.faceme.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
public class MainFragment extends Fragment {
    private ListView listView;
    private List<Status> list;
    private MainFragmentAdapter adapter;
    private PtrFrameLayout ptrFrameLayout;
    private Ptr_refresh ptr_refresh;
  private User me= User.getCurrentUser(User.class);

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
        //子查询(主查询)
        BmobQuery<User> innerQuery = new BmobQuery<>();
        //子查询之一： 查询朋友
        BmobQuery<User> followingQuery = new BmobQuery<>();
        followingQuery.addWhereRelatedTo("following", new BmobPointer(me));
        //子查询之二： 查询自己
        BmobQuery<User> selfQuery = new BmobQuery<>();
        selfQuery.addWhereEqualTo("objectId",me.getObjectId());
        //合并子查询
        List<BmobQuery<User>> addonQueries = new ArrayList<>();
        addonQueries.add(selfQuery);
        addonQueries.add(followingQuery);
        //添加到主查询中
        innerQuery.or(addonQueries);

        //最终查询
        BmobQuery<Status> statusQuery = new BmobQuery<>();
        statusQuery.order("-createdAt");
        statusQuery.addWhereMatchesQuery("author", "_User", innerQuery);
        statusQuery.include("author");
        statusQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> data, BmobException e) {
                Log.i("statuses",data.size()+"");
                list.clear();
                list.addAll(data);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
