package com.switchpool.network;

import com.loopj.android.http.*;

public class HTTPRequestManager {
	  private static final String BASE_URL = "http://192.168.73.153/resourceManager/";

	  private static AsyncHttpClient client = new AsyncHttpClient();

	  public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
	      client.get(getAbsoluteUrl(url), params, responseHandler);
	  }

	  public static void post(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
	      client.post(getAbsoluteUrl(url), params, responseHandler);
	  }

	  private static String getAbsoluteUrl(String relativeUrl) {
	      return BASE_URL + relativeUrl;
	  }
}
