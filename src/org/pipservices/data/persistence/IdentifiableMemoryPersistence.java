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
 * options:
 *     max_page_size:       Maximum number of items returned in a single page (default: 100)
 * 
 * ### References ###
 * 
 * - *:logger:*:*:1.0         (optional) ILogger components to pass log messages
 *  <p>
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

	@Override
	public boolean isOpen() {
		return this._opened;
	}

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

	protected <S> DataPage<S> getPageByFilter(String correlationId, Predicate<T> filter, PagingParams paging,
			Comparator<T> sort, Function<T, S> select) throws ApplicationException {

		DataPage<T> page = getPageByFilter(correlationId, filter, paging, sort);

		Long total = page.getTotal();
		List<S> items = page.getData().stream().map(select).collect(Collectors.toList());

		return new DataPage<S>(items, total);
	}

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

	protected <S> List<S> getListByFilter(String correlationId, Predicate<T> filter, Comparator<T> sort,
			Function<T, S> select) throws ApplicationException {

		List<S> items = getListByFilter(correlationId, filter, sort).stream().map(select).collect(Collectors.toList());
		return items;
	}

	protected T findOne(K id) {
		Optional<T> item = _items.stream().filter((v) -> v.getId().equals(id)).findFirst();
		return item.isPresent() ? item.get() : null;
	}

	protected List<T> findAll(K[] ids) {
		List<T> result = new ArrayList<T>();
		for (K id : ids) {
			Optional<T> item = _items.stream().filter((v) -> v.getId().equals(id)).findAny();
			result.add(item.isPresent() ? item.get() : null);
		}
		return result;
	}

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

	public List<T> getListByIds(String correlationId, K[] id) throws ApplicationException {
		List<T> result = new ArrayList<T>();
		for (K oneId : id) {
			T item = getOneById(correlationId, oneId);
			if (item != null)
				result.add(item);
		}
		return result;
	}

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