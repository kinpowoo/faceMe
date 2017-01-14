package com.jinhanyu.jack.faceme;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.jinhanyu.jack.faceme.entity.Position;
import com.jinhanyu.jack.faceme.entity.User;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/1/14 0014.
 */

public class RealTimeService extends Service {
    private Position position=new Position();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                User currentUser=Utils.getCurrentUser();
                position= (Position) msg.obj;
                currentUser.setRealTimeLoc(new BmobGeoPoint(position.getLongitude(),position.getLatitude()));
                currentUser.update(currentUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {

                    }
                });
            }
        }
    };


    public class MyBinder extends Binder {
        public RealTimeService getService(){
            return RealTimeService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new LocationUtils(RealTimeService.this,handler).guide();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }
}
