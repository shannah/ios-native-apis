package com.codenameone.ios.foundation;

import com.codename1.io.*;
import com.codename1.system.NativeLookup;
import com.codename1.ui.CN;

import com.codename1.ui.events.ActionListener;
import com.codename1.util.AsyncResource;
import com.codename1.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * A class that wraps <a href="https://developer.apple.com/documentation/foundation/nsurlsession?language=objc">NSURLSession</a> on iOS, emulates
 * it on other platforms with a cross-platform implementation.
 */
public class NSURLSession {
    private static NSURLSession sharedSession;
    private NSURLSession0 session;
    private static final Object lock = new Object();
    static {
        // So that ParparVM doesn't strip it out.
        NativeCallbacks callbacks = new NativeCallbacks();
    }


    private static Map<Long,DownloadTask> pendingDownloadTasks = new HashMap<Long,DownloadTask>();
    private static long nextTaskId = 0;

    private static long registerDownloadTask(DownloadTask task) {
        synchronized (lock) {
            long taskId = nextTaskId++;
            pendingDownloadTasks.put(taskId, task);
            return taskId;
        }
    }

    static void downloadTaskCallback(long taskId, String location, NSURLResponse response, NSError error) {
        DownloadTask task;
        synchronized (lock) {
            task = pendingDownloadTasks.remove(taskId);
        }
        task.handleResponse(location, response, error);

    }

    /**
     * Gets a reference to the snared NSURLSession object.
     * @return
     */
    public static NSURLSession getSharedSession() {
        if (sharedSession == null) {
            sharedSession = new NSURLSession();
        }
        return sharedSession;
    }

    /**
     * Creates a new NSURLSession
     */
    public NSURLSession() {
        session = NativeLookup.create(NSURLSession0.class);
    }

    /**
     * A DownloadTask initiated by the {@link #downloadTaskWithURL(String)} method.
     */
    public static class DownloadTask extends AsyncResource<DownloadTask>{
        /**
         * The file that the URL contents were downloaded to.  This is a temp file
         * that will be deleted after the callback handlers are called.
         */
        private com.codename1.io.File location;

        /**
         * The response information from the request.
         */
        private NSURLResponse nsurlResponse;

        /**
         * The error (if an error occurred).
         */
        private NSError nsError;

        /**
         * Called when the request is complete.  This will trigger the ready()/error() etc.. in
         * the AsyncResource
         * @param locationPath
         * @param nsurlResponse
         * @param nsError
         */
        private void handleResponse(String locationPath, NSURLResponse nsurlResponse, NSError nsError) {
            this.location = locationPath == null ? null : new com.codename1.io.File(prefixFile(locationPath));
            this.nsurlResponse = nsurlResponse;
            this.nsError = nsError;

            if (nsError != null) {
                this.error(this.nsError);
                return;
            }
            this.complete(this);
        }

        /**
         * Gets details about the server response.
         * @return
         */
        public NSURLResponse getNSURLResponse() {
            return nsurlResponse;
        }

        /**
         * Gets the error associated with the request.  Or null if no error occurred.
         * @return
         */
        public NSError getNSError() {
            return nsError;
        }

        /**
         * Gets the temporary file that the URL contents were downloaded to.  This will
         * be deleted after the callbacks are finished, so you will need to copy or move this
         * file if you need it.
         * @return
         */
        public File getLocation() {
            return location;
        }

    }

    /**
     * This was adapted from {@link Util#downloadUrlToFileSystemInBackground(String, String)} .  Couldn't use
     * it as was because it didn't run the callback if the download failed.
     * @param url The URL to download
     * @param fileName The file path to download to
     * @param onComplete Handler to run on complete.
     */
    private void downloadUrlToFileSystemInBackground(String url, String fileName, ActionListener<NetworkEvent> onComplete) {
        ConnectionRequest cr = new ConnectionRequest();
        cr.setPost(false);
        cr.setFailSilently(true);
        cr.setReadResponseForErrors(true);
        cr.setDuplicateSupported(true);
        cr.setUrl(url);
        cr.setDestinationFile(fileName);
        cr.addResponseListener(onComplete);
        NetworkManager.getInstance().addToQueue(cr);

    }


    /**
     * Initiates an asynchronous download task for the given URL.
     * @param url The URL to download.
     * @return DownloadTask.
     */
    public DownloadTask downloadTaskWithURL(String url) {
        DownloadTask task = new DownloadTask();
        long taskId = registerDownloadTask(task);
        if (session.isSupported()) {
            session.downloadTaskWithURL(url, taskId);
            return task;
        } else {
            File tempDir = new File("NSURLSession_tempfiles");
            tempDir.mkdirs();
            File tempFile = new File(tempDir, "temp_"+taskId);

            downloadUrlToFileSystemInBackground(url, tempFile.getAbsolutePath(), evt->{
                NetworkEvent nevt = (NetworkEvent)evt;
                ConnectionRequest req = nevt.getConnectionRequest();
                String contentType = req.getContentType();
                String charset = "UTF-8";
                if (contentType.indexOf(";") > 0) {
                    int pos = contentType.indexOf(";");
                    charset = contentType.substring(pos+1);
                    contentType = contentType.substring(0, pos);
                    pos = charset.indexOf("=");
                    if (pos > 0) {
                        charset = charset.substring(pos+1);
                    }
                    charset = StringUtil.replaceAll(charset, "\"", "");
                    charset = StringUtil.replaceAll(charset, "'", "");
                    charset = charset.toLowerCase();
                }
                NSURLResponse response = new NSURLResponse.Builder<NSURLResponse.Builder>()
                        .url(url)
                        .expectedContentLength(req.getContentLength())
                        .mimeType(contentType)
                        .textEncodingName(charset)
                        .unknownLength(req.getContentLength() < 0)
                        .suggestedFilename(null)
                        .build();
                NSError error = (req.getResponseCode() >= 200 && req.getResponseCode() < 300) ?
                        null : new NSError(req.getResponseErrorMessage(), 0);

                try {
                    downloadTaskCallback(taskId, tempFile.getAbsolutePath(), response, error);
                } finally {
                    CN.setTimeout(3000, () ->{
                        if (tempFile.exists()) {
                            tempFile.delete();
                        }
                    });

                }
            });
        }
        return task;
    }

    /**
     * Checks if this class is supported on the current platform.
     *
     * <p>NOTE: This will return true for all platforms.  Use {@link #isEmulated()} to determine
     * if the platform emulates this class.</p>
     * @return True if the class is supported or emulated.
     */
    public static boolean isSupported() {
        return true;
    }

    /**
     * Checks if this class is emulated on the current platform.
     * @return True if this class is emulated.  This will return {@literal true} on all platforms except for iOS.
     */
    public static boolean isEmulated() {
        try {
            return !new NSURLSession().session.isSupported();
        } catch (Exception ex) {
            return true;
        }
    }


    /**
     * Prepends "file:" to the beginning of a path if it hasn't already.  Any paths returned from
     * native need to have a file: prefix so we know that they are absolute.
     * @param path
     * @return
     */
    private static String prefixFile(String path) {
        if (path.startsWith("file:")) return path;
        return "file:"+path;
    }
}
