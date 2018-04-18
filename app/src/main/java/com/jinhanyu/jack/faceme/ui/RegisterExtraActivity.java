package com.jinhanyu.jack.faceme.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.faceme.CustomProgress;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.RealTimeService;
import com.jinhanyu.jack.faceme.Utils;
import com.jinhanyu.jack.faceme.entity.User;
import com.jinhanyu.jack.faceme.tool.PermissionManager;
import com.jinhanyu.jack.faceme.tool.PhotoReadUtil;

import java.io.File;
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
 * Created by anzhuo on 2016/11/7.
 */

public class RegisterExtraActivity extends Activity {

    //返回码，相机
    private static final int RESULT_CAMERA=200;
    //返回码，本地图库
    private static final int RESULT_IMAGE=100;

    //拍照后照片的Uri
    private Uri imageUri;

    private static final int CROP_PICTURE = 2;//裁剪后图片返回码
    //裁剪图片存放地址的Uri
    private Uri cropImageUri;

    SimpleDraweeView take_photo;
    Switch gender_switch;
    EditText et_nickname;
    TextView commitBtn;

    File portrait_file;

    private String[] mCustomItems=new String[]{"本地相册","相机拍照"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_extra);
        take_photo = (SimpleDraweeView) findViewById(R.id.take_photo);
        gender_switch = (Switch) findViewById(R.id.gender_switch);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        commitBtn = (TextView) findViewById(R.id.commit);

        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit(v);
            }
        });


        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCustom();
            }
        });
    }

    public void goBack(View view) {
        finish();
    }




    private void showDialogCustom(){
        //创建对话框
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("请选择：");
        builder.setItems(mCustomItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0) {
                    //相册
                    if(ContextCompat.checkSelfPermission(RegisterExtraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                            PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(RegisterExtraActivity.this,new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }else{
                        openAlbum();
                    }

                }else if(which==1){
                    //照相机
                    if(ContextCompat.checkSelfPermission(RegisterExtraActivity.this, Manifest.permission.CAMERA)!=
                            PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(RegisterExtraActivity.this,new String[]{
                                Manifest.permission.CAMERA},2);
                    }else {
                        openCamera();
                    }
                }
            }
        });
        builder.create().show();
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
                        String picUri = saveBitmapToFile(headShot);

                        portrait_file = new File(picUri);
                        take_photo.setImageURI("file://"+picUri);
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
