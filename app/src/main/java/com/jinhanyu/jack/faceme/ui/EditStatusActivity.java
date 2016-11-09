package com.jinhanyu.jack.faceme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.entity.Status;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_status_activity);
        cancel= (TextView) findViewById(R.id.tv_edit_status_cancel);
        commit= (TextView) findViewById(R.id.tv_edit_status_commit);
        username= (TextView) findViewById(R.id.tv_edit_status_username);
        userPortrait= (SimpleDraweeView) findViewById(R.id.sdv_edit_status_userPortrait);
        statusPhoto= (SimpleDraweeView) findViewById(R.id.sdv_edit_status_statusPhoto);
        statusText= (EditText) findViewById(R.id.et_edit_status_statusText);


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
                finish();
                break;
            case R.id.tv_edit_status_commit:
                String afterEdit=statusText.getText().toString();
                final Status st=new Status();
                st.setText(afterEdit);
                st.update(status.getObjectId(),new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        startActivity(new Intent(EditStatusActivity.this,
                                SingleStatusActivity.class).putExtra("statusId",status.getObjectId()));
                    }
                });
                break;
        }
    }
}
