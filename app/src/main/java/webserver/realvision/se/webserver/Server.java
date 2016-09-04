package webserver.realvision.se.webserver;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

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
        logSession(session);
        Response response = handleSessionRequest(session,getDefaultResponse());
        return response;
    }

    private Response getDefaultResponse() {
        //return a 404
        return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not Found");
    }

    private Response handleSessionRequest(IHTTPSession session, Response defaultResponse) {

        File file = openFile(mWWWRoot,session.getUri());
        if (file == null || !file.isFile()) {
            mLogging.error(TAG, "handleSessionRequest, unable to get file for wwwRoot:" + mWWWRoot + " uri:" + session.getUri());
            return defaultResponse;
        } else {
           mLogging.info(TAG,"handleSessionRequest, got file:"+file.getAbsolutePath());
        }

        InputStream is = openStream(file);
        if (is == null) {
            mLogging.error(TAG, "handleSessionRequest, unable open input stream for file" + file.getAbsolutePath());
            return defaultResponse;
        } else {
            mLogging.info(TAG,"handleSessionRequest, open input stream for file:"+file.getAbsolutePath());
        }

        //newChunkedResponse()
        long nrOfBytes = file.length();

        return newFixedLengthResponse(Response.Status.PARTIAL_CONTENT,NanoHTTPD.MIME_HTML,is, nrOfBytes);
    }

    private InputStream openStream(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            mLogging.error(TAG,"openFile, cant open file:"+file);
        }
        return is;
    }

    private File openFile(String WWWRoot, String uri) {
        String fullFilePath = mWWWRoot + uri;
        File file = new File(fullFilePath);
        return file;
    }

    private void logSession(IHTTPSession session) {
        final NanoHTTPD.Method method = session.getMethod();
        final String uri = session.getUri();
        final Map<String, String> headers = session.getHeaders();
        final String userAgent  = (String) headers.get("user-agent");
        String msg = "serve() called with: method = [" + method + "] uri = [" + uri + "] userAgent = ["+userAgent + "]";
        Log.d(TAG, msg);
        mLogging.info(TAG,msg);

    }

    @Override
    public void stop() {
        super.stop();
        mLogging.info(TAG,"stop");
    }
}