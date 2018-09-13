package com.jinhanyu.jack.faceme.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import android.widget.Toast;

import com.jinhanyu.jack.faceme.BaseFragment;
import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.RealTimeService;
import com.jinhanyu.jack.faceme.tool.ConstantFunc;
import com.jinhanyu.jack.faceme.tool.PermissionManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by anzhuo on 2016/10/18.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //返回码，相机
    private static final int RESULT_CAMERA = 200;
    //返回码，本地图库
    private static final int RESULT_IMAGE = 100;

    //拍照后照片的Uri
    private Uri imageUri;

    private static final int CROP_PICTURE = 2;//裁剪后图片返回码
    //裁剪图片存放地址的Uri
    private Uri cropImageUri;


    private static final String SAVED_CURRENT_ID = "currentId";

    MainFragment mainFragment;
    FlowFragment flowFragment;
    FavoriteFragment favoriteFragment;
    UserFragment userFragment;

    private List<BaseFragment> fragmentList = new ArrayList<>();

    private int currentId = 0;


    private RadioButton rb_mainActivity_mainFragment;
    private RadioButton rb_mainActivity_flowFragment;
    private Button rb_mainActivity_postFragment;
    private RadioButton rb_mainActivity_favoriteFragment;
    private RadioButton rb_mainActivity_userFragment;
    private Intent intent;

    //动态加载视图相关
    private View view;
    private PopupWindow popupWindow;
    private ImageView camera, photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //pop弹窗控件初始化
        view = LayoutInflater.from(MainActivity.this).inflate(R.layout.postchoose, null);
        camera = (ImageView) view.findViewById(R.id.camera);
        photo = (ImageView) view.findViewById(R.id.photo);

        rb_mainActivity_mainFragment = (RadioButton) findViewById(R.id.rb_mainActivity_mainFragment);
        rb_mainActivity_flowFragment = (RadioButton) findViewById(R.id.rb_mainActivity_flowFragment);
        rb_mainActivity_postFragment = (Button) findViewById(R.id.rb_mainActivity_postFragment);
        rb_mainActivity_favoriteFragment = (RadioButton) findViewById(R.id.rb_mainActivity_favoriteFragment);
        rb_mainActivity_userFragment = (RadioButton) findViewById(R.id.rb_mainActivity_userFragment);

        rb_mainActivity_mainFragment.setOnClickListener(this);
        rb_mainActivity_flowFragment.setOnClickListener(this);
        rb_mainActivity_postFragment.setOnClickListener(this);
        rb_mainActivity_favoriteFragment.setOnClickListener(this);
        rb_mainActivity_userFragment.setOnClickListener(this);
        camera.setOnClickListener(this);
        photo.setOnClickListener(this);


        initFragments(savedInstanceState);
        initPermission(this);
    }


    private void initPermission(Context context) {
        //位置采集周期
        // 在Android 6.0及以上系统，若定制手机使用到doze模式，请求将应用添加到白名单。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            String packageName = context.getPackageName();
            boolean isIgnoring = pm.isIgnoringBatteryOptimizations(packageName);
            if (!isIgnoring) {
                Intent intent = new Intent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                try {
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        //检查权限
        if (PermissionManager.checkLocationPermission(this)) {
            ConstantFunc.hint("permission", "权限获得通过");
            //intent = new Intent(MainActivity.this, RealTimeService.class);
            //bindService(intent, connection, BIND_AUTO_CREATE);
        } else {
            Toast.makeText(this, "拒绝权限会让应用某部分功能无法正常运行", Toast.LENGTH_LONG).show();
        }

    }


    private void initFragments(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*获取保存的fragment  没有的话返回null*/
            mainFragment = (MainFragment) getSupportFragmentManager().
                    getFragment(savedInstanceState, MainFragment.class.getName());
            flowFragment = (FlowFragment) getSupportFragmentManager().
                    getFragment(savedInstanceState, FlowFragment.class.getName());
            favoriteFragment = (FavoriteFragment) getSupportFragmentManager().
                    getFragment(savedInstanceState, FavoriteFragment.class.getName());
            userFragment = (UserFragment) getSupportFragmentManager().
                    getFragment(savedInstanceState, UserFragment.class.getName());

            addToList(mainFragment);
            addToList(flowFragment);
            addToList(favoriteFragment);
            addToList(userFragment);

            int cachedId = savedInstanceState.getInt(SAVED_CURRENT_ID, 0);
            if (cachedId >= 0 && cachedId <= 3) {
                currentId = cachedId;
            }
            switchFragment(currentId);
        }else{
            switchFragment(0);
        }
    }


    private void addToList(BaseFragment fragment) {
        if (fragment != null) {
            fragmentList.add(fragment);
        }
    }


    /*添加fragment*/
    private void addFragment(BaseFragment fragment, String tag) {
        /*判断该fragment是否已经被添加过  如果没有被添加  则添加*/
        if (!fragment.isAdded() && null == getSupportFragmentManager().findFragmentByTag(tag)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.add(R.id.ll_mainActivity, fragment, tag).commit();
            //commit方法是异步的，调用后不会立即执行，而是放到UI任务队列中

            getSupportFragmentManager().executePendingTransactions();
            //让commit动作立即执行

            addToList(fragment);
            /*添加到 fragmentList*/
        }
    }


    /*显示fragment*/
    private void showFragment(BaseFragment fragment) {

        for (BaseFragment frag : fragmentList) {
            if (frag != fragment) {
                /*先隐藏其他fragment*/
                getSupportFragmentManager().beginTransaction().hide(frag).commit();
            }
        }
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
    }


    private void switchFragment(int index) {
        switch (index) {
            case 0:
                if (mainFragment == null) {
                    mainFragment = new MainFragment();
                }
                Log.i("tag", "switch fragment exec");
                addFragment(mainFragment, "mainFragment");
                showFragment(mainFragment);
                break;
            case 1:
                if (flowFragment == null) {
                    flowFragment = new FlowFragment();
                }
                addFragment(flowFragment, "flowFragment");
                showFragment(flowFragment);
                break;
            case 2:
                if (favoriteFragment == null) {
                    favoriteFragment = new FavoriteFragment();
                }
                addFragment(favoriteFragment, "favoriteFragment");
                showFragment(favoriteFragment);
                break;
            case 3:
                if (userFragment == null) {
                    userFragment = new UserFragment();
                }
                addFragment(userFragment, "userFragment");
                showFragment(userFragment);
                break;
        }
        currentId = index;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_mainActivity_mainFragment:
                switchFragment(0);
                break;
            case R.id.rb_mainActivity_flowFragment:
                switchFragment(1);
                break;
            case R.id.rb_mainActivity_postFragment:
                getPost();
                break;
            case R.id.rb_mainActivity_favoriteFragment:
                switchFragment(2);
                break;
            case R.id.rb_mainActivity_userFragment:
                switchFragment(3);
                break;
            case R.id.camera:
                popupWindow.dismiss();
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.CAMERA}, 2);
                } else {
                    openCamera();
                }
                break;
            case R.id.photo:
                popupWindow.dismiss();
                //相册
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }

                break;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (mainFragment != null) {
            getSupportFragmentManager().putFragment(outState, MainFragment.class.getName(), mainFragment);
        }
        if (flowFragment != null) {
            getSupportFragmentManager().putFragment(outState, FlowFragment.class.getName(), flowFragment);
        }
        if (favoriteFragment != null) {
            getSupportFragmentManager().putFragment(outState, FavoriteFragment.class.getName(), favoriteFragment);
        }
        if (userFragment != null) {
            getSupportFragmentManager().putFragment(outState, UserFragment.class.getName(), userFragment);
        }
        outState.putInt(SAVED_CURRENT_ID, currentId);
        super.onSaveInstanceState(outState, outPersistentState);
    }


    private void openCamera() {
        //先验证手机是否有sdcard
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            //创建File对象，用于存储拍照后的照片
            File outputImage = new File(getExternalCacheDir(), "out_put.jpg");//SD卡的应用关联缓存目录
            try {
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                outputImage.createNewFile();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(this,
                            "com.jinhanyu.jack.faceme.ui.MainActivity", outputImage);
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }

                //启动相机程序
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, RESULT_CAMERA);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Toast.makeText(this, "没有找到储存目录", Toast.LENGTH_LONG).show();
            }


        } else {
            Toast.makeText(this, "没有储存卡", Toast.LENGTH_LONG).show();
        }
    }


    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_IMAGE);//打开相册
    }


    private String saveBitmapToFile(Bitmap b) {
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
            Log.e("msg", e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (fs != null) {
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
        switch (requestCode) {
            case RESULT_CAMERA:
                if (resultCode == RESULT_OK) {
                    //进行裁剪
                    startPhotoZoom(imageUri);
                }
                break;
            case RESULT_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        handlerImageOnKitKat(data);
                    } else {
                        //4.4以下系统使用这个方法处理图片
                        handlerImageBeforeKitKat(data);
                    }
                }
                break;
            case CROP_PICTURE: // 取得裁剪后的图片
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap headShot = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropImageUri));
                        startActivity(new Intent(this, PostActivity.class).putExtra("pic", saveBitmapToFile(headShot)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }


    /**
     * 4.4及以上系统处理图片的方法
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handlerImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的URI，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath = uri.getPath();
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
        File CropPhoto = new File(getExternalCacheDir(), UUID.randomUUID() + ".jpg");
        try {
            if (CropPhoto.exists()) {
                CropPhoto.delete();
            }
            CropPhoto.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cropImageUri = Uri.fromFile(CropPhoto);
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
     * 4.4以下系统处理图片的方法
     */
    private void handlerImageBeforeKitKat(Intent data) {
        Uri cropUri = data.getData();
        startPhotoZoom(cropUri);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你没有开启权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "你没有开启权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case PermissionManager.MY_PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    intent = new Intent(MainActivity.this, RealTimeService.class);
                    //bindService(intent, connection, BIND_AUTO_CREATE);
                } else {
                    initPermission(this);
                }
                break;
            default:
                break;
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
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 150);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        backgroundAlpha(0.2f);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
