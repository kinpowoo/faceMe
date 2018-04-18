package com.jinhanyu.jack.faceme.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.ClearEditText;
import com.jinhanyu.jack.faceme.CustomProgress;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.RealTimeService;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.User;
import com.jinhanyu.jack.faceme.tool.PermissionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


/**
 * Created by anzhuo on 2016/10/18.
 */
public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView cancel;
    private TextView commit, changePortrait, gender, email, phone, username;
    private SimpleDraweeView userPortrait;
    private ClearEditText nickname;
    private Float alpha = 1.0f;
    private PopupWindow popupWindow;
    private View chooseGenderView;
    private TextView male, female, chooseGenderCancel;
    private View photoSourceView;
    private TextView camera, photoLibrary, photoSourceCancel;
    private User currentUser;
    private File file;
    private Bitmap bitmap;


    //返回码，相机
    private static final int RESULT_CAMERA=200;
    //返回码，本地图库
    private static final int RESULT_IMAGE=100;

    //拍照后照片的Uri
    private Uri imageUri;

    private static final int CROP_PICTURE = 2;//裁剪后图片返回码
    //裁剪图片存放地址的Uri
    private Uri cropImageUri;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    backgroundAlpha((float) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);
        currentUser = Utils.getCurrentUser();

        cancel = (ImageView) findViewById(R.id.tv_edit_profile_cancel);
        commit = (TextView) findViewById(R.id.tv_edit_profile_commit);
        username = (TextView) findViewById(R.id.tv_edit_profile_username);
        changePortrait = (TextView) findViewById(R.id.tv_edit_profile_changePortrait);
        nickname = (ClearEditText) findViewById(R.id.cet_edit_profile_nickname);
        email = (TextView) findViewById(R.id.tv_edit_profile_email);
        phone = (TextView) findViewById(R.id.tv_edit_profile_phone);
        gender = (TextView) findViewById(R.id.tv_edit_profile_gender);
        userPortrait = (SimpleDraweeView) findViewById(R.id.sdv_edit_profile_userPortrait);

        userPortrait.setImageURI(currentUser.getPortrait().getUrl());
        username.setText(currentUser.getUsername());
        nickname.setText(currentUser.getNickname());
        gender.setText(currentUser.getGender());

        cancel.setOnClickListener(this);
        commit.setOnClickListener(this);
        changePortrait.setOnClickListener(this);
        email.setOnClickListener(this);
        phone.setOnClickListener(this);
        gender.setOnClickListener(this);

        chooseGenderView = LayoutInflater.from(this).inflate(R.layout.choose_gender_menu, null);
        male = (TextView) chooseGenderView.findViewById(R.id.tv_choose_gender_male);
        female = (TextView) chooseGenderView.findViewById(R.id.tv_choose_gender_female);
        chooseGenderCancel = (TextView) chooseGenderView.findViewById(R.id.tv_choose_gender_cancel);
        male.setOnClickListener(this);
        female.setOnClickListener(this);
        chooseGenderCancel.setOnClickListener(this);

        photoSourceView = LayoutInflater.from(this).inflate(R.layout.choose_photo_source, null);
        camera = (TextView) photoSourceView.findViewById(R.id.tv_choose_photo_source_camera);
        photoLibrary = (TextView) photoSourceView.findViewById(R.id.tv_choose_photo_source_photoLibrary);
        photoSourceCancel = (TextView) photoSourceView.findViewById(R.id.tv_choose_photo_source_cancel);
        camera.setOnClickListener(this);
        photoLibrary.setOnClickListener(this);
        photoSourceCancel.setOnClickListener(this);

        fillData();
    }

    public void fillData() {
        userPortrait.setImageURI(currentUser.getPortrait().getUrl());
        username.setText(currentUser.getUsername());
        nickname.setText(currentUser.getNickname());
        nickname.setSelection(nickname.getText().length());
        email.setText(currentUser.getEmail());
        phone.setText(currentUser.getMobilePhoneNumber());
        gender.setText(currentUser.getGender());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_edit_profile_cancel:
                finish();
                break;
            case R.id.tv_edit_profile_commit:
                final BmobFile bmobFile = new BmobFile(file);
                if (file != null) {
                    CustomProgress.show(this,"正在提交修改...");
                    bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            User user = new User();
                            user.setPortrait(bmobFile);
                            user.setNickname(nickname.getText().toString());
                            user.setGender(gender.getText().toString());
                            user.update(currentUser.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        CustomProgress.unshow();
                                        finish();
                                    } else {
                                        Toast.makeText(EditProfileActivity.this, "更新失败，请重新提交", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    User user = new User();
                    user.setNickname(nickname.getText().toString());
                    user.setGender(gender.getText().toString());
                    user.update(currentUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                finish();
                            } else {
                                Toast.makeText(EditProfileActivity.this, "更新失败，请重新提交", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                break;

            case R.id.tv_edit_profile_changePortrait:
                if (popupWindow != null && popupWindow.isShowing()) {
                    return;
                } else {
                    showWindow(photoSourceView, changePortrait);
                }
                break;


            case R.id.tv_edit_profile_email:
                Intent intent = new Intent(EditProfileActivity.this, EmailVerfiyActivity.class);
                intent.putExtra("email", email.getText().toString());
                startActivityForResult(intent, 2);
                break;
            case R.id.tv_edit_profile_phone:
                Intent intent1 = new Intent(this, PhoneVerifyActivity.class);
                startActivity(intent1);
                break;

            case R.id.tv_edit_profile_gender:
                if (popupWindow != null && popupWindow.isShowing()) {
                    return;
                } else {
                    showWindow(chooseGenderView, gender);
                }
                break;
            case R.id.tv_choose_gender_male:
                popupWindow.dismiss();
                gender.setText("男");
                break;
            case R.id.tv_choose_gender_female:
                popupWindow.dismiss();
                gender.setText("女");
                break;
            case R.id.tv_choose_gender_cancel:
                popupWindow.dismiss();
                break;
            case R.id.tv_choose_photo_source_camera://照相机
                popupWindow.dismiss();
                if(ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.CAMERA)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditProfileActivity.this,new String[]{
                            Manifest.permission.CAMERA},2);
                }else {
                    openCamera();
                }
                break;
            case R.id.tv_choose_photo_source_photoLibrary://相册
                popupWindow.dismiss();
                //相册
                if(ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditProfileActivity.this,new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    openAlbum();
                }
                break;
            case R.id.tv_choose_photo_source_cancel:
                popupWindow.dismiss();
                break;
        }
    }

    public void showWindow(View targetView, View locationView) {
        popupWindow = new PopupWindow(targetView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //添加弹出、弹入的动画
        popupWindow.setAnimationStyle(R.style.PopupWindow_menu);
        int[] location = new int[2];
        locationView.getLocationOnScreen(location);
        popupWindow.showAtLocation(locationView, Gravity.LEFT | Gravity.BOTTOM, 0, -location[1]);
        //添加pop窗口关闭事件，主要是实现关闭时改变背景的透明度
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //此处while的条件alpha不能<= 否则会出现黑屏
                        while (alpha < 1f) {
                            try {
                                Thread.sleep(4);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Message msg = mHandler.obtainMessage();
                            msg.what = 1;
                            alpha += 0.01f;
                            msg.obj = alpha;
                            mHandler.sendMessage(msg);
                        }
                    }

                }).start();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (alpha > 0.5f) {
                    try {
                        //4是根据弹出动画时间和减少的透明度计算
                        Thread.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    //每次减少0.01，精度越高，变暗的效果越流畅
                    alpha -= 0.01f;
                    msg.obj = alpha;
                    mHandler.sendMessage(msg);
                }
            }

        }).start();
    }


    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
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
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
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
        photoPath = file.getAbsolutePath();
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 1);
    }

    //获取手机相册图片
    public void getPhotopicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }




    private void openCamera(){
        //先验证手机是否有sdcard
        String status= Environment.getExternalStorageState();
        if(status.equals(Environment.MEDIA_MOUNTED))
        {
            //创建File对象，用于存储拍照后的照片
            File outputImage=new File(getExternalCacheDir(),"out_put.jpg");//SD卡的应用关联缓存目录
            try {
                if(outputImage.exists()){
                    outputImage.delete();
                }
                outputImage.createNewFile();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri= FileProvider.getUriForFile(this,
                            "com.jinhanyu.jack.faceme.ui.MainActivity",outputImage);
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                }else{
                    imageUri= Uri.fromFile(outputImage);
                }

                //启动相机程序
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,RESULT_CAMERA);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Toast.makeText(this, "没有找到储存目录",Toast.LENGTH_LONG).show();
            }


        }else{
            Toast.makeText(this, "没有储存卡",Toast.LENGTH_LONG).show();
        }
    }



    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,RESULT_IMAGE);//打开相册
    }





    private String saveBitmapToFile(Bitmap b){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");//获取当前时间，进一步转化为字符串
        Date date = new Date();
        String str = format.format(date);

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        File file = new File(dir, str + ".jpg");
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fs);// 把数据写入文件
        } catch (FileNotFoundException e) {
            Log.e("msg",e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if(fs!=null) {
                    fs.flush();
                    fs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file.getAbsolutePath();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case RESULT_CAMERA:
                if(resultCode==RESULT_OK){
                    //进行裁剪
                    startPhotoZoom(imageUri);
                }
                break;
            case RESULT_IMAGE:
                if(resultCode==RESULT_OK&&data!=null){
                    //判断手机系统版本号
                    if(Build.VERSION.SDK_INT>=19){
                        //4.4及以上系统使用这个方法处理图片
                        handlerImageOnKitKat(data);
                    }else{
                        //4.4以下系统使用这个方法处理图片
                        handlerImageBeforeKitKat(data);
                    }
                }
                break;
            case CROP_PICTURE: // 取得裁剪后的图片
                if(resultCode==RESULT_OK) {
                    try {
                        Bitmap headShot = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImageUri));
                        String fileUri = saveBitmapToFile(headShot);
                        file = new File(fileUri);
                        userPortrait.setImageURI("file://" + fileUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }



    /**
     * 4.4及以上系统处理图片的方法
     * */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handlerImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri,则通过document id处理
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];//解析出数字格式的id
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的URI，则使用普通方式处理
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath=uri.getPath();
        }
        startPhotoZoom(uri);
        //根据图片路径显示图片
        //displayImage(imagePath);
    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }



    public void startPhotoZoom(Uri uri) {
        File CropPhoto=new File(getExternalCacheDir(), UUID.randomUUID()+".jpg");
        try{
            if(CropPhoto.exists()){
                CropPhoto.delete();
            }
            CropPhoto.createNewFile();
        }catch(IOException e){
            e.printStackTrace();
        }
        cropImageUri=Uri.fromFile(CropPhoto);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);

        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);

        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, CROP_PICTURE);
    }



    /**
     *4.4以下系统处理图片的方法
     * */
    private void handlerImageBeforeKitKat(Intent data){
        Uri cropUri=data.getData();
        startPhotoZoom(cropUri);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"你没有开启权限",Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }else{
                    Toast.makeText(this,"你没有开启权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }





}
