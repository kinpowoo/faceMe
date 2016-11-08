package com.jinhanyu.jack.faceme.ui;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.ScreenUtils;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.adapter.AtPeopleAdapter;
import com.jinhanyu.jack.faceme.adapter.CommentAdapter;
import com.jinhanyu.jack.faceme.adapter.CommonAdapter;
import com.jinhanyu.jack.faceme.entity.Comment;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.jinhanyu.jack.faceme.R.id.et_comment_content;
import static com.jinhanyu.jack.faceme.R.id.listview;
import static com.jinhanyu.jack.faceme.R.id.parallax;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class CommentActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,AdapterView.OnItemLongClickListener,
        TextWatcher,AdapterView.OnItemClickListener{
    private ImageView back,send;
    private CheckBox atPeople;
    private EditText commentContent;
    private SimpleDraweeView userPortrait;
    private TextView username,statusText;
    private ListView listView;
    private CommentAdapter adapter;
    private AtPeopleAdapter atPeopleAdapter;
    private List<Comment> list;
    private List<User> followingList,atPeopleList;
    private String statusId;
    private Status status;
    private boolean commentOther=false;
    private User toWho;
    private String commentorName;
    private String name;
    private LinearLayout statusInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);
        back= (ImageView) findViewById(R.id.iv_comment_back);
        atPeople= (CheckBox) findViewById(R.id.iv_comment_atPeople);
        send= (ImageView) findViewById(R.id.iv_comment_send);
        commentContent= (EditText) findViewById(et_comment_content);
        userPortrait= (SimpleDraweeView) findViewById(R.id.sdv_comment_userPortrait);
        username= (TextView) findViewById(R.id.tv_comment_username);
        statusText= (TextView) findViewById(R.id.tv_comment_text);
        listView= (ListView) findViewById(R.id.lv_comment);
        statusInfo= (LinearLayout) findViewById(R.id.ll_status_info);
        list=new ArrayList<>();
        adapter=new CommentAdapter(this,list);
        listView.setAdapter(adapter);

        getFollowingList();
        atPeopleList=new ArrayList<>();
        atPeopleAdapter=new AtPeopleAdapter(atPeopleList,this);

        userPortrait.setOnClickListener(this);
        username.setOnClickListener(this);
        back.setOnClickListener(this);
        atPeople.setOnCheckedChangeListener(this);
        send.setOnClickListener(this);
        commentContent.addTextChangedListener(this);

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

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

    public void getFollowingList(){
        followingList=new ArrayList<>();
     BmobQuery<User> query=new BmobQuery<>();
        query.addWhereRelatedTo("following",new BmobPointer(Utils.getCurrentUser()));
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
               followingList.addAll(list);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_comment_back:
                finish();
                break;
            case R.id.iv_comment_send:
                addComment();
                commentContent.setText("");
                break;
            case R.id.sdv_comment_userPortrait:
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("userId", status.getAuthor().getObjectId());
                startActivity(intent);
                break;
            case R.id.tv_comment_username:
                Intent intent2 = new Intent(this, UserProfileActivity.class);
                intent2.putExtra("userId", status.getAuthor().getObjectId());
                startActivity(intent2);
                break;
            case R.id.iv_comment_item_atPeople:
                toWho=list.get(listView.pointToPosition(v.getScrollX(),v.getScrollY())).getCommentor();
                commentorName=toWho.getUsername();
                Pattern pattern = Pattern.compile("@.+?\0");
                String con = commentContent.getText().toString();
                Matcher matcher = pattern.matcher(con);
                if (!matcher.find() && (con == null || con.equals(""))) {
                    commentContent.setText("@" + commentorName + '\0' + " ");
                    Utils.setETColor(commentContent.getText().toString(), 0, commentorName.length() + 1
                            , commentContent.getResources().getColor(R.color.BlueViolet), commentContent);
                    commentContent.setSelection(commentContent.getText().length());

                }
                break;
        }
    }

    public void addComment(){
        final String comment=commentContent.getText().toString();
        Pattern pattern=Pattern.compile("@.+?\0");
        final Matcher matcher=pattern.matcher(comment);
        if(matcher.find()){
            commentOther=true;
                  Comment com=new Comment();
                  com.setCommentor(Utils.getCurrentUser());
                  com.setText(comment);
                  com.setReplyToUser(toWho);
                  com.setToStatus(status);
                  com.save(new SaveListener<String>() {
                      @Override
                      public void done(String s, BmobException e) {
                          if(e==null){
                              loadComment(status);
                              status.increment("commentNum");
                              status.update(status.getObjectId(), new UpdateListener() {
                                  @Override
                                  public void done(BmobException e) {
                                  }
                              });
                          }
                      }
                  });
        }
        if(commentOther==false){
            if(comment!=null&&comment.length()>0&&commentOther==false){
                Comment com=new Comment();
                com.setCommentor(Utils.getCurrentUser());
                com.setText(comment);
                com.setReplyToUser(status.getAuthor());
                com.setToStatus(status);
                com.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            loadComment(status);
                            status.increment("commentNum");
                            status.update(status.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                }
                            });
                        }
                    }
                });

            }
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
               Collections.sort(list);
               adapter.notifyDataSetChanged();
           }
       });
   }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String content=commentContent.getText().toString();
        if(isChecked){
            listView.setAdapter(atPeopleAdapter);

            if(content==null||content.equals("")){
                commentContent.setText("@");
                commentContent.setSelection(commentContent.getText().length());
            }
        }else {
            statusInfo.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
            if(content.length()==1&&content.charAt(0)=='@'){
                commentContent.setText("");
            }
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(!list.get(position).getCommentor().getUsername().equals(Utils.getCurrentUser().getUsername())) {
            Pattern pattern=Pattern.compile("@.+?\0");
            String comment=commentContent.getText().toString();
            final Matcher matcher=pattern.matcher(comment);
            if(!matcher.find()&&(comment==null||comment.equals(""))) {
                String commentorName = list.get(position).getCommentor().getUsername();
                commentContent.setText(comment+ "@" + commentorName + '\0' + " ");
                Utils.setETColor(commentContent.getText().toString(), 0, commentorName.length() + 1
                        , getResources().getColor(R.color.BlueViolet), commentContent);
                commentContent.setSelection(commentContent.getText().length());
                toWho = list.get(position).getCommentor();
            }
        }
        return false;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
           if(s.length()==1&&s.charAt(0)=='@'){
               statusInfo.setVisibility(View.GONE);
               atPeople.setChecked(true);
               atPeopleList.clear();
               atPeopleAdapter.notifyDataSetChanged();


           }else {
               if(atPeople.isChecked()){
                   atPeople.setChecked(false);
               }
           }
        if(s.length()>1&&s.charAt(0)=='@') {
            Pattern pattern = Pattern.compile("@.+?\0\\s");
            String comment = commentContent.getText().toString();
            Matcher matcher = pattern.matcher(comment);
            if (matcher.find()) {
                listView.setAdapter(adapter);
            }
            else {
                statusInfo.setVisibility(View.GONE);
                listView.setAdapter(atPeopleAdapter);

                if (!atPeople.isChecked()) {
                    atPeople.setChecked(true);
                }
                atPeopleList.clear();
                name = s.toString().substring(1, s.length());
                for (User user : followingList) {
                    if (user.getUsername().contains(name)) {
                        atPeopleList.add(user);
                        atPeopleAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.equals("")||s==null){
            statusInfo.setVisibility(View.VISIBLE);
            if(atPeople.isChecked()){
             atPeople.setChecked(false);
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getAdapter() instanceof AtPeopleAdapter) {
            String name = atPeopleList.get(position).getUsername();
            String afterAt = "@" + name + "\0" + " ";
            toWho = atPeopleList.get(position);
            Utils.setETColor(afterAt, 0, afterAt.length(), getResources().getColor(R.color.Blue), commentContent);
            commentContent.setSelection(afterAt.length());
            statusInfo.setVisibility(View.VISIBLE);
        }
    }
}
