package org.pipservices.data.persistence;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.pipservices.commons.config.*;
import org.pipservices.commons.data.*;
import org.pipservices.commons.random.*;
import org.pipservices.commons.errors.*;
import org.pipservices.data.*;

/**
 * Abstract persistence component that stores data in memory
 * and implements a number of CRUD operations over data items with unique ids.
 * The data items must implement {@link org.pipservices.commons.data.IIdentifiable} interface.
 * <p>
 * In basic scenarios child classes shall only override <code>getPageByFilter()</code>,
 * <code>getListByFilter()</code> or <code>deleteByFilter()</code> operations with specific filter function.
 * All other operations can be used out of the box. 
 * <p>
 * In complex scenarios child classes can implement additional operations by 
 * accessing cached items via <code>this._items</code> property and calling <code>save()</code> method
 * on updates.
 * <p>
 * ### Configuration parameters ###
 * <ul>
 * <li>options:
 *     <ul>
 *     <li>max_page_size:       Maximum number of items returned in a single page (default: 100)
 * 	   </ul>
 * </ul>
 * <p>
 * ### References ###
 * <ul>
 * <li>*:logger:*:*:1.0         (optional) <a href="https://raw.githubusercontent.com/pip-services-java/pip-services-components-java/master/doc/api/org/pipservices/components/log/ILogger.html">ILogger</a> components to pass log messages
 * </ul>
 * <p>
 * ### Examples ###
 * <pre>
 * {@code
 * class MyMemoryPersistence extends IdentifiableMemoryPersistence<MyData, String> {
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
 * MyMemoryPersistence persistence = new MyMemoryPersistence(MyData.class);
 * 
 * MyData item = persistence.create("123", new MyData("1", "ABC"));
 * DataPage<MyData> mydata = persistence.getPageByFilter(
 *         "123",
 *         FilterParams.fromTuples("name", "ABC"),
 *         null, null, null);
 * System.out.println(mydata.getData().toString());          // Result: { id: "1", name: "ABC" }
 * persistence.deleteById("123", "1");
 * ...
 * }
 * </pre>
 * @see MemoryPersistence
 */
