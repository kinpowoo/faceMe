package com.jinhanyu.jack.faceme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.Ptr_refresh;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.adapter.FlowFragmentAdapter;
import com.jinhanyu.jack.faceme.entity.SingleFavoriteItem;
import java.util.ArrayList;
import java.util.List;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;


/**
 * Created by anzhuo on 2016/10/18.陈礼 图钉墙
 */
public class FlowFragment extends Fragment implements View.OnClickListener {
    private GridView gv;
    private FlowFragmentAdapter adapter;
    private in.srain.cube.views.ptr.PtrFrameLayout iv_frame;
    private Ptr_refresh ptr_refresh;
    private ImageView NearbyActivity;
    private ImageView AddFriend;
    private TextView SearchTag;
    private List<SingleFavoriteItem> database;

    final private int[] photos= {R.mipmap.start, R.mipmap.popularity, R.mipmap.movie,
            R.mipmap.travel, R.mipmap.belle, R.mipmap.food, R.mipmap.deary, R.mipmap.nearby2};
    final private String [] tags={"人气","星动态","小电影","旅行","美女","美食","小宝贝","附近动态"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flow_fragment, null);

        //控件初始化
        gv = (GridView) view.findViewById(R.id.gv);
        iv_frame = (PtrFrameLayout) view.findViewById(R.id.iv_ptrFrame);
        NearbyActivity = (ImageView) view.findViewById(R.id.NearbyActivity);
        AddFriend = (ImageView) view.findViewById(R.id.AddFriend);
        SearchTag = (TextView) view.findViewById(R.id.SearchTag);

        SearchTag.setOnClickListener(this);
        NearbyActivity.setOnClickListener(this);
        AddFriend.setOnClickListener(this);


        database=new ArrayList<>();
        ptr_refresh = new Ptr_refresh(getActivity());

        adapter = new FlowFragmentAdapter(database, getActivity());
        gv.setAdapter(adapter);
        addPic();

        //下拉刷新
        iv_frame.setHeaderView(ptr_refresh);
        iv_frame.addPtrUIHandler(ptr_refresh);
        iv_frame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                iv_frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "已是最新", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        iv_frame.refreshComplete();
                        //这里加入刷新代码

                    }
                }, 2000);
            }
        });



        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                database.remove(position);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        return view;
    }

    //添加固定图片文字
    public void addPic() {
        for (int i = 0; i < 8; i++) {
          SingleFavoriteItem  singleFavoriteItem = new SingleFavoriteItem();
            singleFavoriteItem.setPicture_text(tags[i]);
            singleFavoriteItem.setPicture("res://com.jinhanyu.jack.faceme/"+photos[i]);
            database.add(singleFavoriteItem);
            adapter.notifyDataSetChanged();
        }

          //点击不同图片——进行跳转
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(),SearchResultActivity.class).putExtra("search_text",tags[position]));
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.AddFriend:
                startActivity(new Intent(getActivity(), AddFriend.class));
                break;
            case R.id.NearbyActivity:
                startActivity(new Intent(getActivity(), NearbyActivity.class));
                break;
            case R.id.SearchTag:
                startActivity(new Intent(getActivity(), SearchPicture.class));
                break;
        }
    }


}
