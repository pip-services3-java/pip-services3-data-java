package org.pipservices.data.persistence;

import org.pipservices.commons.config.*;
import org.pipservices.commons.errors.*;

/**
 * Abstract persistence component that stores data in flat files
 * and caches them in memory.
 * <p>
 * This is the most basic persistence component that is only
 * able to store data items of any type. Specific CRUD operations
 * over the data items must be implemented in child classes by
 * accessing <code>this._items</code> property and calling <code>save()</code> method.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>path:                path to the file where data is stored
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0   (optional) <a href="https://raw.githubusercontent.com/pip-services-java/pip-services-components-java/master/doc/api/org/pipservices/components/log/ILogger.html">ILogger</a> components to pass log messages
 * </ul>
 * <p>
 * ### Example ###
 * <pre>
 * {@code
 * class MyJsonFilePersistence extends FilePersistence<MyData> {
 *   public MyJsonFilePersistence(String path) {
 *     super(MyData.class, new JsonFilePersister(path));
 *   }
 * 
 *   public MyData getByName(String correlationId, String name) {
 *     MyData item = find(name); // search method
 *     ...
 *     return item;
 *   } 
 * 
 *   public MyData set(String correlatonId, MyData item) {
 *     this._items = filter(); // filter method
 *     ...
 *     this._items.add(item);
 *     this.save(correlationId);
 *   }
 * 
 * }
 * }
 * </pre>
 * @see MemoryPersistence
 * @see JsonFilePersister
 */
public abstract class FilePersistence<T> extends MemoryPersistence<T> implements IConfigurable {
	protected JsonFilePersister<T> _persister;

	// Pass the item type since Jackson cannot recognize type from generics
	// This is related to Java type erasure issue

	/**
	 * Creates a new instance of the persistence.
	 * 
	 * @param type the class type
	 */
	protected FilePersistence(Class<T> type) {
		this(type, null);
	}

	/**
	 * Creates a new instance of the persistence.
	 * 
	 * @param type      the class type
	 * @param persister (optional) a persister component that loads and saves data
	 *                  from/to flat file.
	 */
	protected FilePersistence(Class<T> type, JsonFilePersister<T> persister) {
		super(type, persister == null ? new JsonFilePersister<T>(type) : persister,
				persister == null ? new JsonFilePersister<T>(type) : persister);

		_persister = persister;
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
		_persister.configure(config);
	}
}