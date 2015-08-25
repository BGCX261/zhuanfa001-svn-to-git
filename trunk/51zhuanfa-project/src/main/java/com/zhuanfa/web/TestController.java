package com.zhuanfa.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zhuanfa.entity.ZfUser;

/**
 */
@Controller
@RequestMapping(value = "/test")
public class TestController extends BaseController {

	@RequestMapping(value = "")
	public String test(Model model) {
		ZfUser user = getCurrentUser();
		model.addAttribute("user", user);
		return "test";
	}

}
