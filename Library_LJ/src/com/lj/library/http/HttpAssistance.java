package com.lj.library.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

/**
 * 网络请求铺助类.
 * 
 * @time 2014年12月11日 上午11:36:34
 * @author jie.liu
 */
public class HttpAssistance {

	private static final int DEFAULT_CONNECTION_TIMEOUT = (5 * 1000); // milliseconds
	private static final int DEFAULT_SOCKET_TIMEOUT = (20 * 1000); // milliseconds

	public static HttpClient getDefaultHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				DEFAULT_CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}

	public static String executeRequest(HttpClient client,
			HttpRequestBase request, HttpResponseWrapper responseWrapper)
			throws IOException, ClientProtocolException {
		HttpResponse response = client.execute(request);
		responseWrapper.response = response.getStatusLine().getStatusCode();
		if (responseWrapper.response == HttpStatus.SC_OK) {
			return getResult(response);
		}
		return null;
	}

	public static HttpEntity executeDownload(HttpClient client,
			HttpRequestBase request, HttpResponseWrapper responseWrapper)
			throws ClientProtocolException, IOException {
		HttpResponse response = client.execute(request);
		responseWrapper.response = response.getStatusLine().getStatusCode();
		if (responseWrapper.response == HttpStatus.SC_OK) {
			return response.getEntity();
		}
		return null;
	}

	private static String getResult(HttpResponse response) throws IOException {
		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
		return result;
	}

	public static void shutdown(HttpClient client) {
		if (client != null) {
			client.getConnectionManager().shutdown();
		}
	}
}
