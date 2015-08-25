/**
 * Copyright 2013 51zhuanfa Inc. All Rights Reserved. 
 */
package com.zhuanfa.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * weibo退出后访问该Controller
 * 
 * @author panhz
 * 
 */
@Controller
@RequestMapping(value = "/logout")
public class LogoutController extends BaseController {

	@RequestMapping(value = "")
	public String doCallback(HttpServletRequest request,
			HttpServletResponse response) {
		SecurityUtils.getSubject().logout();
		return "redirect:/";
	}

}
