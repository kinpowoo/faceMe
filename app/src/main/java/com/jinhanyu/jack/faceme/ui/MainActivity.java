package com.jinhanyu.jack.faceme.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.MainApplication;
import com.jinhanyu.jack.faceme.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by anzhuo on 2016/10/18.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private RadioButton rb_mainActivity_mainFragment;
    private RadioButton rb_mainActivity_flowFragment;
    private Button rb_mainActivity_postFragment;
    private RadioButton rb_mainActivity_favoriteFragment;
    private RadioButton rb_mainActivity_userFragment;
    private MainFragment mainFragment;
    private FlowFragment flowFragment;
    private FavoriteFragment favoriteFragment;
    private UserFragment userFragment;
    private ImageView surfaceview;

    //动态加载视图相关
    private View view;
    private PopupWindow popupWindow;
    private TextView camera, photo, cancel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //pop弹窗控件初始化
        view = LayoutInflater.from(MainActivity.this).inflate(R.layout.postchoose, null);
        camera = (TextView) view.findViewById(R.id.camera);
        photo = (TextView) view.findViewById(R.id.photo);
        cancel = (TextView) view.findViewById(R.id.cancel);

        rb_mainActivity_mainFragment = (RadioButton) findViewById(R.id.rb_mainActivity_mainFragment);
        rb_mainActivity_flowFragment = (RadioButton) findViewById(R.id.rb_mainActivity_flowFragment);
        rb_mainActivity_postFragment = (Button) findViewById(R.id.rb_mainActivity_postFragment);
        rb_mainActivity_favoriteFragment = (RadioButton) findViewById(R.id.rb_mainActivity_favoriteFragment);
        rb_mainActivity_userFragment = (RadioButton) findViewById(R.id.rb_mainActivity_userFragment);
        surfaceview = (ImageView) findViewById(R.id.surfaceview);

        rb_mainActivity_mainFragment.setOnClickListener(this);
        rb_mainActivity_flowFragment.setOnClickListener(this);
        rb_mainActivity_postFragment.setOnClickListener(this);
        rb_mainActivity_favoriteFragment.setOnClickListener(this);
        rb_mainActivity_userFragment.setOnClickListener(this);
        camera.setOnClickListener(this);
        cancel.setOnClickListener(this);
        photo.setOnClickListener(this);
        ShowFragment(0);
    }

    private void ShowFragment(int i) {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        switch (i) {
            case 0:
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                    transaction.add(R.id.ll_mainActivity, mainFragment);
                } else {
                    transaction.show(mainFragment);
                }
                break;
            case 1:
                if (flowFragment == null) {
                    flowFragment = new FlowFragment();
                    transaction.add(R.id.ll_mainActivity, flowFragment);
                } else {
                    transaction.show(flowFragment);
                }
                break;
            case 2:
                if (favoriteFragment == null) {
                    favoriteFragment = new FavoriteFragment();
                    transaction.add(R.id.ll_mainActivity, favoriteFragment);
                } else {
                    transaction.show(favoriteFragment);
                }
                break;
            case 3:
                if (userFragment == null) {
                    userFragment = new UserFragment();
                    transaction.add(R.id.ll_mainActivity, userFragment);
                } else {
                    transaction.show(userFragment);
                }
                break;
        }
        transaction.commit();
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        if (mainFragment != null) {
            transaction.hide(mainFragment);
        }
        if (flowFragment != null) {
            transaction.hide(flowFragment);
        }
        if (favoriteFragment != null) {
            transaction.hide(favoriteFragment);
        }
        if (userFragment != null) {
            transaction.hide(userFragment);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_mainActivity_mainFragment:
                ShowFragment(0);
                break;
            case R.id.rb_mainActivity_flowFragment:
                ShowFragment(1);
                break;
            case R.id.rb_mainActivity_postFragment:
                getPost();
                break;
            case R.id.rb_mainActivity_favoriteFragment:
                ShowFragment(2);
                break;
            case R.id.rb_mainActivity_userFragment:
                ShowFragment(3);
                break;
            case R.id.camera:
                popupWindow.dismiss();
                gototakephoto();
                break;
            case R.id.photo:
                popupWindow.dismiss();
                getPhotopicture();
                break;
            case R.id.cancel:
                popupWindow.dismiss();
                break;
        }
    }

    protected Bitmap scaleImg(Bitmap bm, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix,true);
    }

    private String photoPath;

    //调用手机摄像头
    public void gototakephoto() {
        String str = null;
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");//获取当前时间，进一步转化为字符串
        date = new Date();
        str = format.format(date);
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "myImage");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, str + ".jpg");
        photoPath =file.getAbsolutePath();
        Uri uri=Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(intent, 1);
    }

    //获取手机相册图片
    public void getPhotopicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);

    }


    //重写onActivityResult方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {

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
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "myImage");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, str + ".jpg");

            try {
                b = new FileOutputStream(file);
                Bitmap bitmap2 = BitmapFactory.decodeStream(new FileInputStream(photoPath));
                Bitmap bitmap = scaleImg(bitmap2,400);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                File file1=new File(photoPath);
                if(file1.exists()){
                    file1.delete();
                }
                startActivity(new Intent(this, PostActivity.class).putExtra("pic",file.getAbsolutePath()));
            } catch (FileNotFoundException e) {
                Log.e("msg",e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
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
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "myImage");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, str + ".jpg");

            try {
                b = new FileOutputStream(file);
                Bitmap bitmap2 = BitmapFactory.decodeStream(cr.openInputStream(uri));
                Bitmap bitmap = scaleImg(bitmap2,400);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, b);// 把数据写入文件
                System.out.println("the bmp toString: " + bitmap);
                startActivity(new Intent(this, PostActivity.class).putExtra("pic", file.getAbsolutePath()));
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //pop弹窗背景的改变
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = MainActivity.this.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        MainActivity.this.getWindow().setAttributes(lp);
        MainActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    //拍照、相册
    public void getPost() {
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.PopupWindow_menu);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 90);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        backgroundAlpha(0.5f);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
