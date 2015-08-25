/**
 * Copyright 2013 51zhuanfa Inc. All Rights Reserved. 
 */
package com.zhuanfa.web;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import weibo4j.http.AccessToken;

import com.zhuanfa.service.RegisterService;
import com.zhuanfa.service.UserService;

/**
 * weibo认证成功后访问该Controller
 * 
 * @author panhz
 * 
 */
@Controller
@RequestMapping(value = "/login")
public class LoginController extends BaseController {

	private UserService userService;

	private RegisterService registerService;

	@RequestMapping(value = "")
	public String login(@RequestParam("code") String code) {
		try {
			if (code == null)
				return "/unauthorized";
			AccessToken accessToken = userService.getAccessToken(code);
			if (accessToken == null)
				return "/unauthorized";
			// 验证用户是否注册
			boolean existUser = userService.exist(accessToken.getUid());
			// 没注册用户自动注册
			if (!existUser) {
				int registerResult = registerService.register(
						accessToken.getAccessToken(), accessToken.getUid());
				if (registerResult != 2)
					return "/unauthorized";
			}
			// 注册后自动登录，密码为空
			UsernamePasswordToken token = new UsernamePasswordToken(
					accessToken.getUid(), "");
			SecurityUtils.getSubject().login(token);
			// 保存accessToken
			Session session = SecurityUtils.getSubject().getSession();
			session.setAttribute(ACCESS_TOKEN_SESSION_KEY, accessToken);
			return "redirect:/";
		} catch (AuthenticationException e) {
			log.severe(e.getMessage());
			throw new RuntimeException(e);
		}

	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setRegisterService(RegisterService registerService) {
		this.registerService = registerService;
	}

}
