package com.jinhanyu.jack.faceme;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by anzhuo on 2016/11/3.
 */

public class VerifyCodeView extends TextView{

    private String randomCode;

    public VerifyCodeView(Context context) {
        this(context,null);
    }

    public VerifyCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        genRandomCode();
    }




    public void genRandomCode(){
        randomCode = "";
        for (int i = 0; i < 4; i++) {
            randomCode+= (int)Math.floor(Math.random()*10);
        }
        setTextColor(Color.GREEN);
        setText(randomCode);
    }

    public String getRandomCode(){
        return randomCode;
    }

}
