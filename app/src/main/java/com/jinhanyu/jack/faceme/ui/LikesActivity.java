package com.jinhanyu.jack.faceme.ui;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.ClearEditText;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.adapter.LikesAdapter;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by jianbo on 2016/10/19.
 */
public class LikesActivity extends AppCompatActivity implements TextWatcher,View.OnClickListener{
    private ListView listView;
    private TextView title;
    private List<User> list;
    private LikesAdapter adapter;
    private ClearEditText search;
    private ImageView back;
    private String type;
    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.likes_activity);
        listView= (ListView) findViewById(R.id.lv_likes);
        title= (TextView) findViewById(R.id.tv_likes_title);
        search= (ClearEditText) findViewById(R.id.cet_likes_search);
        back= (ImageView) findViewById(R.id.iv_likes_back);
        list=new ArrayList<>();
        adapter=new LikesAdapter(list,LikesActivity.this);
        listView.setAdapter(adapter);

        back.setOnClickListener(this);
        search.addTextChangedListener(this);

        bundle=getIntent().getExtras();
        type=bundle.getString("type");
        switch (type){
            case "followingNum":
                title.setText("关注列表");
               String userId=bundle.getString("userId");

                BmobQuery<User> query=new BmobQuery<>();
                query.getObject(userId, new QueryListener<User>() {
                    @Override
                    public void done(User user, BmobException e) {
                        if(e==null){
                            BmobQuery<User> query1=new BmobQuery<>();
                            query1.addWhereRelatedTo("following",new BmobPointer(user));
                            query1.findObjects(new FindListener<User>() {
                                @Override
                                public void done(List<User> data, BmobException e) {
                                    list.addAll(data);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
                break;
            case "followersNum":
                title.setText("粉丝列表");
                String userId2=bundle.getString("userId");

                BmobQuery<User> followerQuery=new BmobQuery<>();
                BmobQuery<User> innerQuery=new BmobQuery<>();
                innerQuery.addWhereEqualTo("objectId",userId2);
                followerQuery.addWhereMatchesQuery("following","_User",innerQuery);
                followerQuery.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> data, BmobException e) {
                        list.clear();
                        list.addAll(data);
                        adapter.notifyDataSetChanged();
                    }
                });
                break;
            case "status":
                title.setText("收藏列表");
              String statusId=bundle.getString("statusId");
                if(statusId!=null){
                    BmobQuery<Status> query2=new BmobQuery<>();
                    query2.getObject(statusId, new QueryListener<Status>() {
                        @Override
                        public void done(Status status, BmobException e) {
                            if(e==null){
                                loadFavorite(status);
                            }
                        }
                    });
                }
                break;
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
         filterList(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_likes_back:
                finish();
                break;
        }
    }

    public void filterList(String s){
      List<User> changedList=new ArrayList<>();
        if(TextUtils.isEmpty(s)){
            changedList=list;
        }else {
            changedList.clear();
            for(User user: list){
                if(user.getUsername().contains(s)||s.indexOf(user.getUsername())!=-1){
                    changedList.add(user);
                }
            }
        }
        adapter.refreshDataSource(changedList);
    }

    public void loadFavorite(Status status){
        BmobQuery<User> statusQuery = new BmobQuery<>();
        statusQuery.addWhereRelatedTo("likes",new BmobPointer(status));
        statusQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> data, BmobException e) {
                list.addAll(data);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
