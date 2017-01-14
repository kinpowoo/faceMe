package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.LocationUtils;
import com.jinhanyu.jack.faceme.Ptr_refresh;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.adapter.GridViewAdapter;
import com.jinhanyu.jack.faceme.entity.Position;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by anzhuo on 2016/10/28.陈礼
 */
public class SearchResultActivity extends Activity implements View.OnClickListener,AbsListView.OnScrollListener{
    private final int SKIPNUM=10;
    private int count=0;
    private int refreshCount=0;
    private ImageView iv_back;//返回
    private TextView tv_icon;//搜索的标题
    private in.srain.cube.views.ptr.PtrFrameLayout iv_ptrFrame;//下拉刷新
    private GridView gv;//九宫格图片显示
    private Ptr_refresh refresh;//下拉刷新类对象
    private List<Status> list = new ArrayList<>();
    private GridViewAdapter adapter;
    private Position location;
    private String tagIncoming,lastFetchDate;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                location= (Position) msg.obj;
                loadDataGeo(location);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        tagIncoming=getIntent().getStringExtra("search_text");
        //控件初始化
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_icon = (TextView) findViewById(R.id.tv_icon);
        iv_ptrFrame = (PtrFrameLayout) findViewById(R.id.iv_ptrFrame);
        gv = (GridView) findViewById(R.id.gv);
        refresh = new Ptr_refresh(SearchResultActivity.this);
        adapter = new GridViewAdapter(list, this);
        gv.setAdapter(adapter);
        list.clear();
        //加载图片

        switch (tagIncoming){
            case "附近":
                new LocationUtils(this,handler).guide();
                break;
            default:
                loadDataNormal();
                break;
        }



        //控件的监听
        iv_back.setOnClickListener(this);

        //设置标题
        tv_icon.setText(tagIncoming);

        //下拉刷新，从新加载内容
        iv_ptrFrame.setHeaderView(refresh);
        iv_ptrFrame.addPtrUIHandler(refresh);
        iv_ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                iv_ptrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        iv_ptrFrame.refreshComplete();
                    }
                }, 1000);
            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                  finish();
                break;
        }
    }

    public void loadDataNormal(){
        BmobQuery<Status> statusBmobQuery = new BmobQuery<>();
        statusBmobQuery.addWhereEqualTo("tags",tagIncoming);
        statusBmobQuery.include("author");
        statusBmobQuery.order("-createdAt");
        statusBmobQuery.setSkip(SKIPNUM*count+refreshCount);
        statusBmobQuery.setLimit(10);
        statusBmobQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> data, BmobException e) {
                if(data.size()>0 && e==null) {
                    count+=1;
                    lastFetchDate=data.get(0).getCreatedAt();
                    list.addAll(data);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void loadDataGeo(Position loc){
        String bql="select * from Status where location near ["+
        loc.getLongitude()+","+loc.getLatitude()+"] order by -createdAt limit "+(SKIPNUM*count+refreshCount)+" ,10";
        BmobQuery<Status> query=new BmobQuery<Status>();
        query.doSQLQuery(bql, new SQLQueryListener<Status>() {
            @Override
            public void done(BmobQueryResult<Status> result, BmobException e) {
                if(e ==null){
                    List<Status> data = (List<Status>) result.getResults();
                    if(data!=null && data.size()>0){
                        Log.i("status","have been load "+count+" time");
                        count+=1;
                        lastFetchDate=data.get(0).getCreatedAt();
                        list.addAll(data);
                        adapter.notifyDataSetChanged();
                    }else{
                        Log.i("smile", "查询成功，无数据返回");
                    }
                }else{
                    Log.i("smile", "错误码："+e.getErrorCode()+"，错误描述："+e.getMessage());
                }
            }
        });
    }


    public void refreshData(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date= null;
        try {
            date = sdf.parse(lastFetchDate);
            date.setTime(date.getTime()+1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        switch (tagIncoming){
            case "附近":
        String bql="select * from Status where location near ["+
                location.getLongitude()+","+location.getLatitude()+"] and createdAt > date("+lastFetchDate+") order by -createdAt limit 10";
        BmobQuery<Status> query=new BmobQuery<Status>();
        query.doSQLQuery(bql, new SQLQueryListener<Status>() {
            public void done(BmobQueryResult<Status> result, BmobException e) {
                if(e ==null){
                    List<Status> data = (List<Status>) result.getResults();
                    if(data!=null && data.size()>0){
                        refreshCount+=data.size();
                        lastFetchDate=data.get(0).getCreatedAt();
                        list.addAll(0,data);
                        adapter.notifyDataSetChanged();
                    }else{
                       Toast.makeText(SearchResultActivity.this,"没有更多数据",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
                break;
            default:
        BmobQuery<Status> statusBmobQuery = new BmobQuery<>();
        statusBmobQuery.addWhereEqualTo("tags",tagIncoming);
        statusBmobQuery.include("author");
        statusBmobQuery.order("-createdAt");
        statusBmobQuery.addWhereGreaterThan("createdAt",new BmobDate(date));
        statusBmobQuery.findObjects(new FindListener<Status>() {
            @Override
            public void done(List<Status> data, BmobException e) {
                if(data.size()>0&&e==null){
                    lastFetchDate=data.get(0).getCreatedAt();
                    refreshCount+=data.size();
                    list.addAll(0,data);
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(SearchResultActivity.this,"没有更多数据",Toast.LENGTH_SHORT).show();
                }
            }
        });
                break;
        }
    }


    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(i==SCROLL_STATE_FLING) {
            if (absListView.getLastVisiblePosition() == list.size()){
              switch (tagIncoming){
                  case "附近":
                      loadDataGeo(location);
                      Log.i("status","have been load "+count+" time");
                      break;
                  default:
                      loadDataNormal();
                      break;
              }
        }
    }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        list.clear();
    }
}

