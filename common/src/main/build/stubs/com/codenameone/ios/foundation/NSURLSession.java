package com.codenameone.ios.foundation;


/**
 *  A class that wraps <a href="https://developer.apple.com/documentation/foundation/nsurlsession?language=objc">NSURLSession</a> on iOS, emulates
 *  it on other platforms with a cross-platform implementation.
 */
public class NSURLSession {

	/**
	 *  Creates a new NSURLSession
	 */
	public NSURLSession() {
	}

	/**
	 *  Gets a reference to the snared NSURLSession object.
	 *  @return
	 */
	public static NSURLSession getSharedSession() {
	}

	/**
	 *  Initiates an asynchronous download task for the given URL.
	 *  @param url The URL to download.
	 *  @return DownloadTask.
	 */
	public NSURLSession.DownloadTask downloadTaskWithURL(String url) {
	}

	/**
	 *  Checks if this class is supported on the current platform.
	 * 
	 *  <p>NOTE: This will return true for all platforms.  Use {@link #isEmulated()} to determine
	 *  if the platform emulates this class.</p>
	 *  @return True if the class is supported or emulated.
	 */
	public static boolean isSupported() {
	}

	/**
	 *  Checks if this class is emulated on the current platform.
	 *  @return True if this class is emulated.  This will return {@literal true} on all platforms except for iOS.
	 */
	public static boolean isEmulated() {
	}

	/**
	 *  A DownloadTask initiated by the {@link #downloadTaskWithURL(String)} method.
	 */
	public static class DownloadTask {


		public DownloadTask() {
		}

		/**
		 *  Gets details about the server response.
		 *  @return
		 */
		public NSURLResponse getNSURLResponse() {
		}

		/**
		 *  Gets the error associated with the request.  Or null if no error occurred.
		 *  @return
		 */
		public NSError getNSError() {
		}

		/**
		 *  Gets the temporary file that the URL contents were downloaded to.  This will
		 *  be deleted after the callbacks are finished, so you will need to copy or move this
		 *  file if you need it.
		 *  @return
		 */
		public File getLocation() {
		}
	}
}
