package org.pipservices.data.persistence;

import org.pipservices.commons.config.*;
import org.pipservices.commons.data.*;
import org.pipservices.commons.errors.*;

/**
 * Abstract persistence component that stores data in flat files
 * and implements a number of CRUD operations over data items with unique ids.
 * The data items must implement IIdentifiable interface.
 * 
 * In basic scenarios child classes shall only override getPageByFilter(),
 * getListByFilter() or deleteByFilter() operations with specific filter function.
 * All other operations can be used out of the box. 
 * 
 * In complex scenarios child classes can implement additional operations by 
 * accessing cached items via this._items property and calling save() method
 * on updates.
 * 
 * ### Configuration parameters ###
 * 
 * path:                    path to the file where data is stored
 * options:
 *     max_page_size:       Maximum number of items returned in a single page (default: 100)
 * 
 * ### References ###
 * 
 * - *:logger:*:*:1.0         (optional) ILogger components to pass log messages
 * <p>
 * ### Examples ###
 * <pre>
 * {@code
 * class MyFilePersistence extends IdentifiableFilePersistence<MyData, String> {
 *   public MyFilePersistence(String path) {
 *     super(MyData.class, new JsonPersister(path));
 *   }
 * 
 *   private Predicate<MyData> composeFilter(FilterParams filter) {
 *       filter = filter != null ? filter : new FilterParams();
 *       String name = filter.getAsNullableString("name");
 *       return (item) -> {
 *           if (name != null && item.name != name)
 *               return false;
 *           return true;
 *       };
 *   }
 * 
 *   public DataPage<MyData> getPageByFilter(String correlationId, FilterParams filter, PagingParams paging) {
 *       super.getPageByFilter(correlationId, this.composeFilter(filter), paging, null, null);
 *   }
 * 
 * }
 * 
 * MyFilePersistence persistence = new MyFilePersistence("./data/data.json");
 * 
 * MyData item = persistence.create("123", new MyData("1", "ABC"));
 * DataPage<MyData> mydata = persistence.getPageByFilter(
 *         "123",
 *         FilterParams.fromTuples("name", "ABC"),
 *         null, null, null);
 * System.out.println(page.getData().toString());          // Result: { id: "1", name: "ABC" }
 * persistence.deleteById("123", "1");
 * ...
 * }
 * </pre>
 * @see JsonFilePersister
 * @see MemoryPersistence
 */
public class IdentifiableFilePersistence<T extends IIdentifiable<K>, K> extends IdentifiableMemoryPersistence<T, K> {
	protected JsonFilePersister<T> _persister;

	// Pass the item type since Jackson cannot recognize type from generics
	// This is related to Java type erasure issue
	/**
	 * Creates a new instance of the persistence.
	 * 
	 * @param type the class type
	 */
	protected IdentifiableFilePersistence(Class<T> type) {
		this(type, null);
	}

	/**
	 * Creates a new instance of the persistence.
	 * 
	 * @param type      the class type
	 * @param persister (optional) a persister component that loads and saves data
	 *                  from/to flat file.
	 */
	protected IdentifiableFilePersistence(Class<T> type, JsonFilePersister<T> persister) {
		super(type, persister == null ? new JsonFilePersister<T>(type) : persister,
				persister == null ? new JsonFilePersister<T>(type) : persister);

		_persister = persister;
//    	super(type);
//    	
//    	_persister = new JsonFilePersister<T>(type);
//    	_loader = _persister;
//    	_saver = _persister;
	}

	/**
	 * Configures component by passing configuration parameters.
	 * 
	 * @param config configuration parameters to be set.
	 * @throws ConfigException when configuration is wrong.
	 */
	public void configure(ConfigParams config) throws ConfigException {
		super.configure(config);
		_persister.configure(config);
	}
}