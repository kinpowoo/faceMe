package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.jinhanyu.jack.faceme.CustomProgress;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.SelectableFaceMePopupWindow;
import com.jinhanyu.jack.faceme.adapter.UserLogsListAdapter;
import com.jinhanyu.jack.faceme.entity.User;
import com.jinhanyu.jack.faceme.tool.ACache;
import com.jinhanyu.jack.faceme.tool.ConstantFunc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by anzhuo on 2016/10/18.
 */
public class LoginActivity extends Activity {

    EditText et_username;
    EditText et_password;
    CheckBox remember;
    private PopupWindow userLogsWindow;
    private View win;
    private ListView userListView;
    private List<String> usersList;
    private UserLogsListAdapter userAdapter;


    private static SharedPreferences savePass;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initSharedPreference();

        et_password = (EditText) findViewById(R.id.et_password);
        et_username = (EditText) findViewById(R.id.et_username);
        remember = (CheckBox) findViewById(R.id.remember_pass);


        et_username.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (et_username.getCompoundDrawables()[2] == null) {
                    return false;
                }

                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }

                if (event.getX() > et_username.getWidth() - et_username.getCompoundDrawables()[2].getBounds().width()) {
                    if (userLogsWindow == null) {
                        win = LayoutInflater.from(LoginActivity.this).inflate(R.layout.login_record_dialog, null);
                        userLogsWindow = new PopupWindow(win, et_username.getWidth(), 400);
                        userListView = (ListView) win.findViewById(R.id.user_list);
                        usersList = (List<String>) ACache.with(LoginActivity.this).readObject("user_logs");
                        if (usersList == null) {
                            usersList = new ArrayList<>();
                        }

                        userAdapter = new UserLogsListAdapter(usersList,LoginActivity.this);
                        userListView.setAdapter(userAdapter);
                    }

                    userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            et_username.setText(usersList.get(position));
                            et_username.setSelection(et_username.getText().toString().length());
                            userLogsWindow.dismiss();
                        }
                    });
                    userLogsWindow.setBackgroundDrawable(new BitmapDrawable());
                    userLogsWindow.setFocusable(true);
                    userLogsWindow.setOutsideTouchable(true);
                    userLogsWindow.showAsDropDown(et_username,0,0, Gravity.CENTER);
                    return true;
                }

                return false;
            }
        });
    }




    public void initSharedPreference() {
        savePass = getSharedPreferences("savePass", MODE_PRIVATE);
        editor = savePass.edit();
    }


    @Override
    protected void onResume() {
        et_username.setText(savePass.getString("phoneNumber", ""));
        et_password.setText(savePass.getString("password", ""));
        boolean isRemember = savePass.getBoolean("isRemember", false);
        remember.setChecked(isRemember);
        et_username.setSelection(et_username.getText().length());
        super.onResume();
    }

    public void login(View view) {
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }


        editor.putString("phoneNumber", username);
        if (remember.isChecked()) {
            editor.putString("password", password);
            editor.putBoolean("isRemember", true);
            editor.commit();
        } else {
            if (savePass != null && !ConstantFunc.isEmpty(savePass.getString("password", ""))) {
                editor.remove("password");
                editor.putBoolean("isRemember", false);
                editor.commit();
            }
        }


        CustomProgress.show(this,"正在登陆...");

        User.loginByAccount(username, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {

                CustomProgress.unshow();
                if (e == null) {
                    List<String> userLogs = null;
                    if(userAdapter!=null){
                        userLogs = userAdapter.getList();
                    }
                    if(userLogs==null){
                        userLogs = new ArrayList<>();
                        userLogs.add(user.getUsername());
                    }else {
                        Set<String> logSet = ConstantFunc.listToSet(userLogs);
                        logSet.add(user.getUsername());
                        userLogs = ConstantFunc.setToList(logSet);
                    }

                    ACache.with(LoginActivity.this).saveObject(userLogs, "user_logs", new ACache.SaveFinishListener() {
                        @Override
                        public void saveFinish(File path) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    });

                } else {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void forgetPassword(View view) {

        new SelectableFaceMePopupWindow(this)
                .setTitle("请选择")
                .addOption("电子邮箱找回密码", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LoginActivity.this, FindPasswordByEmailActivity.class));
                    }
                })
                .addOption("手机号码找回密码", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LoginActivity.this, FindPasswordByPhoneActivity.class));
                    }
                })
                .show(view);
    }

    public void register(View view) {
          startActivity(new Intent(this,RegisterByPhoneActivity.class));
    }
}
