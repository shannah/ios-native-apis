package com.codenameone.ios.foundation;

import com.codename1.io.Log;

import java.io.IOException;

public class NativeCallbacks {

    public static void downloadTaskCallback(long taskId, String location, String nsurlResponse, String nsError) {
        NSURLResponse nsurlResponse1;
        try {
            nsurlResponse1 = NSURLResponse.parseJSON(nsurlResponse);
        } catch (IOException ex) {
            Log.p("Failed to parse URL response received from download task "+taskId+".  Passing it as null to callback");
            Log.e(ex);
            nsurlResponse1 = null;
        }

        NSError nsError1;
        try {
            nsError1 = NSError.parseJSON(nsError);
        } catch (IOException ex) {
            Log.p("Failed to parse NSError response received from download task "+taskId+".  Passing it as null to callback");
            Log.e(ex);
            nsError1 = null;
        }
        NSURLSession.downloadTaskCallback(taskId, location, nsurlResponse1, nsError1);
    }
}
