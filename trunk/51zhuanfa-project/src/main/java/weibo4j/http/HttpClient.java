package weibo4j.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import weibo4j.model.Configuration;
import weibo4j.model.Paging;
import weibo4j.model.PostParameter;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

/**
 * 使用appengine-api URLFetchService 重新封装
 * 
 * @author panhz
 * 
 */
public class HttpClient implements java.io.Serializable {

	private static final long serialVersionUID = -176092625883595547L;
	private static final int OK = 200; // OK: Success!
	private static final int NOT_MODIFIED = 304; // Not Modified: There was no
													// new data to return.
	private static final int BAD_REQUEST = 400; // Bad Request: The request was
												// invalid. An accompanying
												// error message will explain
												// why. This is the status code
												// will be returned during rate
												// limiting.
	private static final int NOT_AUTHORIZED = 401; // Not Authorized:
													// Authentication
													// credentials were missing
													// or incorrect.
	private static final int FORBIDDEN = 403; // Forbidden: The request is
												// understood, but it has been
												// refused. An accompanying
												// error message will explain
												// why.
	private static final int NOT_FOUND = 404; // Not Found: The URI requested is
												// invalid or the resource
												// requested, such as a user,
												// does not exists.
	private static final int NOT_ACCEPTABLE = 406; // Not Acceptable: Returned
													// by the Search API when an
													// invalid format is
													// specified in the request.
	private static final int INTERNAL_SERVER_ERROR = 500;// Internal Server
															// Error: Something
															// is broken. Please
															// post to the group
															// so the Weibo team
															// can investigate.
	private static final int BAD_GATEWAY = 502;// Bad Gateway: Weibo is down or
												// being upgraded.
	private static final int SERVICE_UNAVAILABLE = 503;// Service Unavailable:
														// The Weibo servers are
														// up, but overloaded
														// with requests. Try
														// again later. The
														// search and trend
														// methods use this to
														// indicate when you are
														// being rate limited.

	private String token;

	public String setToken(String token) {
		this.token = token;
		return this.token;
	}

	private final static boolean DEBUG = Configuration.getDebug();
	static Logger log = Logger.getLogger(HttpClient.class.getName());

	public HttpClient() {
	}

	/**
	 * log调试
	 * 
	 */
	private static void log(String message) {
		if (DEBUG) {
			log.info(message);
		}
	}

	/**
	 * 处理http getmethod 请求
	 * 
	 */

	public Response get(String url) throws WeiboException {

		return get(url, new PostParameter[0]);

	}

	public Response get(String url, PostParameter[] params)
			throws WeiboException {
		log("Request:");
		log("GET:" + url);
		if (null != params && params.length > 0) {
			String encodedParams = HttpClient.encodeParameters(params);
			if (-1 == url.indexOf("?")) {
				url += "?" + encodedParams;
			} else {
				url += "&" + encodedParams;
			}
		}
		return httpRequest(url, null, HTTPMethod.GET);

	}

	public Response get(String url, PostParameter[] params, Paging paging)
			throws WeiboException {
		if (null != paging) {
			List<PostParameter> pagingParams = new ArrayList<PostParameter>(4);
			if (-1 != paging.getMaxId()) {
				pagingParams.add(new PostParameter("max_id", String
						.valueOf(paging.getMaxId())));
			}
			if (-1 != paging.getSinceId()) {
				pagingParams.add(new PostParameter("since_id", String
						.valueOf(paging.getSinceId())));
			}
			if (-1 != paging.getPage()) {
				pagingParams.add(new PostParameter("page", String
						.valueOf(paging.getPage())));
			}
			if (-1 != paging.getCount()) {
				if (-1 != url.indexOf("search")) {
					// search api takes "rpp"
					pagingParams.add(new PostParameter("rpp", String
							.valueOf(paging.getCount())));
				} else {
					pagingParams.add(new PostParameter("count", String
							.valueOf(paging.getCount())));
				}
			}
			PostParameter[] newparams = null;
			PostParameter[] arrayPagingParams = pagingParams
					.toArray(new PostParameter[pagingParams.size()]);
			if (null != params) {
				newparams = new PostParameter[params.length
						+ pagingParams.size()];
				System.arraycopy(params, 0, newparams, 0, params.length);
				System.arraycopy(arrayPagingParams, 0, newparams,
						params.length, pagingParams.size());
			} else {
				if (0 != arrayPagingParams.length) {
					String encodedParams = HttpClient
							.encodeParameters(arrayPagingParams);
					if (-1 != url.indexOf("?")) {
						url += "&" + encodedParams;
					} else {
						url += "?" + encodedParams;
					}
				}
			}
			return get(url, newparams);
		} else {
			return get(url, params);
		}
	}

	/**
	 * 处理http deletemethod请求
	 */

