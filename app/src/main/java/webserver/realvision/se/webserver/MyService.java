/*
 * Copyright 2016 Jan Igerud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
