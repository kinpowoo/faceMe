package com.jinhanyu.jack.faceme.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jinhanyu.jack.faceme.R;

/**
 * Created by DeskTop29 on 2018/9/12.
 */

public class TestActivity extends AppCompatActivity {

    SimpleDraweeView view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

        view = (SimpleDraweeView) findViewById(R.id.iv_status_photo);

        view.setImageURI("http://bmob-cdn-7072.b0.upaiyun.com/2018/09/12/7dc5c9ff47ad47d28599d529db7b9d2e.jpg");

        //view.setImageRequest();
    }
}
