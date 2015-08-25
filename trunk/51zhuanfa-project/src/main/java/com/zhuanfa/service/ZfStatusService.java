/**
 * Copyright 2013 51zhuanfa Inc. All Rights Reserved. 
 */
package com.zhuanfa.service;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;

import bigtable.ext.DatastoreCallback;
import bigtable.ext.RowMapper;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.zhuanfa.entity.ZfStatus;

/**
 * @author panhz
 * 
 */
@Component
public class ZfStatusService extends BaseService {

	public Status findStatusFromWeibo(String accessToken, String statusId) {
		Timeline timeline = new Timeline();
		timeline.setToken(accessToken);
		try {
			return timeline.showStatus(statusId);
		} catch (WeiboException e) {
			log.severe(e.getMessage());
			return null;
		}
	}

	public ZfStatus get(final String accessToken, String statusId) {
		return template.get("ZfStatus", statusId, new RowMapper<ZfStatus>() {

			@Override
			public ZfStatus mapRow(Entity entity, int rowNum) {
				ZfStatus zfStatus = new ZfStatus();
				zfStatus.setCreatedAt((Date) entity.getProperty("createdAt"));
				zfStatus.setFee((Double) entity.getProperty("fee"));
				zfStatus.setMid((String) entity.getProperty("mid"));
				zfStatus.setState((Integer) entity.getProperty("state"));
				zfStatus.setStatusId((String) entity.getProperty("statusId"));
				zfStatus.setStatus(findStatusFromWeibo(accessToken,
						zfStatus.getStatusId()));
				zfStatus.setUid((String) entity.getProperty("uid"));
				return zfStatus;
			}
		});
	}

	public Key save(ZfStatus zfStatus) {

		String uid = zfStatus.getUid();
		if (uid == null) {
			String msg = "用户id不能为空";
			log.severe(msg);
			throw new RuntimeException(msg);
		}
		Key uidKey = KeyFactory.createKey("User", uid);
		final Entity entity = new Entity("ZfStatus", zfStatus.getStatusId(),
				uidKey);
		entity.setProperty("createAt", zfStatus.getCreatedAt());
		entity.setProperty("fee", zfStatus.getFee());
		entity.setProperty("mid", zfStatus.getMid());
		entity.setProperty("state", zfStatus.getState());
		entity.setProperty("statusId", zfStatus.getStatusId());
		entity.setProperty("uid", zfStatus.getUid());

		return template.execute(new DatastoreCallback<Key>() {

			@Override
			public Key doInDatastore(DatastoreService datastore) {
				return datastore.put(entity);
			}
		});
	}

	public Page<ZfStatus> findBy(final String accessToken, String uid,
			int pageNumber, int pageSize) {
		PageRequest request = new PageRequest(pageNumber, pageSize);
		Key uidKey = KeyFactory.createKey("User", uid);
		Query query = new Query("ZfStatus").setAncestor(uidKey).addSort(
				"createdAt", SortDirection.DESCENDING);
		return template.findPage(query, request, new RowMapper<ZfStatus>() {

			@Override
			public ZfStatus mapRow(Entity entity, int rowNum) {
				ZfStatus zfStatus = new ZfStatus();
				zfStatus.setCreatedAt((Date) entity.getProperty("createdAt"));
				zfStatus.setFee((Double) entity.getProperty("fee"));
				zfStatus.setMid((String) entity.getProperty("mid"));
				zfStatus.setState((Integer) entity.getProperty("state"));
				zfStatus.setStatusId((String) entity.getProperty("statusId"));
				zfStatus.setStatus(findStatusFromWeibo(accessToken,
						zfStatus.getStatusId()));
				zfStatus.setUid((String) entity.getProperty("uid"));
				return zfStatus;
			}
		});
	}
}
