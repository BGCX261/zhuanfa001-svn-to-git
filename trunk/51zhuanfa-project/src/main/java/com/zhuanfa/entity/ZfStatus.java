/**
 * Copyright 2013 51zhuanfa Inc. All Rights Reserved. 
 */
package com.zhuanfa.entity;

import java.util.Date;

import weibo4j.model.Status;

/**
 * @author panhz
 * 
 */
public class ZfStatus {

	private String uid;// 用户UID
	private Date createdAt; // status创建时间
	private String statusId; // status id
	private String mid; // 微博MID

	private transient Status status;// 新浪微博，不保存到数据库

	// 佣金=fee * Math.sqrt(粉丝数) / 10
	// 用户佣金=fee * Math.sqrt(粉丝数) / 10 * 0.8
	// 平台佣金=fee * Math.sqrt(粉丝数) / 10 * 0.2
	private Double fee = 0.5;// 有100粉丝数用户转发该微博，愿意付出佣金

	private Integer state;// 状态 0：在新浪微博里发布， 1：开始在51zhuanfa竞价排名， 2：停止在51转发竞价排名

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

}
