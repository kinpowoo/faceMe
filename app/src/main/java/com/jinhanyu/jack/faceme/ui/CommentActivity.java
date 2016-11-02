package com.jinhanyu.jack.faceme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.adapter.CommentAdapter;
import com.jinhanyu.jack.faceme.entity.Comment;
import com.jinhanyu.jack.faceme.entity.Status;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.jinhanyu.jack.faceme.R.id.et_comment_content;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class CommentActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView back,atPeople,send;
    private EditText commentContent;
    private SimpleDraweeView userPortrait;
    private TextView username,statusText;
    private ListView listView;
    private CommentAdapter adapter;
    private List<Comment> list;
    private String statusId;
    private Status status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);
        back= (ImageView) findViewById(R.id.iv_comment_back);
        atPeople= (ImageView) findViewById(R.id.iv_comment_atPeople);
        send= (ImageView) findViewById(R.id.iv_comment_send);
        commentContent= (EditText) findViewById(et_comment_content);
        userPortrait= (SimpleDraweeView) findViewById(R.id.sdv_comment_userPortrait);
        username= (TextView) findViewById(R.id.tv_comment_username);
        statusText= (TextView) findViewById(R.id.tv_comment_text);
        listView= (ListView) findViewById(R.id.lv_comment);
        list=new ArrayList<>();
        adapter=new CommentAdapter(list,this);
        listView.setAdapter(adapter);

        userPortrait.setOnClickListener(this);
        username.setOnClickListener(this);
        back.setOnClickListener(this);
        atPeople.setOnClickListener(this);
        send.setOnClickListener(this);

        statusId=getIntent().getStringExtra("statusId");
        if(statusId!=null) {
            BmobQuery<Status> query = new BmobQuery<>();
            query.include("author");
            query.getObject(statusId, new QueryListener<Status>() {
                @Override
                public void done(Status st, BmobException e) {
                    status = st;
                    loadComment(st);
                    username.setText(status.getAuthor().getUsername());
                    userPortrait.setImageURI(status.getAuthor().getPortrait().getUrl());
                    statusText.setText(status.getText());
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.iv_comment_back:
              finish();
           break;
           case R.id.iv_comment_atPeople:
               break;
           case R.id.iv_comment_send:
               addComment();
               commentContent.setText("");
               break;
           case R.id.sdv_comment_userPortrait:
               Intent intent=new Intent(this,UserProfileActivity.class);
               intent.putExtra("userId",status.getAuthor().getObjectId());
               startActivity(intent);
              break;
           case R.id.tv_comment_username:
               Intent intent2=new Intent(this,UserProfileActivity.class);
               intent2.putExtra("userId",status.getAuthor().getObjectId());
               startActivity(intent2);
               break;
       }
    }

    public void addComment(){
        String comment=commentContent.getText().toString();
        if(comment!=null&&comment.length()>0){
           Comment com=new Comment();
            com.setCommentor(Utils.getCurrentUser());
            com.setText(comment);
            com.setReplyToUser(status.getAuthor());
            com.setToStatus(status);
            com.save(new SaveListener<String>() {
              @Override
              public void done(String s, BmobException e) {
                  if(e==null){
//                      Toast.makeText(CommentActivity.this,s,Toast.LENGTH_LONG).show();
                      loadComment(status);
                  }
              }
          });
            status.setCommentNum(status.getCommentNum()+1);
            status.update(status.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                }
            });
        }
    }


   public void loadComment(Status status){
       BmobQuery<Comment> commentBmobQuery = new BmobQuery<>();
       commentBmobQuery.addWhereEqualTo("toStatus", new BmobPointer(status));
       commentBmobQuery.include("commentor,replyToUser");

       commentBmobQuery.findObjects(new FindListener<Comment>() {
           @Override
           public void done(List<Comment> data, BmobException e) {
               list.clear();
               list.addAll(data);
               adapter.notifyDataSetChanged();

           }
       });
   }
}
