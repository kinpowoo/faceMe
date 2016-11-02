package com.jinhanyu.jack.faceme;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.jinhanyu.jack.faceme.entity.Position;


/**
 * Created by anzhuo on 2016/11/2.
 */

public class LocationUtils implements AMapLocationListener {
    Handler handler;
    String city = "";
    Context mcontent;
    AMapLocationClient mlocationClient = null;
    AMapLocationClientOption mLocationOption = null;

    public LocationUtils(Context content,Handler handler) {
        this.mcontent = content;
        this.handler=handler;
    }

    public void guide() {
        mlocationClient = new AMapLocationClient(mcontent);

        mlocationClient = new AMapLocationClient(mcontent);
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置返回地址信息，默认为true
        mLocationOption.setNeedAddress(true);
//设置定位监听
        mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);

        mlocationClient.startLocation();
    }


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {


        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                Position position  = new Position();
                position.setLatitude(amapLocation.getLatitude());//获取纬度
                position.setLongitude(amapLocation.getLongitude());//获取经度
                position.setCountry(amapLocation.getCountry());//国家信息
                position.setProvince(amapLocation.getProvince());//省信息
                position.setCity(amapLocation.getCity());//城市信息
                handler.sendMessage(Message.obtain(handler,0,position));
//                amapLocation.getDistrict();//城区信息
//                amapLocation.getStreet();//街道信息
//                amapLocation.getStreetNum();//街道门牌号信息

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }

    }


}
