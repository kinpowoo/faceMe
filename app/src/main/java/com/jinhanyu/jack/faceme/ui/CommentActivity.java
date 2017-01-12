package com.jinhanyu.jack.faceme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.adapter.AtPeopleAdapter;
import com.jinhanyu.jack.faceme.adapter.CommentAdapter;
import com.jinhanyu.jack.faceme.entity.Comment;
import com.jinhanyu.jack.faceme.entity.Status;
import com.jinhanyu.jack.faceme.entity.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
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
public class CommentActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,AdapterView.OnItemLongClickListener,
        TextWatcher,AdapterView.OnItemClickListener,AbsListView.OnScrollListener{
    private final int SKIPMOUNT=20;
    private int pageno=0;
    private int refreshCount=0;
    private ImageView back,send;
    private CheckBox atPeople;
    private EditText commentContent;
    private SimpleDraweeView userPortrait;
    private TextView nickname,statusText;
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
    private String lastFetchDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);
        back= (ImageView) findViewById(R.id.iv_comment_back);
        atPeople= (CheckBox) findViewById(R.id.iv_comment_atPeople);
        send= (ImageView) findViewById(R.id.iv_comment_send);
        commentContent= (EditText) findViewById(et_comment_content);
        userPortrait= (SimpleDraweeView) findViewById(R.id.sdv_comment_userPortrait);
        nickname= (TextView) findViewById(R.id.tv_comment_username);
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
        nickname.setOnClickListener(this);
        back.setOnClickListener(this);
        atPeople.setOnCheckedChangeListener(this);
        send.setOnClickListener(this);
        commentContent.addTextChangedListener(this);

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setOnScrollListener(this);

        statusId=getIntent().getStringExtra("statusId");
        if(statusId!=null) {
            BmobQuery<Status> query = new BmobQuery<>();
            query.include("author");
            query.getObject(statusId, new QueryListener<Status>() {
                @Override
                public void done(Status st, BmobException e) {
                    status = st;
                    loadComment(st);
                    nickname.setText(status.getAuthor().getNickname());
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
                commentorName=toWho.getNickname();
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
                                      refreshComment(status);
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
                                    refreshComment(status);
                                }
                            });
                        }
                    }
                });

            }
        }

    }

    public void refreshComment(Status status){
        BmobQuery<Comment> commentBmobQuery = new BmobQuery<>();
        commentBmobQuery.addWhereEqualTo("toStatus", new BmobPointer(status));
        commentBmobQuery.include("commentor,replyToUser");
        commentBmobQuery.order("-createdAt");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date= null;
        try {
            date = sdf.parse(lastFetchDate);
            date.setTime(date.getTime()+1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        commentBmobQuery.addWhereGreaterThan("createdAt",new BmobDate(date));
        commentBmobQuery.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> data, BmobException e) {
                if(e==null&data.size()>0){
                    refreshCount+=data.size();
                    lastFetchDate = data.get(0).getCreatedAt();
                    list.addAll(0,data);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

   public void loadComment(Status status){
       BmobQuery<Comment> commentBmobQuery = new BmobQuery<>();
       commentBmobQuery.addWhereEqualTo("toStatus", new BmobPointer(status));
       commentBmobQuery.include("commentor,replyToUser");
       commentBmobQuery.order("-createdAt");
       commentBmobQuery.setSkip(SKIPMOUNT*pageno+refreshCount);
       commentBmobQuery.setLimit(20);
       commentBmobQuery.findObjects(new FindListener<Comment>() {
           @Override
           public void done(List<Comment> data, BmobException e) {
               if(data!=null&&e==null){
                   pageno++;
                   lastFetchDate=data.get(0).getCreatedAt();
                   list.addAll(data);
                   adapter.notifyDataSetChanged();
               }
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
        if(!list.get(position).getCommentor().getNickname().equals(Utils.getCurrentUser().getNickname())) {
            Pattern pattern=Pattern.compile("@.+?\0");
            String comment=commentContent.getText().toString();
            final Matcher matcher=pattern.matcher(comment);
            if(!matcher.find()&&(comment==null||comment.equals(""))) {
                String commentorName = list.get(position).getCommentor().getNickname();
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
            String name = atPeopleList.get(position).getNickname();
            String afterAt = "@" + name + "\0" + " ";
            toWho = atPeopleList.get(position);
            Utils.setETColor(afterAt, 0, afterAt.length(), getResources().getColor(R.color.Blue), commentContent);
            commentContent.setSelection(afterAt.length());
            statusInfo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        getFollowingList();
        super.onRestart();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(i==SCROLL_STATE_FLING){
            if(absListView.getLastVisiblePosition()==absListView.getCount()-1){
                loadComment(status);
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
    }
}
