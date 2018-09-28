package org.pipservices.data.persistence;

import java.util.*;

import org.pipservices.commons.errors.*;
import org.pipservices.commons.refer.*;
import org.pipservices.commons.run.*;
import org.pipservices.components.log.CompositeLogger;
import org.pipservices.data.*;

/**
 * Abstract persistence component that stores data in memory.
 * 
 * This is the most basic persistence component that is only
 * able to store data items of any type. Specific CRUD operations
 * over the data items must be implemented in child classes by
 * accessing this._items property and calling save() method.
 * 
 * The component supports loading and saving items from another data source.
 * That allows to use it as a base class for file and other types
 * of persistence components that cache all data in memory. 
 * 
 * ### References ###
 * 
 * - *:logger:*:*:1.0         (optional) ILogger components to pass log messages
 * 
 * ### Example ###
 * 
 * class MyMemoryPersistence extends MemoryPersistence {
 *   public MyData getByName(String correlationId, String name) {
 *     MyData item = find(name); // search method
 *     ...
 *     return item;
 *   }); 
 * 
 *   public MyData set(String correlatonId, MyData item) {
 *     this._items = filter(); // filter method
 *     ...
 *     this._items.add(item);
 *     this.save(correlationId);
 *   }
 * 
 * }
 * 
 * MyMemoryPersistence persistence = new MyMemoryPersistence();
 * 
 * persistence.set("123", new MyData("ABC"));
 * System.out.println(persistence.getByName("123", "ABC")).toString(); // Result: { name: "ABC" }
 */
public abstract class MemoryPersistence<T> implements IReferenceable, IOpenable, ICleanable {
	protected Class<?> _type;
	protected String _typeName;

	protected CompositeLogger _logger = new CompositeLogger();

	protected List<T> _items = new ArrayList<T>();
	protected ILoader<T> _loader;
	protected ISaver<T> _saver;
	protected boolean _opened = false;
	protected Object _lock = new Object();

	/**
	 * Creates a new instance of the persistence.
	 * 
	 * @param type the class type
	 */
	protected MemoryPersistence(Class<T> type) {
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
	protected MemoryPersistence(Class<T> type, ILoader<T> loader, ISaver<T> saver) {
		_type = type;
		_typeName = type.getName();
		_loader = loader;
		_saver = saver;
	}

	/**
	 * Sets references to dependent components.
	 * 
	 * @param references references to locate the component dependencies.
	 * @throws ReferenceException when no found references.
	 */
	public void setReferences(IReferences references) throws ReferenceException {
		_logger.setReferences(references);
	}

	/**
	 * Checks if the component is opened.
	 * 
	 * @return true if the component has been opened and false otherwise.
	 */
	public boolean isOpen() {
		return _opened;
	}

	/**
	 * Opens the component.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @throws ApplicationException when error occured.
	 */
	public void open(String correlationId) throws ApplicationException {
		load(correlationId);
		_opened = true;
	}

	/**
	 * Closes component and frees used resources.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @throws ApplicationException when error occured.
	 */
	public void close(String correlationId) throws ApplicationException {
		save(correlationId);
		_opened = false;
	}

	private void load(String correlationId) throws ApplicationException {
		if (_loader != null) {
			synchronized (_lock) {
				_items = _loader.load(correlationId);
				_logger.trace(correlationId, "Loaded %d of %s", _items.size(), _typeName);
			}
		}
	}

	/**
	 * Saves items to external data source using configured saver component.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @throws ApplicationException when error occured.
	 */
	public void save(String correlationId) throws ApplicationException {
		if (_saver != null) {
			synchronized (_lock) {
				_saver.save(correlationId, _items);
				_logger.trace(correlationId, "Saved %d of %s", _items.size(), _typeName);
			}
		}
	}

	/**
	 * Clears component state.
	 * 
	 * @param correlationId (optional) transaction id to trace execution through
	 *                      call chain.
	 * @throws ApplicationException when error occured.
	 */
	public void clear(String correlationId) throws ApplicationException {
		synchronized (_lock) {
			_items = new ArrayList<T>();
			_logger.trace(correlationId, "Cleared %s", _typeName);
			save(correlationId);
		}
	}

}