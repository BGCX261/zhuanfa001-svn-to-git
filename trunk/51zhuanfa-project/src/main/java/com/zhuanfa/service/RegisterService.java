/**
 * Copyright 2013 51zhuanfa Inc. All Rights Reserved. 
 */
package com.zhuanfa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import weibo4j.model.User;

import com.zhuanfa.entity.ZfUser;

/**
 * @author panhz
 * 
 */
@Component
public class RegisterService extends BaseService {

	private UserService userService;

	/**
	 * 
	 * @param access_token
	 * @param uid
	 * @return <ul>
	 *         <li>[-1:access_token或uid为空]</li>
	 *         <li>[0:uid已经存在]</li>
	 *         <li>[1:access_token或uid有误]</li>
	 *         <li>[2:成功]</li>
	 *         </ul>
	 * 
	 */
	public int register(String access_token, String uid) {
		try {
			Assert.notNull(access_token, "access_token is null");
			Assert.notNull(uid, "uid is null");
		} catch (IllegalArgumentException e) {
			log.warning(e.getMessage());
			return -1;
		}
		if (userService.exist(uid))
			return 0;

		User user = userService.findUserFromWeibo(access_token, uid);
		if (user == null)
			return 1;

		ZfUser zfuser = new ZfUser();
		zfuser.setId(user.getId());
		zfuser.setName(user.getName());
		zfuser.setScreenName(user.getScreenName());

		userService.save(zfuser);
		return 2;

	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