public class IdentifiableMemoryPersistence<T extends IIdentifiable<K>, K> extends MemoryPersistence<T>
		implements IConfigurable, IWriter<T, K>, IGetter<T, K>, ISetter<T> {

	private final static int _defaultMaxPageSize = 100;

	protected int _maxPageSize = _defaultMaxPageSize;

	/**
	 * Creates a new instance of the persistence.
	 * 
	 * @param type the class type
	 */
	protected IdentifiableMemoryPersistence(Class<T> type) {
		this(type, null, null);
	}

	// Pass the item type since Jackson cannot recognize type from generics
	// This is related to Java type erasure issue

	/**
	 * Creates a new instance of the persistence.
	 * 
	 * @param type   the class type
	 * @param loader (optional) a loader to load items from external datasource.
	 * @param saver  (optional) a saver to save items to external datasource.
	 */
	protected IdentifiableMemoryPersistence(Class<T> type, ILoader<T> loader, ISaver<T> saver) {
		super(type, loader, saver);
	}

	/**
	 * Configures component by passing configuration parameters.
	 * 
	 * @param config configuration parameters to be set.
	 * @throws ConfigException when configuration is wrong.
	 */
	public void configure(ConfigParams config) throws ConfigException {
		_maxPageSize = config.getAsIntegerWithDefault("options.max_page_size", _maxPageSize);
	}

	/**
	 * Checks if the component is opened.
	 * 
	 * @return true if the component has been opened and false otherwise.
	 */
	@Override
	public boolean isOpen() {
		return this._opened;
	}

	/**
	 * Gets a page of data items retrieved by a given filter and sorted according to
	 * sort parameters.
	 * 
	 * This method shall be called by a public getPageByFilter method from child
	 * class that receives FilterParams and converts them into a filter function.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param filter        (optional) a filter function to filter items
	 * @param paging        (optional) paging parameters
	 * @param sort          (optional) sorting parameters
	 * @return a data page of result by filter.
	 * @throws ApplicationException when error occured.
	 */
	protected DataPage<T> getPageByFilter(String correlationId, Predicate<T> filter, PagingParams paging,
			Comparator<T> sort) throws ApplicationException {

		synchronized (_lock) {
			Stream<T> items = this._items.stream();

			// Apply filter
			if (filter != null)
				items = items.filter(filter);

			// Extract a page
			paging = paging != null ? paging : new PagingParams();
			long skip = paging.getSkip(-1);
			long take = paging.getTake(_maxPageSize);

			Long total = null;
			if (paging.hasTotal()) {
				List<T> selectedItems = items.collect(Collectors.toList());
				total = (long) selectedItems.size();
				items = selectedItems.stream();
			}

			if (skip > 0)
				items = items.skip(skip);
			items = items.limit(take);

			// Apply sorting
			if (sort != null)
				items = items.sorted(sort);

			List<T> data = items.collect(Collectors.toList());

			_logger.trace(correlationId, "Retrieved %d of %s", data.size(), _typeName);

			return new DataPage<T>(data, total);
		}
	}

	/**
	 * Gets a page of data items retrieved by a given filter and sorted according to
	 * sort parameters.
	 * 
	 * This method shall be called by a public getPageByFilter method from child
	 * class that receives FilterParams and converts them into a filter function.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param filter        (optional) a filter function to filter items
	 * @param paging        (optional) paging parameters
	 * @param sort          (optional) sorting parameters
	 * @param select        (optional) projection parameters (not used yet)
	 * @return a data page of result by filter.
	 * @throws ApplicationException when error occured.
	 */
	protected <S> DataPage<S> getPageByFilter(String correlationId, Predicate<T> filter, PagingParams paging,
			Comparator<T> sort, Function<T, S> select) throws ApplicationException {

		DataPage<T> page = getPageByFilter(correlationId, filter, paging, sort);

		Long total = page.getTotal();
		List<S> items = page.getData().stream().map(select).collect(Collectors.toList());

		return new DataPage<S>(items, total);
	}

	/**
	 * Gets a list of data items retrieved by a given filter and sorted according to
	 * sort parameters.
	 * 
	 * This method shall be called by a public getListByFilter method from child
	 * class that receives FilterParams and converts them into a filter function.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param filter        (optional) a filter function to filter items
	 * @param sort          (optional) sorting parameters
	 * @return a data list of results by filter.
	 * @throws ApplicationException when error occured.
	 */
	protected List<T> getListByFilter(String correlationId, Predicate<T> filter, Comparator<T> sort)
			throws ApplicationException {

		synchronized (_lock) {
			Stream<T> items = this._items.stream();

			// Apply filter
			if (filter != null)
				items = items.filter(filter);

			// Apply sorting
			if (sort != null)
				items = items.sorted(sort);

			List<T> data = items.collect(Collectors.toList());

			_logger.trace(correlationId, "Retrieved %d of %s", data.size(), _typeName);

			return data;
		}
	}

	/**
	 * Gets a list of data items retrieved by a given filter and sorted according to
	 * sort parameters.
	 * 
	 * This method shall be called by a public getListByFilter method from child
	 * class that receives FilterParams and converts them into a filter function.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param filter        (optional) a filter function to filter items
	 * @param sort          (optional) sorting parameters
	 * @param select        (optional) projection parameters (not used yet)
	 * @return a data list of results by filter.
	 * @throws ApplicationException when error occured.
	 */
	protected <S> List<S> getListByFilter(String correlationId, Predicate<T> filter, Comparator<T> sort,
			Function<T, S> select) throws ApplicationException {

		List<S> items = getListByFilter(correlationId, filter, sort).stream().map(select).collect(Collectors.toList());
		return items;
	}

	/**
	 * Finds one element by id.
	 * 
	 * @param id an id of data item.
	 * @return data item.
	 */
	protected T findOne(K id) {
		Optional<T> item = _items.stream().filter((v) -> v.getId().equals(id)).findFirst();
		return item.isPresent() ? item.get() : null;
	}

	/**
	 * Finds all elements by ids.
	 * 
	 * @param ids ids of data items.
	 * @return data list of items.
	 */
	protected List<T> findAll(K[] ids) {
		List<T> result = new ArrayList<T>();
		for (K id : ids) {
			Optional<T> item = _items.stream().filter((v) -> v.getId().equals(id)).findAny();
			result.add(item.isPresent() ? item.get() : null);
		}
		return result;
	}

	/**
	 * Gets a data item by its unique id.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param id            an id of data item to be retrieved.
	 * @return data item.
	 * @throws ApplicationException when error occured.
	 */
	public T getOneById(String correlationId, K id) throws ApplicationException {
		synchronized (_lock) {
			T item = findOne(id);
			if (item != null)
				_logger.trace(correlationId, "Retrieved %s by %s", item, id);
			else
				_logger.trace(correlationId, "Cannot find %s by %s", _typeName, id);
			return item;
		}
	}

	/**
	 * Gets a list of data items retrieved by given unique ids.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param ids           ids of data items to be retrieved
	 * @return a data list.
	 * @throws ApplicationException when error occured.
	 */
	public List<T> getListByIds(String correlationId, K[] ids) throws ApplicationException {
		List<T> result = new ArrayList<T>();
		for (K oneId : ids) {
			T item = getOneById(correlationId, oneId);
			if (item != null)
				result.add(item);
		}
		return result;
	}

	/**
	 * Gets a random item from items that match to a given filter.
	 * 
	 * This method shall be called by a public getOneRandom method from child class
	 * that receives FilterParams and converts them into a filter function.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @return a random item.
	 * @throws ApplicationException when error occured.
	 */
	public T getOneRandom(String correlationId) throws ApplicationException {
		synchronized (_lock) {
			if (_items.size() == 0)
				return null;

			T item = _items.get(RandomInteger.nextInteger(_items.size()));

			if (item != null)
				_logger.trace(correlationId, "Retrieved a random %s", _typeName);
			else
				_logger.trace(correlationId, "Nothing to return as random %s", _typeName);

			return item;
		}
	}

	/**
	 * Creates a data item.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param item          an item to be created.
	 * @return created item.
	 * @throws ApplicationException when error occured.
	 */
	public T create(String correlationId, T item) throws ApplicationException {
		// Assign unique string key
		if (item instanceof IStringIdentifiable && item.getId() == null)
			((IStringIdentifiable) item).setId(IdGenerator.nextLong());

		synchronized (_lock) {
			_items.add(item);

			_logger.trace(correlationId, "Created %s", item);

			save(correlationId);
		}

		return item;
	}

	/**
	 * Updates a data item.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param newItem       an item to be updated.
	 * @return updated item.
	 * @throws ApplicationException when error occured.
	 */
	public T update(String correlationId, T newItem) throws ApplicationException {
		synchronized (_lock) {
			T oldItem = findOne(newItem.getId());
			if (oldItem == null)
				return null;

			int index = _items.indexOf(oldItem);
			if (index < 0)
				return null;

			_items.set(index, newItem);

			_logger.trace(correlationId, "Updated %s", newItem);

			save(correlationId);

			return newItem;
		}
	}

	/**
	 * Sets a data item. If the data item exists it updates it, otherwise it create
	 * a new data item.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param newItem       a item to be set.
	 * @return updated item.
	 * @throws ApplicationException when error occured.
	 */
	public T set(String correlationId, T newItem) throws ApplicationException {
		// Assign unique string key
		if (newItem instanceof IStringIdentifiable && newItem.getId() == null)
			((IStringIdentifiable) newItem).setId(IdGenerator.nextLong());

		synchronized (_lock) {
			T oldItem = findOne(newItem.getId());

			if (oldItem == null)
				_items.add(newItem);
			else {
				int index = _items.indexOf(oldItem);
				if (index < 0)
					_items.add(newItem);
				else
					_items.set(index, newItem);
			}

			_logger.trace(correlationId, "Set %s", newItem);

			save(correlationId);

			return newItem;
		}
	}

	/**
	 * Deleted a data item by it's unique id.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param id            an id of the item to be deleted
	 * @return deleted item.
	 * @throws ApplicationException when error occured.
	 */
	public T deleteById(String correlationId, K id) throws ApplicationException {
		synchronized (_lock) {
			T item = findOne(id);
			if (item == null)
				return null;

			int index = _items.indexOf(item);
			if (index < 0)
				return null;

			_items.remove(index);

			_logger.trace(correlationId, "Deleted %s", item);

			save(correlationId);

			return item;
		}
	}

	/**
	 * Deletes data items that match to a given filter.
	 * 
	 * This method shall be called by a public deleteByFilter method from child
	 * class that receives FilterParams and converts them into a filter function.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param filter        (optional) a filter function to filter items.
	 * @throws ApplicationException when error occured.
	 */
	public void deleteByFilter(String correlationId, Predicate<T> filter) throws ApplicationException {
		int deleted = 0;
		synchronized (_lock) {
			Stream<T> items = _items.stream();

			if (filter != null) {
				items = items.filter(filter);
				List<T> data = items.collect(Collectors.toList());
				for (T item : data) {
					_items.remove(item);
					deleted++;
				}
				_logger.trace(correlationId, "Deleted %d items", deleted);
			}
			if (deleted > 0) {
				save(correlationId);
			}
		}
	}

	/**
	 * Deletes multiple data items by their unique ids.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @param ids           ids of data items to be deleted.
	 * @throws ApplicationException when error occured.
	 */
	public void deleteByIds(String correlationId, K[] ids) throws ApplicationException {
		List<K> idsList = Arrays.asList(ids);

		int deleted = 0;
		synchronized (_lock) {
			Stream<T> items = _items.stream();

			items = items.filter(x -> idsList.contains(x.getId()));
			List<T> data = items.collect(Collectors.toList());
			for (T item : data) {
				_items.remove(item);
				deleted++;
			}
			_logger.trace(correlationId, "Deleted %d items", deleted);

			if (deleted > 0) {
				save(correlationId);
			}
		}
	}
}