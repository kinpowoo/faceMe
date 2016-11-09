package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.PassAuthenticator;
import com.jinhanyu.jack.faceme.R;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.Properties;


/**
 * Created by anzhuo on 2016/11/8.
 */

public class FeedBackActivity extends Activity implements View.OnClickListener,TextWatcher{
    private ImageView back;
    private EditText content;
    private TextView count;
    private TextView submit;
    private ProgressDialog progressDialog;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what==1){
                progressDialog.dismiss();
                Toast.makeText(FeedBackActivity.this,"消息发送成功",Toast.LENGTH_SHORT).show();
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);

        back= (ImageView) findViewById(R.id.iv_feedback_back);
        content= (EditText) findViewById(R.id.et_feedback_content);
        count= (TextView) findViewById(R.id.tv_feedback_count);
        submit= (TextView) findViewById(R.id.tv_feedback_commit);

        back.setOnClickListener(this);
        content.addTextChangedListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int c) {
        if(s.length()<=140) {
            count.setText("剩余" + (140 - s.length()) + "字");
        }else {
            count.setText("剩余0字");
            content.setText(s.toString().substring(0,140));
            content.requestFocus();
            content.setSelection(140);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_feedback_back:
                finish();
                break;
            case R.id.tv_feedback_commit:
                String text=content.getText().toString();
                if(text.equals("")||text==null){
                    Toast.makeText(this,"内容不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    submit.setEnabled(false);
                    content.setEnabled(false);
                    progressDialog=ProgressDialog.show(this,null,"信息正在发送中...");
                    new Thread(){
                        @Override
                        public void run() {
                            sendEmail();
                        }
                    }.start();
                }
                break;
        }
    }

    private void sendEmail(){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");//设置要验证
        props.put("mail.smtp.host", "smtp-mail.outlook.com");//设置host
        props.put("mail.smtp.port","587");  //设置端口
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust","smtp-mail.outlook.com");
        PassAuthenticator pass = new PassAuthenticator();   //获取帐号密码
        Session session =Session.getDefaultInstance(props,pass); //获取验证会话
        try
        {
            //配置发送及接收邮箱
            InternetAddress fromAddress;
            /**
             * 这个地方需要改成自己的邮箱
             */
            fromAddress = new InternetAddress("kinpowoo@outlook.com","KINPOWOO");

            MimeMessage message = new MimeMessage(session);

            message.setSubject("反馈邮件");
            message.setFrom(fromAddress);
            message.setContent(content.getText().toString(), "text/html;charset=GBK");
//            message.setText(content.getText().toString());
            message.setRecipients(Message.RecipientType.TO,new Address[]{
                    new InternetAddress("kinpowoo@outlook.com"),new InternetAddress("547494937@qq.com"),
                    new InternetAddress("1565771886@qq.com")});
            message.setHeader("Content-Type","text/html");
            message.saveChanges();
            //连接邮箱并发送
            session.setDebug(true);
            Transport transport = session.getTransport("smtp");
            /**
             * 这个地方需要改称自己的账号和密码
             */
            transport.connect("smtp-mail.outlook.com","kinpowoo@outlook.com","3344???a");
            transport.send(message);
            transport.close();
            handler.sendEmptyMessage(1);
            finish();
        } catch (AuthenticationFailedException e) {
            Toast.makeText(this,"auth failed",Toast.LENGTH_SHORT).show();
        }catch (MessagingException mex){
            mex.printStackTrace();
            Exception ex = null;
            if ((ex = mex.getNextException()) != null) {
                ex.printStackTrace();
            }
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }
}
