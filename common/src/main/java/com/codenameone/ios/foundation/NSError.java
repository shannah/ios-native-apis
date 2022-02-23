package com.codenameone.ios.foundation;

import com.codename1.io.JSONParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

/**
 * Ancapsulates an <a href="https://developer.apple.com/documentation/foundation/nserror?language=objc">NSError</a>.
 */
public class NSError extends RuntimeException {

    private int code;



    public NSError(String localizedDescription, int code) {
        super(localizedDescription);
        this.code = code;
    }

    public NSError(Throwable cause) {
        super(cause.getMessage(), cause);
        this.code =0;
    }

    public String getLocalizedDescription() {
        return getMessage();
    }

    public int getCode() {
        return code;
    }

    public static NSError parseJSON(String data) throws IOException {
        if (data == null || data.trim().length() == 0) return null;
        JSONParser parser = new JSONParser();
        Map<String,Object> m = parser.parseJSON(new StringReader(data));

        int code = ((Number)m.get("code")).intValue();
        String localizedDescription = (String)m.get("localizedDescription");
        return new NSError(localizedDescription, code);

    }

}
