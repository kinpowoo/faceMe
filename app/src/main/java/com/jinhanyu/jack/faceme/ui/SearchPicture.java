package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.adapter.FlowAdapter;
import com.jinhanyu.jack.faceme.entity.FlowSearchItem;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by anzhuo on 2016/10/24.
 */
public class SearchPicture extends Activity implements View.OnClickListener {
    private FlowSearchItem flowSearchItem;//数据源
    private List<FlowSearchItem> mlist;
    private FlowAdapter adapter;//适配器
    private GridView searchTag_gv;
    private ImageView iv_back;//返回
    private EditText search_content;//搜索的内容
    private TextView tv_search;//开始搜索

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_tag);

        //初始化控件
        iv_back = (ImageView) findViewById(R.id.iv_back);
        search_content = (EditText) findViewById(R.id.search_content);
        tv_search = (TextView) findViewById(R.id.tv_search);
        searchTag_gv = (GridView) findViewById(R.id.searchTag_gv);
        mlist = new ArrayList<>();
        flowSearchItem = new FlowSearchItem();


        iv_back.setOnClickListener(this);
        tv_search.setOnClickListener(this);


        adapter = new FlowAdapter(mlist, this);
        searchTag_gv.setAdapter(adapter);

        searchTag_gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final FlowSearchItem flowSearchItem = mlist.get(position);

                flowSearchItem.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(SearchPicture.this, "删除成功" + flowSearchItem.getUpdatedAt(), Toast.LENGTH_SHORT).show();
                            mlist.remove(position);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SearchPicture.this, "删除失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                return false;
            }
        });

        new BmobQuery<FlowSearchItem>().findObjects(new FindListener<FlowSearchItem>() {
            @Override
            public void done(List<FlowSearchItem> list, BmobException e) {
                mlist.add((FlowSearchItem) list);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
            case R.id.tv_search:
                if (search_content.getText().toString().trim().equals("")) {
                    Toast.makeText(SearchPicture.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    final FlowSearchItem flowSearchItem = new FlowSearchItem();
                    flowSearchItem.setText(search_content.getText().toString());
                    flowSearchItem.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                mlist.add(flowSearchItem);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(SearchPicture.this, "搜索失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                break;
        }
    }
}
