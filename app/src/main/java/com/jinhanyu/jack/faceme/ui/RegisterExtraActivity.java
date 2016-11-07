package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.CustomProgress;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by anzhuo on 2016/11/7.
 */

public class RegisterExtraActivity extends Activity {

    SimpleDraweeView take_photo;
    Switch gender_switch;
    EditText et_nickname;

    File portrait_file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_extra);
        take_photo = (SimpleDraweeView) findViewById(R.id.take_photo);
        gender_switch = (Switch) findViewById(R.id.gender_switch);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
    }

    public void goBack(View view) {
        finish();
    }


    //调用手机摄像头
    public void gototakephoto(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 1);
    }

    //重写onActivityResult方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                return;
            }
            FileOutputStream b = null;
            String str = null;
            Date date = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");//获取当前时间，进一步转化为字符串
            date = new Date();
            str = format.format(date);
            File dir = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"myImage");
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file = new File(dir,str+".jpg");

            try {
                b = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100, b);// 把数据写入文件
                portrait_file = file;
                Log.i("path","file://"+file.getAbsolutePath());
                take_photo.setImageURI("file://"+file.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void commit(View view) {
        final String nickname = et_nickname.getText().toString().trim();
        if(TextUtils.isEmpty(nickname)){
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomProgress.show(this,"正在上传...");
        final User user = new User();
        if(portrait_file==null){
            user.setGender(gender_switch.isChecked()? "男" : "女");
            user.setNickname(nickname);
            user.update(Utils.getCurrentUser().getObjectId(),new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    Utils.getCurrentUser().setNickname(nickname);
                    Utils.getCurrentUser().setGender(gender_switch.isChecked()? "男" : "女");
                    CustomProgress.unshow();
                    startActivity(new Intent(RegisterExtraActivity.this,MainActivity.class));
                }
            });
        }else{
            final BmobFile portrait = new BmobFile(portrait_file);
            portrait.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    user.setGender(gender_switch.isChecked()? "男" : "女");
                    user.setNickname(nickname);
                    user.setPortrait(portrait);
                    user.update(Utils.getCurrentUser().getObjectId(),new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            Utils.getCurrentUser().setNickname(nickname);
                            Utils.getCurrentUser().setGender(gender_switch.isChecked()? "男" : "女");
                            Utils.getCurrentUser().setPortrait(portrait);
                            CustomProgress.unshow();
                            startActivity(new Intent(RegisterExtraActivity.this,MainActivity.class));
                        }
                    });
                }
            });
        }



    }
}
