package com.jinhanyu.jack.faceme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.ScreenUtils;
import com.jinhanyu.jack.faceme.entity.Status;
import com.sun.mail.imap.protocol.INTERNALDATE;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by jianbo on 2016/10/22.
 */
public class EditStatusActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView cancel,commit,username;
    private SimpleDraweeView userPortrait,statusPhoto;
    private EditText statusText;
    private String statusId;
    private Status status;
    private LinearLayout.LayoutParams params;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_status_activity);
        int width= ScreenUtils.getScreenWidth(this);
        params = new LinearLayout.LayoutParams(width,width);
        cancel= (TextView) findViewById(R.id.tv_edit_status_cancel);
        commit= (TextView) findViewById(R.id.tv_edit_status_commit);
        username= (TextView) findViewById(R.id.tv_edit_status_username);
        userPortrait= (SimpleDraweeView) findViewById(R.id.sdv_edit_status_userPortrait);
        statusPhoto= (SimpleDraweeView) findViewById(R.id.sdv_edit_status_statusPhoto);
        statusText= (EditText) findViewById(R.id.et_edit_status_statusText);
        statusPhoto.setLayoutParams(params);
        statusPhoto.setScaleType(ImageView.ScaleType.FIT_XY);

        cancel.setOnClickListener(this);
        commit.setOnClickListener(this);


        statusId=getIntent().getStringExtra("statusId");
        if(statusId!=null){
            BmobQuery<Status> query=new BmobQuery<>();
            query.include("author");
            query.getObject(statusId, new QueryListener<Status>() {
                @Override
                public void done(Status st, BmobException e) {
                    if(e==null){
                        status=st;
                        fillData(st);
                    }
                }
            });

        }
    }

    public void fillData(Status status){
        userPortrait.setImageURI(status.getAuthor().getPortrait().getUrl());
        username.setText(status.getAuthor().getUsername());
        statusPhoto.setImageURI(status.getPhoto().getUrl());
        statusText.setText(status.getText());
        statusText.requestFocus();
        statusText.setSelection(statusText.getText().length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_edit_status_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.tv_edit_status_commit:
                final String afterEdit=statusText.getText().toString();
                if(TextUtils.isEmpty(afterEdit)){
                    Toast.makeText(EditStatusActivity.this,"内容不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                final Status st=new Status();
                st.setText(afterEdit);
                st.update(status.getObjectId(),new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Intent intent = new Intent(EditStatusActivity.this, SingleStatusActivity.class);
                            intent.putExtra("content",afterEdit);
                            setResult(RESULT_OK, intent);
                            finish();
                        }else{
                            Toast.makeText(EditStatusActivity.this,"更新失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }
}
