/**
 * Copyright 2013 51zhuanfa Inc. All Rights Reserved. 
 */
package bigtable.ext;

import com.google.appengine.api.datastore.Entity;

/**
 * @author panhz
 * 
 */
public interface RowMapper<T> {

	public T mapRow(Entity entity, int rowNum);

}
