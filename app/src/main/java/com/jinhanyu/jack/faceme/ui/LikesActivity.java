package com.jinhanyu.jack.faceme.ui;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.jinhanyu.jack.faceme.ClearEditText;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.adapter.LikesAdapter;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianbo on 2016/10/19.
 */
public class LikesActivity extends AppCompatActivity implements TextWatcher,View.OnClickListener{
    private ListView listView;
    private List<User> list;
    private LikesAdapter adapter;
    private ClearEditText search;
    private ImageView back;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.likes_activity);
        listView= (ListView) findViewById(R.id.lv_likes);
        search= (ClearEditText) findViewById(R.id.cet_likes_search);
        back= (ImageView) findViewById(R.id.iv_likes_back);
        Utils.setListViewHeightBasedOnChildren(listView);
        list=new ArrayList<>();
        adapter=new LikesAdapter(list,this);

        back.setOnClickListener(this);
        search.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

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
}
