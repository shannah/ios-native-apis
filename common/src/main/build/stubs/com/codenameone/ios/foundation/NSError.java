package com.codenameone.ios.foundation;


/**
 *  Ancapsulates an <a href="https://developer.apple.com/documentation/foundation/nserror?language=objc">NSError</a>.
 */
public class NSError extends RuntimeException {

	public NSError(String localizedDescription, int code) {
	}

	public NSError(Throwable cause) {
	}

	public String getLocalizedDescription() {
	}

	public int getCode() {
	}

	public static NSError parseJSON(String data) {
	}
}
