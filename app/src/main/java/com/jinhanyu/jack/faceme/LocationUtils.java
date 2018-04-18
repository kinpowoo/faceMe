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

    private static LocationUtils instance;
    Handler handler;
    Context mcontent;
    AMapLocationClient mlocationClient = null;
    AMapLocationClientOption mLocationOption = null;


    public static LocationUtils getInstance(){
        if(instance==null){
            synchronized (LocationUtils.class){
                if(instance==null){
                    instance = new LocationUtils(MainApplication.getContext());
                }
            }
        }
        return instance;
    }


    private LocationUtils(Context content) {
        this.mcontent = content;
        mlocationClient = new AMapLocationClient(mcontent);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置返回地址信息，默认为true
        mLocationOption.setNeedAddress(true);
        mLocationOption.setWifiActiveScan(true);
        mLocationOption.setMockEnable(true);
        mLocationOption.setHttpTimeOut(30000);
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，
        // setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);

        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);

        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
    }

    public void guide(Handler handler) {
        this.handler = handler;
        mlocationClient.startLocation();
    }


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        Log.i("location","定位结果刷新了");
        if (amapLocation != null) {
            Log.i("location","定位结果amapLocation不为空");
            if (amapLocation.getErrorCode() == 0) {
                Log.i("location","定位结果amapLocation.getErrorCode()==0");
                //定位成功回调信息，设置相关消息
                Position position  = new Position();
                position.setLatitude(amapLocation.getLatitude());//获取纬度
                position.setLongitude(amapLocation.getLongitude());//获取经
                position.setProvince(amapLocation.getProvince());//省信息
                position.setCity(amapLocation.getCity());//城市信息
                handler.sendMessage(Message.obtain(handler,0,position));
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getAoiName();
//                amapLocation.getStreetNum();//街道门牌号信息


                mlocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁

            } else {
                Log.i("location","定位结果amapLocation.getErrorCode()!=0");
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }

    }


    public void destroyClient(){
        if(mlocationClient!=null){
            mlocationClient.onDestroy();
        }
    }


}
