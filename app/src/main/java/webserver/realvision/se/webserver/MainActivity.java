package webserver.realvision.se.webserver;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private MyWebServer mWebServer;
    private Button mStartServerButton;
    private Button mStopServerButton;
    //FIXME add more info eg listening address and port
    //FIXME add a button for getting a HTTP head of a file
    //FIXME enable logging window

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);

        mStartServerButton = (Button)findViewById(R.id.start_server);
        mStopServerButton = (Button)findViewById(R.id.stop_server);

        Intent intent = new Intent(this,MyService.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        Intent intent = new Intent(this,MyService.class);
        bindService(intent, mServiceConnection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        unbindService(mServiceConnection);
    }

    /**
     * Started web server button pressed
     * */
    public void startServerButtonPressed(final View view) {
        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (mWebServer != null) {
            mWebServer.start(null,9090, path.getAbsolutePath(), mWebServerLogging);
        }
    }

    /**
     * Stopped web server button pressed
     * */
    public void stopServerButtonPressed(final View view) {
        if (mWebServer != null) {
            mWebServer.stop();
        }
        Intent intent = new Intent(this,MyService.class);
        stopService(intent);
        unbindService(mServiceConnection);
    }

    /**
     * Webserver logging interface
     * */
    MyWebServer.Logging mWebServerLogging = new MyWebServer.Logging() {
        @Override
        public void info(final String tag, final String information) {
            Log.d(TAG, "info: tag:"+tag+ " info:"+information);
        }

        @Override
        public void error(final String tag, final String error) {
            Log.d(TAG, "info: tag:"+tag+ " info:"+error);
        }
    };

    /**
     * Bind connection
     * */
    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: ");
            mWebServer = ((MyService.MyBinder) iBinder).getInstance();
            mStartServerButton.setEnabled(true);
            mStopServerButton.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: ");
            mStartServerButton.setEnabled(false);
            mStopServerButton.setEnabled(false);
        }
    };

}
