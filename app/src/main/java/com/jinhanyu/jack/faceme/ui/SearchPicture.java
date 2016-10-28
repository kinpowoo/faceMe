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
 * Created by anzhuo on 2016/10/24.陈礼
 */
public class SearchPicture extends Activity implements View.OnClickListener {
    private List<String> list;
    private TagAdapter adapter;//适配器
    private GridView searchTag_gv;
    private ImageView iv_back;//返回
    private EditText search_content;//搜索的内容
    private TextView tv_search;//开始搜索
    private String FILE = "recent_search";//用于保存SharedPreferences的文件
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_tag);

        //初始化控件
        iv_back = (ImageView) findViewById(R.id.iv_back);
        search_content = (EditText) findViewById(R.id.search_content);
        tv_search = (TextView) findViewById(R.id.tv_search);
        searchTag_gv = (GridView) findViewById(R.id.searchTag_gv);
        list = new ArrayList<>();



        iv_back.setOnClickListener(this);
        tv_search.setOnClickListener(this);

        sp= getSharedPreferences(FILE, MODE_PRIVATE);
        editor = sp.edit();
        Set<String> searches = sp.getStringSet("search",new HashSet<String>());
        list.addAll(searches);
        adapter = new TagAdapter(list, this);
        searchTag_gv.setAdapter(adapter);
        searchTag_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              Toast.makeText(SearchPicture.this,"wocao",Toast.LENGTH_SHORT).show();
                  search(list.get(position));
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
                    Toast.makeText(SearchPicture.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    //跳到搜索页面
                    search(search_text);
                    Set<String> searches = sp.getStringSet("search",new HashSet<String>());
                    searches.add(search_text);
                    editor.putStringSet("search",searches);
                    editor.commit();
                }
                break;
        }
    }

    public void search(String text){
          startActivity(new Intent(SearchPicture.this,SearchResultActivity.class).putExtra("search_text",text));
    }
}
