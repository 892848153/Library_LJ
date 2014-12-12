package com.lj.library.http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

public class HttpConnect {

	private static final int DEFAULT_CONNECTION_TIMEOUT = 8000;

	public static String doGet(String url, List<BasicNameValuePair> params) {
		Log.e("info", "HttpConnection---------->url=" + url);
		HttpClient client = getDefaultHttpClient();
		try {
			url = buildGetUrl(url, params);
			HttpGet request = new HttpGet(url);
			return execute(client, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			shutdown(client);
		}
	}

	private static String buildGetUrl(String url,
			List<BasicNameValuePair> params) {
		if (params != null && params.size() > 0) {
			url = url + "?" + URLEncodedUtils.format(params, HTTP.UTF_8);
		}
		return url;
	}

	public static String doPost(String url, List<BasicNameValuePair> params) {
		Log.e("info", "HttpConnection---------->url=" + url);
		HttpClient client = getDefaultHttpClient();
		try {
			HttpPost request = new HttpPost(url);
			buildPostParams(params, request);
			return execute(client, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			shutdown(client);
		}
	}

	private static void buildPostParams(List<BasicNameValuePair> params,
			HttpPost request) throws UnsupportedEncodingException {
		if (params != null && params.size() > 0) {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		}
	}

	/**
	 * 
	 * @param url
	 *            上传的地址
	 * @param params
	 *            上传需要传递的参数
	 * @param files
	 * @return
	 */
	public static String doUpload(String url, Map<String, String> params,
			Map<String, String> files) {
		HttpClient client = getDefaultHttpClient();
		try {
			HttpPost request = new HttpPost(url);
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
					.create();
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			entityBuilder.setCharset(Charset.forName(HTTP.UTF_8));
			if (params != null) {
				for (Entry<String, String> param : params.entrySet()) {
					entityBuilder.addTextBody(param.getKey(), param.getValue());
				}
			}

			if (files != null) {
				for (Entry<String, String> fileEntry : files.entrySet()) {
					File file = new File(fileEntry.getValue());
					if (file.exists()) {
						entityBuilder.addBinaryBody(fileEntry.getKey(), file);
					}
				}
			}

			request.setEntity(entityBuilder.build());
			return execute(client, request);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			shutdown(client);
		}
		return null;
	}

	// public static String doUpload(String url, String content, String file) {
	// HttpClient client = getDefaultHttpClient();
	// try {
	// HttpPost request = new HttpPost(url);
	// MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
	// .create();
	// entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	// entityBuilder.setCharset(Charset.forName(HTTP.UTF_8));
	// if (file != null) {
	// entityBuilder.addPart("image1", new FileBody(new File(file)));
	// entityBuilder.addBinaryBody("", new File(file));
	// }
	// if (content != null)
	// entityBuilder.addTextBody("content", content);
	// request.setEntity(entityBuilder.build());
	// execute(client, request);
	// HttpResponse response = client.execute(request);
	// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	// return getResult(response);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// } finally {
	// shutdown(client);
	// }
	// return null;
	// }

	// public static String doUpload(String url, Map<String, String> params,
	// Map<String, String>) {
	// HttpClient client = getDefaultHttpClient();
	// try {
	// HttpPost request = new HttpPost(url);
	// MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
	// .create();
	// entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	// entityBuilder.setCharset(Charset.forName(HTTP.UTF_8));
	// if (files != null) {
	// for (int i = 0; i < files.size(); i++) {
	// postEntity.addPart("image" + (i + 1), new FileBody(
	// new File(files.get(i))));
	// }
	// }
	// if (content != null)
	// postEntity.addPart("content",
	// new StringBody(content, Charset.forName("UTF-8")));
	// request.setEntity(postEntity);
	// HttpResponse response = client.execute(request);
	// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	// return getResult(response);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// } finally {
	// shutdown(client);
	// }
	// return null;
	// }

	public static String getCity(double longitude, double latitude) {
		try {
			HttpClient client = getDefaultHttpClient();
			HttpGet httpGet = new HttpGet(
					"http://api.map.baidu.com/geocoder?output=json&location="
							+ latitude + ",%20" + longitude
							+ "&key=d03481035d6c92766cca10c923f6762c");
			HttpResponse response = client.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String string = EntityUtils.toString(response.getEntity());
				if (string != null) {
					JSONObject jsonObject = new JSONObject(string);
					String status = (String) jsonObject.get("status");
					if ("OK".equals(status)) {
						JSONObject address = jsonObject.getJSONObject("result")
								.getJSONObject("addressComponent");
						String city = (String) address.get("city");
						if (city != null && city.length() >= 3)
							city = city.substring(0, city.length() - 1);
						return city;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getAddress(double endLatitude, double endLongitude) {
		HttpClient client = getDefaultHttpClient();
		HttpGet httpGet = new HttpGet(
				"http://api.map.baidu.com/geocoder?output=json&location="
						+ endLatitude + ",%20" + endLongitude
						+ "&key=d03481035d6c92766cca10c923f6762c");
		try {
			HttpResponse response = client.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String string = EntityUtils.toString(response.getEntity());
				JSONObject jsonObject = new JSONObject(string);
				String status = (String) jsonObject.get("status");
				StringBuffer sb = new StringBuffer();
				if ("OK".equals(status)) {
					JSONObject address = jsonObject.getJSONObject("result")
							.getJSONObject("addressComponent");
					String province = (String) address.get("province");
					String streetnumber = (String) address.get("street_number");
					String city = (String) address.get("city");
					String district = (String) address.get("district");
					String street = (String) address.get("street");
					sb.append(province).append(" ").append(city).append(" ")
							.append(district).append(" ").append(street)
							.append(" ").append(streetnumber);
					return sb.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			shutdown(client);
		}
		return null;
	}

	private static HttpClient getDefaultHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				DEFAULT_CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams,
				DEFAULT_CONNECTION_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}

	private static String execute(HttpClient client, HttpRequestBase request)
			throws IOException, ClientProtocolException {
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return getResult(response);
		}
		return null;
	}

	private static String getResult(HttpResponse response) throws IOException {
		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
		Log.e("info", "HttpConnection---------->result=" + result);
		return result;
	}

	private static void shutdown(HttpClient client) {
		if (client != null) {
			client.getConnectionManager().shutdown();
		}
	}
}
