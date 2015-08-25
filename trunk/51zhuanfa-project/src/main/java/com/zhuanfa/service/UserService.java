/**
 * Copyright 2013 51zhuanfa Inc. All Rights Reserved. 
 */
package com.zhuanfa.service;

import org.springframework.stereotype.Component;

import weibo4j.Oauth;
import weibo4j.Users;
import weibo4j.http.AccessToken;
import weibo4j.model.User;
import weibo4j.model.WeiboException;

import bigtable.ext.DatastoreCallback;
import bigtable.ext.RowMapper;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.zhuanfa.entity.ZfUser;

/**
 * @author panhz
 * 
 */
@Component
public class UserService extends BaseService {

	public AccessToken getAccessToken(String code) {
		Oauth oauth = new Oauth();
		try {
			return oauth.getAccessTokenByCode(code);
		} catch (WeiboException e) {
			log.severe(e.getMessage());
			return null;
		}
	}

	public User findUserFromWeibo(String access_token, String uid) {

		User user = null;

		// 获取用户
		Users um = new Users();
		um.client.setToken(access_token);
		try {
			user = um.showUserById(uid);
		} catch (WeiboException e) {
			log.severe(e.getMessage());
			user = null;
		}

		return user;
	}

	public ZfUser get(String uid) {
		ZfUser user = template.get("ZfUser", uid, new RowMapper<ZfUser>() {

			@Override
			public ZfUser mapRow(Entity entity, int rowNum) {
				ZfUser user = new ZfUser();
				String id = (String) entity.getProperty("id");
				String screenName = (String) entity.getProperty("screenName");
				String name = (String) entity.getProperty("name");
				boolean verified = (Boolean) entity.getProperty("verified");
				int verifiedType = ((Long) entity.getProperty("verifiedType"))
						.intValue();
				user.setId(id);
				user.setName(name);
				user.setScreenName(screenName);
				user.setVerified(verified);
				user.setVerifiedType(verifiedType);
				return user;
			}
		});

		return user;
	}

	public boolean exist(String uid) {
		if (uid == null)
			return false;
		ZfUser user = get(uid);
		return user != null;
	}

	public Key save(final ZfUser user) {
		return template.execute(new DatastoreCallback<Key>() {

			@Override
			public Key doInDatastore(DatastoreService datastore) {
				Entity entity = new Entity("ZfUser", user.getId());
				entity.setProperty("id", user.getId());
				entity.setProperty("screenName", user.getScreenName());
				entity.setProperty("name", user.getName());
				entity.setProperty("verified", user.isVerified());
				entity.setProperty("verifiedType", user.getVerifiedType());
				return datastore.put(entity);
			}
		});
	}

}
