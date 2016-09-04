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

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
/**
 * The actual web server class that is listening for incoming connections and sending responses
 * */
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
        //FIXME Add support for DASH eg a if audio and video is available both must exist, if just one exists return 404
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