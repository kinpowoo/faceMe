package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.adapter.TagAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by anzhuo on 2016/11/8.
 */

public class CustomTag extends Activity implements View.OnClickListener {
    private List<String> list;
    private TagAdapter adapter;//适配器
    private GridView searchTag_gv;
    private ImageView iv_back;//返回
    private EditText search_content;//添加的内容
    private TextView tv_search, tv_title, search_title;//添加
    private String FILE = "custom_tag";//用于保存SharedPreferences的文件
    SharedPreferences sp;
    SharedPreferences.Editor editor;



    public void removeTag(String tag){
        Set<String> searches = sp.getStringSet("search", new HashSet<String>());
        searches.remove(tag);
        editor.putStringSet("search", searches);
        editor.commit();

        list.remove(tag);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_tag);



        //初始化控件
        iv_back = (ImageView) findViewById(R.id.iv_back);
        search_content = (EditText) findViewById(R.id.search_content);
        tv_search = (TextView) findViewById(R.id.tv_search);
        searchTag_gv = (GridView) findViewById(R.id.searchTag_gv);
        tv_title = (TextView) findViewById(R.id.tv_title);
        search_title = (TextView) findViewById(R.id.search_title);
        list = new ArrayList<>();


        iv_back.setOnClickListener(this);
        tv_search.setOnClickListener(this);

        sp = getSharedPreferences(FILE, MODE_PRIVATE);
        editor = sp.edit();
        Set<String> searches = sp.getStringSet("search", new HashSet<String>());
        list.addAll(searches);
        adapter = new TagAdapter(list, this);
        searchTag_gv.setAdapter(adapter);


        searchTag_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                intent.putExtra("content", list.get(position));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        searchTag_gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.enterEditMode();
                return true;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_search:
                    String search_text = search_content.getText().toString().trim();
                    if (search_text.equals("")) {
                        Toast.makeText(CustomTag.this, "添加内容不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        Set<String> searches = sp.getStringSet("search", new HashSet<String>());
                        searches.add(search_text);
                        editor.putStringSet("search", searches);
                        editor.commit();
                        Intent intent = getIntent();
                        intent.putExtra("content", search_content.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                break;
        }
    }
}

