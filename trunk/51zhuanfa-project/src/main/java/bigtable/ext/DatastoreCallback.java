/**
 * Copyright 2013 51zhuanfa Inc. All Rights Reserved. 
 */
package bigtable.ext;

import com.google.appengine.api.datastore.DatastoreService;

/**
 * @author panhz
 * 
 */
public interface DatastoreCallback<T> {

	public T doInDatastore(DatastoreService datastore);

}
