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

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.adapter.FlowFragmentAdapter;
import com.jinhanyu.jack.faceme.entity.SingleFavoriteItem;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by anzhuo on 2016/10/18.陈礼 图钉墙
 */
public class FlowFragment extends Fragment implements View.OnClickListener {
    private GridView gv;
    private FlowFragmentAdapter adapter;

    private ImageView AddFriend;
    private TextView SearchTag;
    private List<SingleFavoriteItem> database;

    final private int[] photos= {R.mipmap.stars, R.mipmap.handsome_boy, R.mipmap.faceme,
            R.mipmap.travel, R.mipmap.beauty, R.mipmap.food, R.mipmap.baby, R.mipmap.nearby_big};
     private String [] tags;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.flow_fragment, null);
        tags=getResources().getStringArray(R.array.tags);
        //控件初始化
        gv = (GridView) view.findViewById(R.id.gv);
        AddFriend = (ImageView) view.findViewById(R.id.AddFriend);
        SearchTag = (TextView) view.findViewById(R.id.SearchTag);

        SearchTag.setOnClickListener(this);
        AddFriend.setOnClickListener(this);


        database=new ArrayList<>();

        adapter = new FlowFragmentAdapter(database, getActivity());
        gv.setAdapter(adapter);
        addPic();



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
            case R.id.SearchTag:
                startActivity(new Intent(getActivity(), SearchPicture.class));
                break;
        }
    }


}
