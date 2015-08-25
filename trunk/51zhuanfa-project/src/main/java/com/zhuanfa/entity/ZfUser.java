/**
 * Copyright 2013 51zhuanfa Inc. All Rights Reserved. 
 */
package com.zhuanfa.entity;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author panhz
 * 
 */
public class ZfUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6478090731043443269L;

	private String id; // 用户UID
	private String screenName; // 微博昵称
	private String name; // 友好显示名称，如Bill Gates,名称中间的空格正常显示(此特性暂不支持)
	private boolean verified; // 加V标示，是否微博认证用户
	private int verifiedType; // 认证类型

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public int getVerifiedType() {
		return verifiedType;
	}

	public void setVerifiedType(int verifiedType) {
		this.verifiedType = verifiedType;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
