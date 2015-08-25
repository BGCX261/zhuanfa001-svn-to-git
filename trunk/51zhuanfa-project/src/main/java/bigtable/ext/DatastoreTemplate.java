/**
 * Copyright 2013 51zhuanfa Inc. All Rights Reserved. 
 */
package bigtable.ext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

/**
 * @author panhz
 * 
 */
public class DatastoreTemplate {

	private static Logger log = Logger.getLogger(DatastoreTemplate.class
			.getName());

	private DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();

	/**
	 * 
	 * @param callback
	 */
	public <T> T execute(DatastoreCallback<T> callback) {
		Transaction txn = datastore.beginTransaction();
		try {
			T t = callback.doInDatastore(datastore);
			txn.commit();
			return t;
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	/**
	 * 
	 * @param query
	 * @param extractor
	 * @return
	 */
	public <T> T find(Query query, ResultExtractor<T> extractor) {
		return find(query, FetchOptions.Builder.withDefaults(), extractor);
	}

	/**
	 * 
	 * @param query
	 * @param fetchOptions
	 * @param extractor
	 * @return
	 */
	public <T> T find(Query query, FetchOptions fetchOptions,
			ResultExtractor<T> extractor) {
		try {
			PreparedQuery pq = datastore.prepare(query);
			List<Entity> entities = pq.asList(fetchOptions);
			if (entities == null || entities.isEmpty())
				return null;
			return extractor.extractData(entities);
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new DatastoreFailureException(e.getMessage());
		}
	}

	public <T> List<T> find(Query query, RowMapper<T> mapper) {
		return find(query, FetchOptions.Builder.withDefaults(), mapper);
	}

	/**
	 * 
	 * @param query
	 * @param fetchOptions
	 * @param mapper
	 * @return
	 */
	public <T> List<T> find(Query query, FetchOptions fetchOptions,
			RowMapper<T> mapper) {

		try {
			PreparedQuery pq = datastore.prepare(query);
			List<Entity> entities = pq.asList(fetchOptions);
			if (entities == null || entities.isEmpty())
				return Collections.emptyList();

			List<T> list = new ArrayList<T>();
			for (int i = 0, size = entities.size(); i < size; i++) {
				Entity entity = entities.get(i);
				T t = mapper.mapRow(entity, i);
				list.add(t);
			}

			return list;
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new DatastoreFailureException(e.getMessage());
		}

	}

	/**
	 * 
	 * @param query
	 * @param pageable
	 * @param mapper
	 * @return
	 */
	public <T> Page<T> findPage(Query query, Pageable pageable,
			RowMapper<T> mapper) {

		try {
			PreparedQuery pq = datastore.prepare(query);
			List<Entity> entities = pq.asList(FetchOptions.Builder.withOffset(
					pageable.getOffset()).limit(pageable.getPageSize()));
			int total = pq.countEntities(FetchOptions.Builder.withDefaults());

			List<T> content = new ArrayList<T>();

			if (entities == null)
				return new PageImpl<T>(content);

			for (int i = 0, size = entities.size(); i < size; i++) {
				T t = mapper.mapRow(entities.get(i), i);
				content.add(t);
			}

			return new PageImpl<T>(content, pageable, total);
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new DatastoreFailureException(e.getMessage());
		}
	}

	public <T> T get(String kind, String keyName, RowMapper<T> mapper) {
		Key key = KeyFactory.createKey(kind, keyName);
		return get(key, mapper);
	}

	public <T> T get(Key key, RowMapper<T> mapper) {
		try {
			Entity entity = null;
			try {
				entity = datastore.get(key);
			} catch (EntityNotFoundException e) {
				log.info(e.getMessage());
				entity = null;
			}

			if (entity == null)
				return null;

			return mapper.mapRow(entity, 0);
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new DatastoreFailureException(e.getMessage());
		}

	}
}