	public Response delete(String url, PostParameter[] params)
			throws WeiboException {
		if (0 != params.length) {
			String encodedParams = HttpClient.encodeParameters(params);
			if (-1 == url.indexOf("?")) {
				url += "?" + encodedParams;
			} else {
				url += "&" + encodedParams;
			}
		}
		return httpRequest(url, null, HTTPMethod.DELETE);

	}

	/**
	 * 处理http post请求
	 * 
	 */

	public Response post(String url, PostParameter[] params)
			throws WeiboException {
		return post(url, params, true);

	}

	public Response post(String url, PostParameter[] params,
			Boolean WithTokenHeader) throws WeiboException {
		log("Request:");
		log("POST" + url);
		if (WithTokenHeader) {
			return httpRequest(url, params, HTTPMethod.POST);
		} else {
			return httpRequest(url, params, HTTPMethod.POST, WithTokenHeader);
		}
	}

	public Response httpRequest(String url, PostParameter[] params,
			HTTPMethod method) throws WeiboException {
		return httpRequest(url, params, method, true);
	}

	public Response httpRequest(String urlStr, PostParameter[] params,
			HTTPMethod method, Boolean WithTokenHeader) throws WeiboException {
		InetAddress ipaddr;
		int responseCode = -1;
		try {
			URL url = new URL(urlStr);
			ipaddr = InetAddress.getLocalHost();
			List<HTTPHeader> headers = new ArrayList<HTTPHeader>();
			if (WithTokenHeader) {
				if (token == null) {
					throw new IllegalStateException("Oauth2 token is not set!");
				}
				headers.add(new HTTPHeader("Authorization", "OAuth2 " + token));
				headers.add(new HTTPHeader("API-RemoteIP", ipaddr
						.getHostAddress()));
				for (HTTPHeader hd : headers) {
					log(hd.getName() + ": " + hd.getValue());
				}
			}

			URLFetchService urlFetchService = URLFetchServiceFactory
					.getURLFetchService();
			HTTPRequest httpRequest = new HTTPRequest(url, method);
			for (HTTPHeader hd : headers) {
				httpRequest.addHeader(hd);
			}
			if (params != null && params.length > 0
					&& method == HTTPMethod.POST) {
				httpRequest.setPayload(encodeParameters(params).getBytes(
						"UTF-8"));
			}
			HTTPResponse httpResponse = urlFetchService.fetch(httpRequest);
			List<HTTPHeader> resHeader = httpResponse.getHeaders();
			responseCode = httpResponse.getResponseCode();
			log("Response:");
			log("https StatusCode:" + String.valueOf(responseCode));

			for (HTTPHeader header : resHeader) {
				log(header.getName() + ":" + header.getValue());
			}

			Response response = new Response();
			response.setResponseAsString(new String(httpResponse.getContent(),
					"UTF-8"));
			log(response.toString() + "\n");

			if (responseCode != OK)

			{
				try {
					throw new WeiboException(getCause(responseCode),
							response.asJSONObject(),
							httpResponse.getResponseCode());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return response;

		} catch (IOException ioe) {
			throw new WeiboException(ioe.getMessage(), ioe, responseCode);
		}

	}

	/*
	 * 对parameters进行encode处理
	 */
	public static String encodeParameters(PostParameter[] postParams) {
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < postParams.length; j++) {
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(postParams[j].getName(), "UTF-8"))
						.append("=")
						.append(URLEncoder.encode(postParams[j].getValue(),
								"UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
		}
		return buf.toString();
	}

	private static String getCause(int statusCode) {
		String cause = null;
		switch (statusCode) {
		case NOT_MODIFIED:
			break;
		case BAD_REQUEST:
			cause = "The request was invalid.  An accompanying error message will explain why. This is the status code will be returned during rate limiting.";
			break;
		case NOT_AUTHORIZED:
			cause = "Authentication credentials were missing or incorrect.";
			break;
		case FORBIDDEN:
			cause = "The request is understood, but it has been refused.  An accompanying error message will explain why.";
			break;
		case NOT_FOUND:
			cause = "The URI requested is invalid or the resource requested, such as a user, does not exists.";
			break;
		case NOT_ACCEPTABLE:
			cause = "Returned by the Search API when an invalid format is specified in the request.";
			break;
		case INTERNAL_SERVER_ERROR:
			cause = "Something is broken.  Please post to the group so the Weibo team can investigate.";
			break;
		case BAD_GATEWAY:
			cause = "Weibo is down or being upgraded.";
			break;
		case SERVICE_UNAVAILABLE:
			cause = "Service Unavailable: The Weibo servers are up, but overloaded with requests. Try again later. The search and trend methods use this to indicate when you are being rate limited.";
			break;
		default:
			cause = "";
		}
		return statusCode + ":" + cause;
	}

	public String getToken() {
		return token;
	}

}
