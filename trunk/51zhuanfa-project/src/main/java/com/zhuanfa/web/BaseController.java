package com.zhuanfa.web;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

import weibo4j.http.AccessToken;

import com.zhuanfa.entity.ZfUser;

/**
 */
public class BaseController {

	protected final Logger log = Logger.getLogger(getClass().getName());

	public static final String ACCESS_TOKEN_SESSION_KEY = "access_token";

	/**
	 * 获取access_token
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public AccessToken getAccessToken() {
		Session session = SecurityUtils.getSubject().getSession();
		AccessToken accessToken = (AccessToken) session
				.getAttribute(ACCESS_TOKEN_SESSION_KEY);
		return accessToken;

	}

	/**
	 * 获取当前登录的微薄用户
	 * 
	 * @return
	 */
	public ZfUser getCurrentUser() {
		return (ZfUser) SecurityUtils.getSubject().getPrincipal();
	}
}
