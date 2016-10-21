package com.jinhanyu.jack.faceme.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.adapter.CommentAdapter;
import com.jinhanyu.jack.faceme.entity.Comment;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
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
                }
            });
        }
        username.setText(status.getAuthor().getUsername());
        userPortrait.setImageURI(Uri.parse(status.getAuthor().getPortrait()));
        statusText.setText(status.getText());
        loadComment();
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
               break;


       }
    }

    public void addComment(){
        String comment=commentContent.getText().toString();
        if(comment!=null&&comment.length()>0){
           Comment com=new Comment();
            com.setCommentor(Utils.getCurrentUser());
            com.setText(comment);
            com.setToUser(status.getAuthor());
            com.setToStatus(status);
            com.save(new SaveListener<String>() {
              @Override
              public void done(String s, BmobException e) {
                  if(e==null){
                      Toast.makeText(CommentActivity.this,s,Toast.LENGTH_LONG).show();
                  }
              }
          });

            BmobRelation relation=new BmobRelation();
            relation.add(com);
            status.setComments(relation);
            status.increment("commentsNum");
            status.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                   loadComment();
                }
            });

        }
    }


   public void loadComment(){
       BmobQuery<Comment> query=new BmobQuery<>();
       query.addWhereRelatedTo("comments",new BmobPointer(status));
       query.include("commentor");
       query.findObjects(new FindListener<Comment>() {
           @Override
           public void done(List<Comment> data, BmobException e) {
               if(e==null){
                   list.addAll(data);
                   Collections.sort(list);
                   adapter.notifyDataSetChanged();
               }
           }
       });
   }
}
