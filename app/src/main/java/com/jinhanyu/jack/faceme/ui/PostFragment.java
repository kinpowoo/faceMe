package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinhanyu.jack.faceme.R;
import com.jinhanyu.jack.faceme.entity.Status;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by anzhuo on 2016/10/18.陈礼 拍照
 */
public class PostFragment extends Fragment {
      private List<Status> Status_list;

      private ImageView iv_back;//返回
      private TextView tv_uploading;//发表动态
      private EditText et_content;//发表的内容
      private TextView word_number;//内容的字数限定（上限140）
      private ImageView who_look;//@谁，给谁看
      private ImageView face;//表情
      private ImageView location;//定位
      private TextView location_show;//具体位置

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_fragment, null);
        gototakephoto();


        return view;
    }


    //调用手机摄像头
    public void gototakephoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    //重写onActivityResult方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && requestCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("date");
            BmobQuery<Status> query = new BmobQuery<>();
            query.addWhereContains("photo", bitmap.toString());
            query.findObjects(new FindListener<Status>() {
                @Override
                public void done(List<Status> list, BmobException e) {
                    Status_list=list;
                }
            });
        }
    }
}
