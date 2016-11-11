package com.jinhanyu.jack.faceme.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class MainFragment extends Fragment{
    private ListView listView;
    private List<Status> list;
    private MainFragmentAdapter adapter;
    private PtrFrameLayout ptrFrameLayout;
    private Ptr_refresh ptr_refresh;
    private User me= User.getCurrentUser(User.class);
    private ProgressBar bar;
    String lastFetchDate;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==2){
                bar.setVisibility(View.GONE);
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_fragment,null);
        listView= (ListView) view.findViewById(R.id.lv_mainFragment);
        ptrFrameLayout= (PtrFrameLayout) view.findViewById(R.id.iv_mainFragment_ptrFrame);
        ptr_refresh=new Ptr_refresh(getActivity());
        bar= (ProgressBar) view.findViewById(R.id.pb_main);
        list=new ArrayList<>();
        adapter=new MainFragmentAdapter(list,getActivity(),getActivity());
        listView.setAdapter(adapter);

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
        statusQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> data, BmobException e) {
                lastFetchDate = data.get(0).getCreatedAt();
                handler.sendEmptyMessage(2);
                list.addAll(data);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
