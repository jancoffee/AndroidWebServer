package webserver.realvision.se.webserver;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;

import fi.iki.elonen.NanoHTTPD;

public class Server extends NanoHTTPD {

    private static final String TAG = "Server";
    private final String mWWWRoot;
    private final MyWebServer.Logging mLogging;

    /**
     * Constructs an HTTP server on given port.
     *
     * @param port webserver listening port (has to be over over 1024)
     * @param wwwRoot webserver WWW root, can't be null or EMPTY
     * @param logging client logging interface
     */
    public Server(@IntRange(from = 1025,to = 65534) int port, @NonNull final String wwwRoot, @NonNull final MyWebServer.Logging logging) {
        super(port);
        mWWWRoot = wwwRoot;
        mLogging = logging;
    }

    /**
     * Constructs an HTTP server on given hostname and port.
     *
     * @param hostname
     * @param port webserver listening port (has to be over over 1024)
     * @param wwwRoot webserver WWW root, can't be null or EMPTY
     * @param logging client logging interface
     */
    public Server(@NonNull  String hostname, @IntRange(from = 1025,to = 65534) int port, @NonNull final String wwwRoot, @NonNull final MyWebServer.Logging logging) {
        super(hostname, port);
        mWWWRoot = wwwRoot;
        mLogging = logging;
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        NanoHTTPD.Method method = session.getMethod();
        String uri = session.getUri();
        String msg = "serve() called with: method = [" + method + "] uri = [" + uri + "]";
        Log.d(TAG, msg);
        mLogging.info(TAG,msg);
        return super.serve(session);
    }

    @Override
    public void stop() {
        super.stop();
        mLogging.info(TAG,"stop");
    }
}