package com.jinhanyu.jack.faceme.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.MainApplication;
import com.jinhanyu.jack.faceme.Ptr_refresh;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.adapter.MainFragmentAdapter;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class MainFragment extends Fragment implements AbsListView.OnScrollListener{
    private int COUNT=0;
    private final int SKIPMOUNT=20;
    private int refreshCount=0;
    private ListView listView;
    private List<Status> list;
    private MainFragmentAdapter adapter;
    private PtrFrameLayout ptrFrameLayout;
    private Ptr_refresh ptr_refresh;
    private User me= User.getCurrentUser(User.class);
    private String lastFetchDate;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_fragment,null);
        listView= (ListView) view.findViewById(R.id.lv_mainFragment);
        ptrFrameLayout= (PtrFrameLayout) view.findViewById(R.id.iv_mainFragment_ptrFrame);
        ptr_refresh=new Ptr_refresh(getActivity());
        list=new ArrayList<>();
        adapter=new MainFragmentAdapter(list,getActivity(),getActivity());
        listView.setAdapter(adapter);
        listView.setOnScrollListener(this);

        ptrFrameLayout.setHeaderView(ptr_refresh);
        ptrFrameLayout.addPtrUIHandler(ptr_refresh);
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ptrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                    }
                },2000);
            }
        });
        loadData();
        return view;
    }

    public void refreshData(){
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
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date= null;
        try {
            date = sdf.parse(lastFetchDate);
            date.setTime(date.getTime()+1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        statusQuery.addWhereGreaterThan("createdAt",new BmobDate(date));
        statusQuery.include("author");
        statusQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> data, BmobException e) {
                if(data.size()> 0 ){
                    refreshCount+=data.size();
                    lastFetchDate = data.get(0).getCreatedAt();
                    list.addAll(0,data);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "刷新了"+data.size()+"条数据", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "已是最新数据", Toast.LENGTH_SHORT).show();
                }
                ptrFrameLayout.refreshComplete();

            }
        });
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
        statusQuery.setSkip(COUNT*SKIPMOUNT+refreshCount);
        statusQuery.setLimit(20);
        //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
        boolean isCache = statusQuery.hasCachedResult(Status.class);
        if(isCache){  //此为举个例子，并不一定按这种方式来设置缓存策略
            statusQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            statusQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }
        statusQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> data, BmobException e) {
                if(data!=null&&e==null) {
                    COUNT++;
                    lastFetchDate = data.get(0).getCreatedAt();
                    list.addAll(data);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(i==SCROLL_STATE_FLING) {
            if (absListView.getLastVisiblePosition() == absListView.getCount()-1) {
                loadData();
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }
}
