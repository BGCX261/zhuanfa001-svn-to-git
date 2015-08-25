package bigtable.ext;

import java.util.List;

import com.google.appengine.api.datastore.Entity;

/**
 * 
 * @author panhz
 * 
 */
public interface ResultExtractor<T> {

	public T extractData(List<Entity> iterable);
}
