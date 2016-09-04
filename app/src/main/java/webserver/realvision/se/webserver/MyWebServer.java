package webserver.realvision.se.webserver;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.Executors;

import fi.iki.elonen.NanoHTTPD;

/**
 * Client "interface" for the web server
 */
public class MyWebServer {

    Server mServer;
    private static final String TAG = "MyWebServer";
    private String mHostName;
    private int mListeningPort;
    public static final int NR_OF_THREAD_POOL_THREADS = 10;

    public interface Logging{
        void info(String tag,String information);
        void error(String tag,String error);
    }

    public void start(@Nullable final String addr,@IntRange(from = 1025,to = 65534) int port, @NonNull final String wwwRoot, @NonNull final MyWebServer.Logging logging) {
        logging.info(TAG,"start");
        try {
            mServer = new Server(addr,port, wwwRoot,logging);
            mServer.setAsyncRunner(new ThreadHandler(Executors.newFixedThreadPool(NR_OF_THREAD_POOL_THREADS)));
            mServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT*2, true);
            mHostName = mServer.getHostname();
            mListeningPort = mServer.getListeningPort();
        } catch (Exception exception) {
            Log.e(TAG, "start: failed with exception:"+exception );
        }
        Log.d(TAG, "start: listning on http://"+mHostName+":"+mListeningPort+"/");
    }

    public void stop() {
        if (mServer != null) {
            mServer.stop();
        }
    }
}
