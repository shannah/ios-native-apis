package com.codenameone.ios.foundation;

import com.codename1.io.JSONParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

/**
 * Encapsulates an <a href="https://developer.apple.com/documentation/foundation/nsurlresponse?language=objc">NSURLResponse</a>.
 */
public class NSURLResponse {
    private String mimeType, suggestedFilename, textEncodingName, url;
    private long expectedContentLength;
    private boolean unknownLength;

    public static NSURLResponse parseJSON(String data) throws IOException {
        if (data == null || data.trim().length() == 0) return null;
        JSONParser parser = new JSONParser();
        Map<String,Object> m = parser.parseJSON(new StringReader(data));
        NSURLResponse out = new NSURLResponse();
        out.mimeType = (String)m.get("MIMEType");
        out.suggestedFilename = (String)m.get("suggestedFilename");
        out.textEncodingName = (String)m.get("textEncodingName");
        out.url = (String)m.get("URL");
        out.expectedContentLength = Long.parseLong((String)m.get("expectedContentLength"));
        Object unknownLength = m.get("unknownLength");
        if (unknownLength instanceof Number) {
            out.unknownLength = ((Number)unknownLength).intValue() != 0;
        } else if (unknownLength instanceof Boolean) {
            out.unknownLength = (Boolean)unknownLength;
        }

        return out;
    }

    public static class Builder<T extends Builder> {
        private String mimeType, suggestedFilename, textEncodingName, url;
        private long expectedContentLength;
        private boolean unknownLength;

        public T mimeType(String mimeType) {
            this.mimeType = mimeType;
            return (T)this;
        }

        public T suggestedFilename(String suggestedFilename) {
            this.suggestedFilename = suggestedFilename;
            return (T)this;
        }

        public T textEncodingName(String textEncodingName) {
            this.textEncodingName = textEncodingName;
            return (T)this;
        }

        public T url(String url) {
            this.url = url;
            return (T)this;
        }

        public T expectedContentLength(long len) {
            this.expectedContentLength = len;
            return (T)this;
        }

        public T unknownLength(boolean unknownLength) {
            this.unknownLength = unknownLength;
            return (T)this;
        }

        public NSURLResponse build() {
            NSURLResponse out = new NSURLResponse();
            out.unknownLength = unknownLength;
            out.expectedContentLength = expectedContentLength;
            out.mimeType = mimeType;
            out.textEncodingName = textEncodingName;
            out.url = url;
            return out;
        }
    }
}
