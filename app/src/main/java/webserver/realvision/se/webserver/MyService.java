package webserver.realvision.se.webserver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
/**
 * Android Service class that encapsulates the web server class to keep it alive in the background
 * */
public class MyService extends Service {

    private MyWebServer myWebServer;
    private MyBinder myBinder = new MyBinder();
    private static final String TAG = "MyService";
    //FIXME Run this service in "High" priority

    public MyService() {
        myWebServer = new MyWebServer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return myBinder;
    }

    class MyBinder extends Binder {
        public MyWebServer getInstance(){
            return myWebServer;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind:");
        return super.onUnbind(intent);
    }
}
