package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.ClearEditText;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by anzhuo on 2016/11/1.
 */

public class AddFriendNum extends Activity implements View.OnClickListener{
    private ClearEditText account;
    private ImageView search,back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend_num);
        account= (ClearEditText) findViewById(R.id.cet_addFriend_account);
        search= (ImageView) findViewById(R.id.iv_addFriend_search);
        back= (ImageView) findViewById(R.id.iv_addFriend_back);

        search.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_addFriend_search:
                final String accountNum=account.getText().toString();
                if(accountNum==null||accountNum.equals("")){
                    Toast.makeText(AddFriendNum.this,"请输入你要查找的用户名",Toast.LENGTH_SHORT).show();
                }else {
                    BmobQuery<User> query = new BmobQuery<>();
                    query.addWhereEqualTo("username", accountNum);
                    query.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if (e == null) {
                                if (list == null) {
                                    Toast.makeText(AddFriendNum.this, "没有找到改用户", Toast.LENGTH_LONG).show();
                                } else {
                                    Intent intent=new Intent(AddFriendNum.this,LikesActivity.class);
                                    Bundle bundle=new Bundle();
                                    bundle.putString("type","searchResult");
                                    bundle.putString("result",accountNum);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }
                        }
                    });
                }
                break;
            case R.id.iv_addFriend_back:
                finish();
                break;
        }
    }
}
