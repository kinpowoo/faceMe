package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.User;

import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by anzhuo on 2016/11/1.
 */

public class AddFriendNum extends Activity implements View.OnClickListener {
    private ImageView iv_back;
    private EditText et_FaceMe_Num;
    private ImageView search;
    private TextView tv_FaceMe_Num;
    private ImageButton add;
    private User userIncoming;
    private TextView search_result;
    private User currentUser=Utils.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend_num);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        et_FaceMe_Num = (EditText) findViewById(R.id.et_FaceMe_Num);
        search = (ImageView) findViewById(R.id.search);
        tv_FaceMe_Num = (TextView) findViewById(R.id.tv_FaceMe_Num);
        add = (ImageButton) findViewById(R.id.add);
        search_result = (TextView) findViewById(R.id.search_result);

        iv_back.setOnClickListener(this);
        search.setOnClickListener(this);
        add.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.search:
                search_result.setVisibility(View.VISIBLE);
                String faceme_num = et_FaceMe_Num.getText().toString().trim();//输入的FaceMe账号
                if (faceme_num.equals("")) {
                    tv_FaceMe_Num.setText("输入账号有误！无法查询...");
                } else {
                    if (Utils.getCurrentUser().getUsername().equals(faceme_num)) {
                        tv_FaceMe_Num.setText(faceme_num);
                        add.setVisibility(View.VISIBLE);
                    } else {
                        tv_FaceMe_Num.setText("账号不存在，请从新输入...");
                    }
                }


                break;
            case R.id.add:
                BmobRelation relation = new BmobRelation();
                relation.add(userIncoming);
                currentUser.setFollowing(relation);
                currentUser.setFollowerNum(currentUser.getFollowingNum()+1);
                currentUser.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            userIncoming.setFollowing(false);
                            userIncoming.update();
                            startActivity(new Intent(AddFriendNum.this, MainActivity.class));
                        } else {
                            Toast.makeText(AddFriendNum.this,"关注失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });



                break;
        }
    }
}
